ALTER TABLE tenant_configurations
ALTER COLUMN creator TYPE TEXT
USING creator::TEXT;