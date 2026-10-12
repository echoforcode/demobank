BEGIN;

-- Prevent data changes between checking and altering the tables.
LOCK TABLE account, "transaction" IN ACCESS EXCLUSIVE MODE;

-- Check existing values before converting them.
DO $$
    BEGIN
        IF EXISTS (
            SELECT 1
            FROM account
            WHERE balance IS NULL
               OR balance < 0
               OR balance::numeric <> round(balance::numeric, 2)
               OR abs(balance::numeric) >= 100000000000000000
        ) THEN
            RAISE EXCEPTION
                'Account balance check failed: null, negative, extra decimals, or too large.';
        END IF;

        IF EXISTS (
            SELECT 1
            FROM "transaction"
            WHERE amount IS NOT NULL
              AND (
                amount::numeric <> round(amount::numeric, 2)
                    OR abs(amount::numeric) >= 100000000000000000
                )
        ) THEN
            RAISE EXCEPTION
                'Transaction amount check failed: extra decimals or too large.';
        END IF;

        IF EXISTS (
            SELECT 1
            FROM "transaction"
            WHERE tx_fee IS NOT NULL
              AND (
                tx_fee::numeric <> round(tx_fee::numeric, 2)
                    OR abs(tx_fee::numeric) >= 100000000000000000
                )
        ) THEN
            RAISE EXCEPTION
                'Transaction fee check failed: extra decimals or too large.';
        END IF;
    END
$$;

-- These statements run only if all checks pass.
ALTER TABLE account
    ALTER COLUMN balance TYPE numeric(19,2)
        USING balance::numeric(19,2),
    ALTER COLUMN balance SET NOT NULL;

ALTER TABLE "transaction"
    ALTER COLUMN amount TYPE numeric(19,2)
        USING amount::numeric(19,2),
    ALTER COLUMN tx_fee TYPE numeric(19,2)
        USING tx_fee::numeric(19,2);

COMMIT;

-- Show the resulting column types.
SELECT table_name, column_name, data_type,
       numeric_precision, numeric_scale, is_nullable
FROM information_schema.columns
WHERE table_schema = 'public'
  AND (
    (table_name = 'account' AND column_name = 'balance')
        OR
    (table_name = 'transaction'
        AND column_name IN ('amount', 'tx_fee'))
    )
ORDER BY table_name, column_name;