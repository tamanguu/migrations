-- region Initial DB setup

-- Return a random UUID datum
CREATE OR REPLACE FUNCTION create_uuid()
  RETURNS UUID
  AS
  $BODY$
    BEGIN
      RETURN (md5(random()::text || clock_timestamp()::text)::uuid);
    END;
  $BODY$
  LANGUAGE plpgsql;

-- Enumeration
DROP TYPE IF EXISTS ShipClass;
CREATE TYPE ShipClass AS ENUM ('shuttle', 'constitution', 'defiant', 'galaxy');

-- Table with a single entry specifying the schema version
CREATE TABLE IF NOT EXISTS FleetSchema (version VARCHAR(3) NOT NULL);
DELETE FROM FleetSchema;
INSERT INTO FleetSchema (version) VALUES ('1.0');

-- Table for Captains
CREATE TABLE IF NOT EXISTS Captains
(captainID UUID NOT NULL PRIMARY KEY DEFAULT create_uuid(),
  creationDate TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  name TEXT);

-- Table for Starships
CREATE TABLE IF NOT EXISTS Starships
(starshipID UUID NOT NULL PRIMARY KEY DEFAULT create_uuid(),
  creationDate TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  captainID UUID NOT NULL REFERENCES Captains(captainID) ON DELETE CASCADE,
  name TEXT);

-- Table for Fleets
CREATE TABLE IF NOT EXISTS Fleets
(fleetID UUID NOT NULL PRIMARY KEY DEFAULT create_uuid(),
  creationDate TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  name TEXT,
  orders JSONB[],
  affiliation TEXT);

-- Ships within a fleet
CREATE TABLE IF NOT EXISTS ShipsInFleet
(relationID UUID NOT NULL PRIMARY KEY DEFAULT create_uuid(),
  starshipID UUID NOT NULL REFERENCES Starships(starshipID) ON DELETE CASCADE,
  fleetID UUID NOT NULL REFERENCES Fleets(fleetID) ON DELETE CASCADE);

-- endregion
