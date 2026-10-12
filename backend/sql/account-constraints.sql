-- Run against the same local bank_db used by Spring Boot, with Java stopped.
-- No rows or tables are deleted. Existing invalid data causes a rollback.
\set ON_ERROR_STOP on
BEGIN;

DO $$
    BEGIN
        IF EXISTS (SELECT 1 FROM account
                   WHERE code IS NULL OR owner_id IS NULL
                      OR code NOT IN ('USD','EUR','GBP','JPY','NGN','INR')) THEN
            RAISE EXCEPTION 'Review invalid account codes/owners before proceeding';
        END IF;
        IF EXISTS (SELECT 1 FROM account GROUP BY owner_id, code
                   HAVING count(*) > 1) THEN
            RAISE EXCEPTION 'Review duplicate owner/currency accounts first';
        END IF;
        IF NOT EXISTS (SELECT 1 FROM pg_constraint
                       WHERE conrelid = 'account'::regclass
                         AND conname = 'uk_account_owner_code') THEN
            ALTER TABLE account ADD CONSTRAINT uk_account_owner_code
                UNIQUE (owner_id, code);
        END IF;
    END $$;

ALTER TABLE account ALTER COLUMN code SET NOT NULL;
ALTER TABLE account ALTER COLUMN owner_id SET NOT NULL;
COMMIT;