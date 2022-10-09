(ns common-specs.date
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [java-time.api :as jt]
            [spec-tools.core :as st]))

(def ^:private timezone "UTC")

(defn- millis->days-mapper-fn
  [converter]
  (fn [ms]
    (-> ms
        (/ (* 1000 60 60 24))
        int
        (* (* 1000 60 60 24))
        jt/instant
        (converter timezone))))

(defn str->local-date [x] (cond-> x (string? x) jt/local-date))
(defn str->local-date-time [x] (cond-> x (string? x) jt/local-date-time))
(defn str->instant [x] (cond-> x (string? x) (-> (clojure.string/replace #"\+00:00" "Z") jt/instant)))
(defn local-date->str [x] (cond-> x (jt/local-date? x) str))
(defn local-date-time->str [x] (cond-> x (jt/local-date-time? x) str))
(defn instant->str  [x] (cond-> x (jt/instant? x) str))

(defn instant->local-date-time
  "Converts a given java.time.Instant to a java.time.LocalDateTime in the UTC timezone."
  [x]
  (cond-> x (jt/instant? x) (jt/local-date-time timezone)))

(defn local-date-time->instant
  "Converts a given java.time.LocalDateTime to a java.time.Instant in the UTC timezone."
  [x]
  (cond-> x (jt/local-date-time? x) (jt/instant timezone)))

(defn instant->local-date
  "Converts a given java.time.Instant to a java.time.LocalDate in the UTC timezone."
  [x]
  (cond-> x (jt/instant? x) (jt/local-date timezone)))

(defn- date-string?
  [date-string]
  (try
    (jt/local-date "yyyy-MM-dd" date-string)
    true
    (catch Exception _ false)))

(s/def :date/date-string
  (s/with-gen (s/and string? date-string?)
              #(gen/fmap (fn [ms] (->> ((millis->days-mapper-fn jt/local-date) ms)
                                       (jt/format "yyyy-MM-dd")))
                         (gen/choose 0
                                     (-> "2030-12-31T23:59:59Z" jt/instant jt/to-millis-from-epoch)))))

(defn- date-time-string?
  [date-time-string]
  (try
    (jt/instant date-time-string)
    true
    (catch Exception _ false)))

(s/def :date/date-time-string
  (s/with-gen (s/and string? date-time-string?)
              #(gen/fmap str (s/gen :date/instant))))

(defmacro date-spec
  [pred type gen-mapper-fn]
  `(st/spec {:spec ~pred
             :type ~type
             :gen  #(gen/fmap ~gen-mapper-fn
                              (gen/choose 0
                                          (-> "2030-12-31T23:59:59Z" jt/instant jt/to-millis-from-epoch)))}))

; A date ONLY - i.e. no time included. Internal type is java.time.LocalDate.
(s/def :date/local-date (date-spec jt/local-date? :local-date
                                   (millis->days-mapper-fn jt/local-date)))
; A date and time of day - i.e. independent of any timezone. Internal type is java.time.LocalDateTime
(s/def :date/local-date-time (date-spec jt/local-date-time? :local-date-time
                                        #(-> % jt/instant (jt/local-date-time timezone))))
; A specific moment in time - i.e. a UTC date/time. Internal type is java.time.Instant
(s/def :date/instant (date-spec jt/instant? :instant jt/instant))
