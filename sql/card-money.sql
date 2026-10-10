-- Stop Spring Boot first. Preserve existing rows; reject ambiguous data.
BEGIN;
LOCK TABLE public.card IN ACCESS EXCLUSIVE MODE;
-- Inspection is part of this reusable script.
SELECT column_name, data_type, numeric_precision, numeric_scale, is_nullable
FROM information_schema.columns
WHERE table_schema = 'public' AND table_name = 'card'
  AND column_name IN ('balance', 'owner_id');
DO $$
    BEGIN
        IF EXISTS (SELECT 1 FROM public.card
                   WHERE balance IS NULL OR owner_id IS NULL
                      OR balance::text IN ('NaN', 'Infinity', '-Infinity')) THEN
            RAISE EXCEPTION 'Review null/non-finite balance or missing card owner';
        END IF;
        IF EXISTS (SELECT 1 FROM public.card
                   WHERE balance < 0 OR balance::numeric > 99999999999999999.99
                      OR balance::numeric <> round(balance::numeric, 2)) THEN
            RAISE EXCEPTION 'Review negative, oversized or fractional-cent balances';
        END IF;
        IF EXISTS (SELECT 1 FROM public.card
                   GROUP BY owner_id HAVING count(*) > 1) THEN
            RAISE EXCEPTION 'Review duplicate cards for an owner';
        END IF;
    END
$$;
ALTER TABLE public.card
    ALTER COLUMN balance TYPE numeric(19,2) USING balance::numeric(19,2),
    ALTER COLUMN balance SET NOT NULL,
    ALTER COLUMN owner_id SET NOT NULL;

-- Reuse an existing single-column owner uniqueness constraint if present.
DO $$
DECLARE owner_column smallint;
BEGIN
    SELECT attnum INTO STRICT owner_column FROM pg_attribute
            WHERE attrelid = 'public.card'::regclass
            AND attname = 'owner_id' AND NOT attisdropped;
        IF NOT EXISTS (SELECT 1 FROM pg_constraint
WHERE conrelid = 'public.card'::regclass AND contype = 'u'
AND conkey = ARRAY[owner_column]) THEN
        ALTER TABLE public.card ADD CONSTRAINT uk_card_owner UNIQUE (owner_id);
        END IF;
        END
$$;
ALTER TABLE public.card DROP CONSTRAINT IF EXISTS ck_card_balance;
ALTER TABLE public.card ADD CONSTRAINT ck_card_balance
    CHECK (balance >= 0 AND balance <= 99999999999999999.99);
-- Match the owner filters and newest-first history order.
CREATE INDEX IF NOT EXISTS ix_tx_owner_time
    ON public."transaction" (owner_id, created_at DESC, tx_id DESC);
CREATE INDEX IF NOT EXISTS ix_tx_owner_card_time
    ON public."transaction" (owner_id, card_id, created_at DESC, tx_id DESC);
CREATE INDEX IF NOT EXISTS ix_tx_owner_account_time
    ON public."transaction" (owner_id, account_id, created_at DESC, tx_id DESC);
COMMIT;
-- Verification: numeric(19,2), NOT NULL, and ownership/balance constraints.
SELECT column_name, data_type, numeric_precision, numeric_scale, is_nullable
FROM information_schema.columns
WHERE table_schema = 'public' AND table_name = 'card'
  AND column_name IN ('balance', 'owner_id');
SELECT conname, pg_get_constraintdef(oid) AS definition
FROM pg_constraint WHERE conrelid = 'public.card'::regclass;
SELECT indexname, indexdef FROM pg_indexes
WHERE schemaname = 'public' AND tablename = 'transaction'
  AND indexname LIKE 'ix_tx_owner%';