(ns common-specs.enum
  (:require [clojure.spec.alpha :as s]
            [spec-tools.core :as st]))

(defn enum-spec
  [keyword-set]
  (let [enum-ns  (-> keyword-set first namespace)]
    (st/spec
      {:spec             keyword-set
       :decode/edn->json (fn [_ x] (if (keyword? x) (name x) x))
       :decode/json->edn (fn [_ x] (if (string? x) (keyword enum-ns x) x))
       :gen              #(s/gen keyword-set)})))
