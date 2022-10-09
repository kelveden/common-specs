(ns common-specs.date-test
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test :refer :all]
            [com.gfredericks.test.chuck.clojure-test :refer [checking]]
            [java-time.api :as jt]
            [common-specs.date :as date])
  (:import (java.time Instant LocalDate LocalDateTime)
           (java.util Date)))

(deftest can-generate-local-dates
  (checking "LocalDate generation" 1000
            [dt (s/gen :date/local-date)]
            (is (instance? LocalDate dt))))

(deftest can-generate-instants
  (checking "Instant generation" 1000
            [dt (s/gen :date/instant)]
            (is (instance? Instant dt))))

(deftest can-generate-local-date-times
  (checking "LocalDateTime generation" 1000
            [dt (s/gen :date/local-date-time)]
            (is (instance? LocalDateTime dt))))

(deftest can-convert-local-dates-to-strings
  (checking "LocalDate unforming" 1000
            [dt (s/gen :date/local-date)]
            (is (re-matches #"^\d{4}-\d{2}-\d{2}$" (date/local-date->str dt)))))

(deftest can-convert-instants-to-strings
  (checking "Instant unforming" 1000
            [dt (s/gen :date/instant)]
            (is (re-matches #"^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}(|.\d{1,3})Z$" (date/instant->str dt)))))

(deftest can-convert-local-date-times-to-strings
  (checking "LocalDateTime unforming" 1000
            [dt (s/gen :date/local-date-time)]
            (is (re-matches #"^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}(|.\d{1,3})$" (date/local-date-time->str dt)))))

(deftest can-convert-strings-to-local-dates
  (checking "LocalDate conforming" 1000
            [dt (s/gen :date/local-date)]
            (is (= dt (date/str->local-date (date/local-date->str dt))))))

(deftest can-convert-strings-to-local-date-times
  (checking "LocalDateTime conforming" 1000
            [dt (s/gen :date/local-date-time)]
            (is (= dt (date/str->local-date-time (date/local-date-time->str dt))))))

(deftest can-convert-strings-to-instants
  (checking "Instant conforming" 1000
            [dt (s/gen :date/instant)]
            (is (= dt (date/str->instant (date/instant->str dt))))))

(deftest local-date-is-converted-to-local-date-as-is
  (let [dt (s/gen :date/local-date)]
    (is (= dt (date/str->local-date dt)))))

(deftest local-date-time-is-converted-to-local-date-time-as-is
  (let [dt (s/gen :date/local-date-time)]
    (is (= dt (date/str->local-date-time dt)))))

(deftest instant-is-converted-to-instant-as-is
  (let [dt (s/gen :date/local-date)]
    (is (= dt (date/str->instant dt)))))

(deftest local-date-string-is-converted-to-string-as-is
  (let [s (-> (s/gen :date/local-date)
              (date/local-date->str))]
    (is (= s (date/local-date->str s)))))

(deftest local-date-time-string-is-converted-to-string-as-is
  (let [s (-> (s/gen :date/local-date-time)
              (date/local-date-time->str))]
    (is (= s (date/local-date-time->str s)))))

(deftest instant-string-is-converted-to-string-as-is
  (let [s (-> (s/gen :date/instant)
              (date/instant->str))]
    (is (= s (date/instant->str s)))))

(deftest instant-+0-string-is-converted-to-string-as-is
  (let [s "2019-06-16T23:00:00+00:00"]
    (is (instance? Instant (date/str->instant s)))))

(deftest instant->local-date-time-creates-timezone-agnostic-date-time
  (checking "all instants" 100
            [i (s/gen :date/instant)]
            (let [instant-str (-> i
                                  (date/instant->str)
                                  (clojure.string/replace "Z" ""))
                  converted   (date/instant->local-date-time i)]
              (is (= instant-str (date/local-date-time->str converted))
                  "String representation of local-date-time is the same as instant except for absence of Z representing timezone"))))

(deftest instant->local-date-creates-timezone-agnostic-date
  (checking "all instants" 100
            [i (s/gen :date/instant)]
            (let [instant-str (-> i
                                  (date/instant->str))
                  converted   (date/instant->local-date i)]
              (is (clojure.string/starts-with? instant-str (date/local-date->str converted))
                  "String representation of local-date is the same as the date part of the instant"))))

(deftest local->local-date-time-creates-timezone-agnostic-date-time
  (checking "all local date times" 100
            [ldt (s/gen :date/local-date-time)]
            (let [ldt-string (-> ldt
                                 (date/local-date-time->str)
                                 (str "Z"))
                  converted  (date/local-date-time->instant ldt)]
              (is (= ldt-string (date/instant->str converted))
                  "String representation of instant is the same as local-date-time except for presence of Z representing timezone"))))