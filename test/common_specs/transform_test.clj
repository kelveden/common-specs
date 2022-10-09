(ns common-specs.transform-test
  (:require [cheshire.core :as json]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test :refer :all]
            [com.gfredericks.test.chuck.clojure-test :refer [checking]]
            [java-time.api :as jt]
            [medley.core :refer [map-vals]]
            [common-specs.date]
            [common-specs.enum :refer [enum-spec]]
            [common-specs.transform :as t]
            [common-specs.decimal]
            [spec-tools.core :as st]))

(deftest can-transform-jsonify-friendly-edn-that-can-be-transformed-jsonify-string
  (let [spec (s/map-of string? map?)]
    (checking "Transformation is as expected" 100
              [m (s/gen spec)]
              (is (string? (->> m (t/jsonify spec) json/generate-string))))))

(deftest jsonify-instants-are-transformed-jsonify-friendly-edn
  (let [spec (s/map-of string? :date/instant)]
    (checking "Transformation is as expected" 100
              [m (s/gen spec)]
              (is (= (->> m (map-vals str))
                     (t/jsonify spec m))))))

(deftest jsonify-local-dates-are-transformed-jsonify-friendly-edn
  (let [spec (s/map-of string? :date/local-date)]
    (checking "Transformation is as expected" 100
              [m (s/gen spec)]
              (is (= (->> m (map-vals str))
                     (t/jsonify spec m))))))

(deftest jsonify-local-date-times-are-transformed-jsonify-friendly-edn
  (let [spec (s/map-of string? :date/local-date-time)]
    (checking "Transformation is as expected" 100
              [m (s/gen spec)]
              (is (= (->> m (map-vals str))
                     (t/jsonify spec m))))))

(deftest unjsonify-instant-strings-are-transformed-to-instants
  (let [spec (s/map-of string? :date/instant)]
    (checking "Transformation is as expected" 100
              [m (s/gen spec)]
              (is (= m
                     (->> m (t/jsonify spec) (t/unjsonify spec)))))))

(deftest unjsonify-local-date-strings-are-transformed-to-local-dates
  (let [spec (s/map-of string? :date/local-date)]
    (checking "Transformation is as expected" 100
              [m (s/gen spec)]
              (is (= m
                     (->> m (t/jsonify spec) (t/unjsonify spec)))))))

(deftest unjsonify-local-date-time-strings-are-transformed-to-local-date-times
  (let [spec (s/map-of string? :date/local-date-time)]
    (checking "Transformation is as expected" 100
              [m (s/gen spec)]
              (is (= m
                     (->> m (t/jsonify spec) (t/unjsonify spec)))))))

(deftest jsonify-enum-values-are-transformed-to-strings
  (let [spec  (enum-spec #{:sample/a :sample/b :sample/c})
        value (gen/generate (s/gen spec))]
    (is (#{"a" "b" "c"} (t/jsonify spec value)))))

(deftest unjsonify-enum-values-are-transformed-to-qualified-keywords
  (let [spec  (enum-spec #{:sample/a :sample/b :sample/c})
        value (gen/generate (s/gen #{"a" "b" "c"}))]
    (is (#{:sample/a :sample/b :sample/c} (t/unjsonify spec value)))))

(deftest unjsonify-bigdecimals-are-left-as-is
  (let [spec (s/map-of string? :decimal/scale-2dp)]
    (checking "Transformation is as expected" 100
              [m (s/gen spec)]
              (is (= m (t/unjsonify spec m))))))

(deftest unjsonify-doubles-are-converted-to-bigdecimals
  (let [spec (s/map-of string? :decimal/scale-2dp)]
    (checking "Transformation is as expected" 100
              [m (s/gen spec)]
              (let [json   (-> (t/jsonify spec m)
                               (json/generate-string m))
                    result (->> (json/parse-string json)
                                (t/unjsonify spec))]
                (is (= m result))))))

(deftest jsonify-big-decimals-are-transformed-to-doubles
  (let [spec (s/map-of string? :decimal/scale-2dp)]
    (checking "Transformation is as expected" 100
              [m (s/gen spec)]
              (is (= m
                     (->> m (t/jsonify spec) (t/unjsonify spec)))))))

(deftest unjsonify-invalid-instant-causes-assertion-error
  (is (thrown? AssertionError (t/unjsonify :date/instant "bollox"))))

(deftest unjsonify-invalid-local-date-causes-assertion-error
  (is (thrown? AssertionError (t/unjsonify :date/local-date "bollox"))))

(deftest unjsonify-invalid-local-date-time-causes-assertion-error
  (is (thrown? AssertionError (t/unjsonify :date/local-date-time "bollox"))))

(deftest unjsonify-invalid-decimal-causes-assertion-error
  (is (thrown? AssertionError (t/unjsonify :decimal/scale-1dp "bollox"))))