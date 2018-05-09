# migrations

This project contains recommendations and guidelines for performing
database migrations within the tamanguu.contacts project. A test
database is set up and different migrations scenarios are presented.

This document is recommended reading for all developers who work with
the backend and mandatory reading for all developers who work with the
database in the backend.

The project does not provide any executables and is mainly operated
through test runs.

## Installation

The following prerequisites MUST be installed and available:
* [Java JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
  version 1.8,
* [Leiningen](https://leiningen.org) package manager version 2.6 or
  higher,
* [PostgreSQL](https://www.postgresql.org) version 9.6 (recommended)
  or 10.1.

Prior to using the project the database needs to be set up (first time
only):
```sql
CREATE USER admin WITH PASSWORD 'admin';
CREATE DATABASE migrations OWNER admin;
```

## Usage

This git repository sets up a series of scenarios with different
database versions. The different scenarios add the following
behaviors:
* Schema version "1.0": Basic use case, elementary table setup.
* Schema version "2.0": Add an object column with proper default value
  to all entities in all tables.

### Starting with schema version "1.0"

Check out the first scenario with database schema version "1.0", apply
the initial migrations to set up the database and run all tests:

    $ git checkout v1.0
    $ lein reset-db
    $ lein eftest

The tests verify the content of the database at schema version 1.0 and
fail for any other version.

### Migrations for schema version "2.0"

With database schema version "2.0" a new enumeration for objects and a
new column with the object type is added to all tables. To add the
migrations and run all tests use:

    $ git checkout v2.0
    $ lein migrate-db
    $ lein eftest

In order to rollback the changes and rerun the tests use:

    $ lein rollback-db
    $ lein eftest

Note that the tests in this scenario work with both schema version
"1.0" and "2.0".

### Migrations for schema version "3.0"

Schema version "3.0" changes a JSON datum by reformatting it. This is
done using encapsulated Clojure code.

Note that the rollback action in the above scenario left the database
at schema version "1.0". Now check out the migrations and tests for
schema version "3.0" which do no longer support version "1.0":

    $ git checkout v3.0
    $ lein eftest

The tests should now fail.

After applying the migrations the tests will again succeed:

    $ lein migrate-db
    $ lein eftest

When rolling back and going back to schema version "2.0" the tests at
"3.0" will succeed since they are compatible with both version "2.0"
and "3.0":

    $ lein rollback-db
    $ git checkout v2.0
    $ lein migrate-db
    $ git checkout v3.0
    $ lein eftest

## Legal matter

The following licensing conditions and notices apply.

### License

Copyright © since 2018 Tamanguu GmbH & Co KG

The project uses a commercial, proprietary license, see `LICENSE.md`
distributed together with this project.
