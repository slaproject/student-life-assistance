-- Simple migration to add transaction_type column
-- This version is compatible with Spring Boot's sql.init system
-- For development only - run once, then comment out or remove from application-dev.properties

-- Add transaction_type column with default value 'EXPENSE' for existing records
ALTER TABLE expenses 
ADD COLUMN IF NOT EXISTS transaction_type VARCHAR(20) NOT NULL DEFAULT 'EXPENSE';

-- Add check constraint to ensure only valid transaction types
-- Note: IF NOT EXISTS is not supported for constraints in older PostgreSQL versions
-- If this fails, it means the constraint already exists, which is fine
ALTER TABLE expenses 
ADD CONSTRAINT check_transaction_type 
CHECK (transaction_type IN ('EXPENSE', 'INCOME'));

-- Create indexes for faster filtering
CREATE INDEX IF NOT EXISTS idx_expenses_transaction_type 
ON expenses(transaction_type);

CREATE INDEX IF NOT EXISTS idx_expenses_user_transaction 
ON expenses(user_id, transaction_type);
