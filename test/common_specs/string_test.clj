(ns common-specs.string-test
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test :refer :all]
            [common-specs.string]))

(deftest string-identifiers-are-unique
  (let [number-of-ids 1000000
        work-item-ids (gen/vector (s/gen :string/identifier) number-of-ids)]
    (is (= number-of-ids (count (set (gen/generate work-item-ids)))))))
