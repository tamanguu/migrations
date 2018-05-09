-- region Add entity object data type

-- Update DB schema version
DELETE FROM FleetSchema;
INSERT INTO FleetSchema (version) VALUES ('2.0');

-- New object type definition
DROP TYPE IF EXISTS ObjectType;
CREATE TYPE ObjectType AS ENUM ('captain', 'starship', 'fleet');

-- Add object type to all tables with appropriate values
ALTER TABLE Captains ADD COLUMN IF NOT EXISTS "object" ObjectType NOT NULL DEFAULT 'captain';
ALTER TABLE Starships ADD COLUMN IF NOT EXISTS "object" ObjectType NOT NULL DEFAULT 'starship';
ALTER TABLE Fleets ADD COLUMN IF NOT EXISTS "object" ObjectType NOT NULL DEFAULT 'fleet';

-- endregion
