-- Hour 6: extend the existing string-enum check to allow CONVERSION.
-- Run with Spring Boot stopped, against the existing hour-five schema.
BEGIN;
--The table lock prevents concurrent writes while its checks change--
LOCK TABLE public."transaction" IN ACCESS EXCLUSIVE MODE;
DO $$
DECLARE
item record;
type_column smallint;
BEGIN
    SELECT attnum INTO STRICT type_column
        FROM pg_attribute
    WHERE attrelid = 'public."transaction"'::regclass
        AND attname = 'type' AND NOT attisdropped;
    IF EXISTS (

        SELECT 1 FROM public."transaction"
        WHERE type IS NOT NULL
        AND type NOT IN ('WITHDRAW', 'DEPOSIT', 'DEBIT',
        'CREDIT', 'CONVERSION')

        ) THEN
            RAISE EXCEPTION 'Unexpected transaction type; inspect existing data';
    END IF;

    FOR item IN
        SELECT conname, conkey
        FROM pg_constraint
        WHERE conrelid = 'public."transaction"'::regclass
        AND contype = 'c' AND type_column = ANY(conkey)
        LOOP
    IF item.conname NOT IN ('transaction_type_check',
    'ck_transaction_type')
    OR cardinality(item.conkey) <> 1 THEN
    RAISE EXCEPTION 'Review custom type constraint: %', item.conname;
    END IF;
    EXECUTE format('ALTER TABLE public."transaction" DROP CONSTRAINT %I',
    item.conname);
    END LOOP;
    END
$$;
ALTER TABLE public."transaction"
    ADD CONSTRAINT ck_transaction_type
        CHECK (type IN ('WITHDRAW', 'DEPOSIT', 'DEBIT', 'CREDIT', 'CONVERSION'));
COMMIT;
SELECT conname, pg_get_constraintdef(oid) AS definition
FROM pg_constraint
WHERE conrelid = 'public."transaction"'::regclass AND contype = 'c';