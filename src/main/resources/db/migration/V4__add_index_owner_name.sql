-- Task E, step 4: copy this file to src/main/resources/db/migration/ and restart the application
CREATE INDEX idx_accounts_owner_name ON accounts (owner_name);
