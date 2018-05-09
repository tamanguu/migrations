-- region Public functions

-- :name get-version :? :1
-- :doc Return the schema version as a string
SELECT version FROM FleetSchema;

-- :name get-captains :? :*
-- :doc Return all captains
SELECT * FROM Captains;

-- :name get-starships :? :*
-- :doc Return all starships
SELECT *, (SELECT Captains.name FROM Captains WHERE Starships.captainID = Captains.captainID) AS captain FROM Starships;

-- :name get-fleets :? :*
-- :doc Return all fleets
SELECT *, ARRAY(SELECT starshipID FROM ShipsInFleet WHERE ShipsInFleet.fleetID = Fleets.fleetID) AS ships FROM Fleets;

-- endregion
