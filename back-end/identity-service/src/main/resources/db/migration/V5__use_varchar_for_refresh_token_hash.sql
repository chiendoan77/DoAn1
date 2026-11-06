ALTER TABLE refresh_tokens
    MODIFY COLUMN token_hash VARCHAR(64) NOT NULL;
