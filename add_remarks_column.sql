-- Add remarks column to invoices table if it doesn't exist
ALTER TABLE invoices ADD COLUMN IF NOT EXISTS remarks VARCHAR(500);

-- Update any existing invoices to have empty remarks
UPDATE invoices SET remarks = '' WHERE remarks IS NULL;


