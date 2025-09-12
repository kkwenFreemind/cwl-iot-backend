-- Migration: Add is_deleted column to sys_dict_item table
-- Date: 2025-09-12
-- Description: Add missing is_deleted column for soft delete functionality in dictionary items

-- Add is_deleted column to sys_dict_item table
ALTER TABLE sys_dict_item 
ADD COLUMN is_deleted SMALLINT DEFAULT 0;

-- Add comment for the new column
COMMENT ON COLUMN sys_dict_item.is_deleted IS '邏輯刪除標識(1-已刪除 0-未刪除) (Logical Delete Flag: 1-Deleted, 0-Not Deleted)';

-- Update existing records to ensure they are not marked as deleted
UPDATE sys_dict_item SET is_deleted = 0 WHERE is_deleted IS NULL;

-- Make the column NOT NULL now that all existing records have a value
ALTER TABLE sys_dict_item 
ALTER COLUMN is_deleted SET NOT NULL;

-- Create index for better query performance on is_deleted column
CREATE INDEX idx_sys_dict_item_is_deleted ON sys_dict_item(is_deleted);

-- Verify the changes
SELECT column_name, data_type, is_nullable, column_default 
FROM information_schema.columns 
WHERE table_name = 'sys_dict_item' 
ORDER BY ordinal_position;