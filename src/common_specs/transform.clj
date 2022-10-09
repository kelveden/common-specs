(ns common-specs.transform
  (:require [common-specs.date :as dt]
            [spec-tools.core :as st]
            [spec-tools.transform :as stt]
            [clojure.spec.alpha :as s])
  (:import (clojure.lang ExceptionInfo)))

(def ^:private edn->json-friendly-decoders
  (-> stt/json-type-encoders
      (assoc :local-date (fn [_ x] (dt/local-date->str x))
             :local-date-time (fn [_ x] (dt/local-date-time->str x))
             :instant (fn [_ x] (dt/instant->str x))
             ; to avoid st/coerce undoing the work of cheshire in converting numbers to BigDecimals
             :decimal (fn [_ x] x))))

(def ^:private json-friendly->edn-decoders
  (-> stt/json-type-decoders
      (assoc :local-date (fn [_ x] (try
                                     (dt/str->local-date x)
                                     (catch ExceptionInfo _
                                       (throw (AssertionError. (format "%s cannot be coerced to a LocalDate" x))))))
             :local-date-time (fn [_ x] (try
                                          (dt/str->local-date-time x)
                                          (catch ExceptionInfo _
                                            (throw (AssertionError. (format "%s cannot be coerced to a LocalDateTime" x))))))
             :instant (fn [_ x] (try
                                  (dt/str->instant x)
                                  (catch ExceptionInfo _
                                    (throw (AssertionError. (format "%s cannot be coerced to an Instant" x))))))
             ; to avoid st/coerce undoing the work of cheshire in converting numbers to BigDecimals
             :decimal (fn [_ x] (try (bigdec x)
                                     (catch NumberFormatException _
                                       (throw (AssertionError. (format "%s cannot be coerced to a BigDecimal." x)))))))))

(def json-friendly->edn-transformer
  (st/type-transformer
    {:name            :json->edn
     :decoders        json-friendly->edn-decoders
     :default-encoder stt/any->any}))

(def edn->json-friendly-transformer
  (st/type-transformer
    {:name            :edn->json
     :decoders        edn->json-friendly-decoders
     :default-encoder stt/any->any}))

(defn jsonify
  "Transforms the given edn to a JSON-friendly version that can then be transformed into a JSON string."
  [spec edn]
  (st/coerce spec edn edn->json-friendly-transformer))

(defn unjsonify
  "Transforms the given JSON-friendly edn into edn where all JSON-friendly values have been replaced with internal types
  (dates, enums and so on)"
  [spec json]
  (try
    (st/coerce spec json json-friendly->edn-transformer)
    (catch ExceptionInfo e
      (throw (IllegalArgumentException. e)))))
