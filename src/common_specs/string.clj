(ns common-specs.string
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(s/def :string/non-blank (s/and string? (complement clojure.string/blank?)))
(s/def :string/non-null (s/and string? some?))

(s/def :string/identifier
  (s/with-gen :string/non-blank
    #(gen/fmap str (gen/uuid))))
