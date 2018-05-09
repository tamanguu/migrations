(ns migrations.core-test
  (:require [clojure.test :refer :all]
            [clojure.test.tap :refer [print-tap-fail]]
            [environ.core :refer [env]]
            [taoensso.timbre :as timbre]
            [conman.core :as conman]))

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
          (or (= "1.0" db-version)
              (= "2.0" db-version))))))

(deftest content-test
  (let [db-version (:version (get-version))]

    (testing "captains table"
      (let [captains (get-captains)
            jim      (->> captains
                          (filter #(= "James T. Kirk" (:name %)))
                          first)]
        (is (= 2 (count captains)))
        (is (not (nil? jim)))

        (condp = db-version
          "1.0" (do
                  (is (= 3 (count jim)))
                  (is (nil? (:object jim))))
          "2.0" (do
                  (is (= 4 (count jim)))
                  (is (= "captain" (:object jim))))
          (print-tap-fail "unknown version number"))))

    (testing "starships table"
      (let [ships (get-starships)
            tos   (->> ships
                       (filter #(= "USS Enterprise NCC1701" (:name %)))
                       first)]
        (is (= 2 (count ships)))
        (is (not (nil? tos)))

        (condp = db-version
          "1.0" (do
                  (is (= 4 (count tos)))
                  (is (= "James T. Kirk" (:captain tos)))
                  (is (nil? (:object tos))))
          "2.0" (do
                  (is (= 5 (count tos)))
                  (is (= "James T. Kirk" (:captain tos)))
                  (is (= "starship" (:object tos))))
          (print-tap-fail "unknown version number"))))

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
          "1.0" (do
                  (is (= 6 (count starfleet)))
                  (is (nil? (:object starfleet)))
                  (is (= [{:priority "urgent", :objective "Overthrow Palpatine"}
                          {:priority "trivial", :objective "Destroy Klingon Empire"}] orders)))
          "2.0" (do
                  (is (= 7 (count starfleet)))
                  (is (= "fleet" (:object starfleet)))
                  (is (= [{:priority "urgent", :objective "Overthrow Palpatine"}
                          {:priority "trivial", :objective "Destroy Klingon Empire"}] orders)))
          (print-tap-fail "unknown version number"))))))

;; endregion
