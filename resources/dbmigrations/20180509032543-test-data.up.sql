-- region Add test data

INSERT INTO Captains (name)
  VALUES ('James T. Kirk');
INSERT INTO Starships (captainID, name)
  VALUES ((SELECT captainID FROM Captains WHERE name IS NOT NULL), 'USS Enterprise NCC1701'),
         ((SELECT captainID FROM Captains WHERE name IS NOT NULL), 'USS Enterprise NCC1701-A');
INSERT INTO Fleets (name, orders, affiliation)
  VALUES ('Starfleet', ARRAY['{}'::jsonb], 'UFP');
INSERT INTO ShipsInFleet (starshipID, fleetID)
  VALUES ((SELECT starshipID FROM Starships WHERE name LIKE '%NCC1701'), (SELECT fleetID FROM Fleets WHERE name IS NOT NULL)),
         ((SELECT starshipID FROM Starships WHERE name LIKE '%NCC1701-A'), (SELECT fleetID FROM Fleets WHERE name IS NOT NULL));
INSERT INTO Captains (name)
  VALUES ('Jean-Luc Picard');

-- endregion
