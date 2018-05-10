(ns migrations.core-test
  (:require [clojure.test :refer :all]
            [environ.core :refer [env]]
            [taoensso.timbre :as timbre]
            [conman.core :as conman]
            [clojure.data.json :as json]))

;; region Database connection and fixtures

(timbre/set-level! :info)
(timbre/merge-config! {:ns-blacklist [] #_["*com.zaxxer.hikari.*"]})
(def connection (conman/connect! {:jdbc-url (env :database-url)}))
(conman/bind-connection connection "sql/fleets.sql")

;; endregion

;; region Test cases

(deftest db-version-test
  (testing "compatible database version"
    (is (let [db-version (:version (get-version))]
          (or (= "2.0" db-version)
              (= "3.0" db-version)))))) ; Support for "1.0" has been dropped

(deftest content-test
  (let [db-version (:version (get-version))]

    (testing "captains table"
      (let [captains (get-captains)
            jim      (->> captains
                          (filter #(= "James T. Kirk" (:name %)))
                          first)]
        (is (= 2 (count captains)))
        (is (not (nil? jim)))

        (cond
          (contains? #{"2.0" "3.0"} db-version) (do
                                                  (is (= 4 (count jim)))
                                                  (is (= "captain" (:object jim))))
          :else (is false))))

    (testing "starships table"
      (let [ships (get-starships)
            tos   (->> ships
                       (filter #(= "USS Enterprise NCC1701" (:name %)))
                       first)]
        (is (= 2 (count ships)))
        (is (not (nil? tos)))

        (cond
          (contains? #{"2.0" "3.0"} db-version) (do
                                                  (is (= 6 (count tos)))
                                                  (is (= "James T. Kirk" (:captain tos)))
                                                  (is (= "starship" (:object tos))))
          :else (is false))))

    (testing "fleets table"
      (let [fleets    (get-fleets)
            starfleet (first fleets)
            ships     (->> starfleet
                           :ships
                           .getArray
                           (map str)
                           vec)
            orders    (->> starfleet
                           :orders
                           .getArray
                           (map #(json/read-str % :key-fn keyword))
                           vec)]
        (is (= 1 (count fleets)))
        (is (= 2 (count ships)))
        (is (not (nil? starfleet)))

        (condp = db-version
          "2.0" (do
                  (is (= 7 (count starfleet)))
                  (is (= "fleet" (:object starfleet)))
                  (is (= [{:priority "urgent", :objective "Overthrow Palpatine"}
                          {:priority "trivial", :objective "Destroy Klingon Empire"}] orders)))
          "3.0" (do
                  (is (= 7 (count starfleet)))
                  (is (= "fleet" (:object starfleet)))
                  (is (= [{:is-nonsense? true :ignore-me "urgent" :priority "HIGHEST" :purpose "Overthrow Palpatine"}
                          {:is-nonsense? false :ignore-me "trivial" :priority "HIGHEST" :purpose "Destroy Klingon Empire"}] orders)))
          (is false))))))

;; endregion
