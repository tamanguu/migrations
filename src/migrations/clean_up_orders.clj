(ns migrations.clean-up-orders
  (:require [taoensso.timbre :refer [info]]
            [conman.core :as conman]
            [environ.core :refer [env]]
            [clojure.data.json :as json]))

;; region Database setup

(def connection (conman/connect! {:jdbc-url (env :database-url)}))
(conman/bind-connection connection "sql/fleets.sql")

;; endregion

;; region Private database helpers

(defn- psql-json-array
  "@return A PostgreSQL array of JSON objects converted from the Clojure array of hash-maps"
  [datum]
  (str "{" (clojure.string/join ","
                                (remove nil?
                                        (map #(pr-str (json/write-str %)) datum)))
       "}"))

;; endregion

;; region Migration functions

(defn migrate-up
  "Apply the fix-orders migration."
  [config]
  (info "Performing migration v2.0 -> v3.0 with configuration" config)
  (when (not= 1 (set-version! {:version "3.0"}))
    (throw (Throwable. "Failed to migrate table schema version in up migration")))
  (let [fleets (get-fleets)]
    (doseq [fleet fleets]
      (let [orders      (->> fleet
                             :orders
                             .getArray
                             (map #(json/read-str % :key-fn keyword)))
            new-orders  (->> orders
                             (map #(merge % {:is-nonsense? (not (nil? (re-find #"Palpatine" (:objective %))))}))
                             (map #(clojure.set/rename-keys % {:objective :purpose :priority :ignore-me}))
                             (map #(merge % {:priority "HIGHEST"})))
            orders-psql (psql-json-array new-orders)]
        (set-fleet-orders! {:orders orders-psql :fleetid (:fleetid fleet)}))))
  (conman/disconnect! connection))

(defn migrate-down
  "Rollback the fix-orders migration."
  [config]
  (info "Performing migration v3.0 -> v2.0 with configuration" config)
  (when (not= 1 (set-version! {:version "2.0"}))
    (throw (Throwable. "Failed to migrate table schema version in down migration")))
  (let [fleets (get-fleets)]
    (doseq [fleet fleets]
      (let [orders      (->> fleet
                             :orders
                             .getArray
                             (map #(json/read-str % :key-fn keyword)))
            old-orders  (->> orders
                             (map #(hash-map :objective (:purpose %) :priority (:ignore-me %))))
            orders-psql (psql-json-array old-orders)]
        (set-fleet-orders! {:orders orders-psql :fleetid (:fleetid fleet)}))))
  (conman/disconnect! connection))

;; endregion
