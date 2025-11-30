-- Migration to add transaction_type column to expenses table
-- This allows tracking both income and expenses in the same table
-- This script is idempotent and safe to run multiple times

-- Add transaction_type column with default value 'EXPENSE' for existing records (if not exists)
-- Add transaction_type column with default value 'EXPENSE' for existing records (if not exists)
ALTER TABLE expenses ADD COLUMN IF NOT EXISTS transaction_type VARCHAR(20) NOT NULL DEFAULT 'EXPENSE';

-- Add check constraint to ensure only valid transaction types
-- Drop first to ensure idempotency without using DO block
ALTER TABLE expenses DROP CONSTRAINT IF EXISTS check_transaction_type;
ALTER TABLE expenses ADD CONSTRAINT check_transaction_type CHECK (transaction_type IN ('EXPENSE', 'INCOME'));

-- Create index on transaction_type for faster filtering (if not exists)
CREATE INDEX IF NOT EXISTS idx_expenses_transaction_type 
ON expenses(transaction_type);

-- Create index on user_id and transaction_type combination for common queries (if not exists)
CREATE INDEX IF NOT EXISTS idx_expenses_user_transaction 
ON expenses(user_id, transaction_type);
