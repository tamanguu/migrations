-- region Remove entity object and revert schema version

-- Revert schema version
DELETE FROM FleetSchema;
INSERT INTO FleetSchema (version) VALUES ('1.0');

-- Drop entity object and enumeration type
ALTER TABLE Captains DROP COLUMN IF EXISTS "object" CASCADE;
ALTER TABLE Starships DROP COLUMN IF EXISTS "object" CASCADE;
ALTER TABLE Fleets DROP COLUMN IF EXISTS "object" CASCADE;
DROP TYPE IF EXISTS ObjectType;

-- endregion
