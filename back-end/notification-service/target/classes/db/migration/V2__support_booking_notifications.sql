ALTER TABLE notifications
    MODIFY COLUMN package_id BINARY(16) NULL,
    ADD COLUMN related_id BINARY(16) NULL,
    ADD COLUMN related_type VARCHAR(16) NULL;

UPDATE notifications
SET related_id = package_id, related_type = 'PACKAGE'
WHERE related_id IS NULL;
