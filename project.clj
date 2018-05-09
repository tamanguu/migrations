(defproject migrations "1.0.0-SNAPSHOT"

  ;; Usage:
  ;; ------
  ;;
  ;; The following invocations are primarily used:
  ;; - `lein reset-db`:    Reset the database and apply all pending migrations.
  ;; - `lein migrate-db`:  Apply migratus migrate command.
  ;; - `lein rollback-db`: Apply migratus rollback command.
  ;; - `lein eftest`:      Run all test cases (depends on database version).
  ;;
  ;; Please see the `README.md` file for further information.

  ;; Basic project description and licensing terms
  :description "Test project to demonstrate database migration scenarios"
  :url "http://service.tamanguu.com"
  :license {:name "Commercial, proprietary license"
            :url  "http://www.tamanguu.de/legal/license.html"}

  ;; Dependencies, please reload the entire environment after changes here
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [conman "0.7.5"]
                 [org.postgresql/postgresql "42.2.2"]
                 [com.taoensso/timbre "4.10.0"]
                 [migratus "1.0.6"]
                 [com.fzakaria/slf4j-timbre "0.3.2"]
                 [environ "1.1.0"]
                 [eftest "0.5.1"]]
  :main ^:skip-aot migrations.core

  ;; File paths
  :target-path "target/%s"
  :source-paths ["src"]
  :test-paths ["test"]

  ;; Lein plugins and their configurations
  :plugins [[migratus-lein "0.5.7"]
            [lein-environ "1.1.0"]
            [lein-eftest "0.5.1"]]
  :env {:database-url "jdbc:postgresql://localhost/migrations?user=admin&password=admin"}
  :migratus {:store         :database
             :migration-dir "dbmigrations"
             :db            {:subprotocol "postgresql"
                             :subname     "//localhost/migrations"
                             :user        "admin"
                             :password    "admin"}}
  :eftest {:multithread? false}

  ;; Auxiliary command aliases
  :aliases {"reset-db"    ["migratus" "reset"]
            "migrate-db"  ["migratus" "migrate"]
            "rollback-db" ["migratus" "rollback"]})

