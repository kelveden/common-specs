(ns common-specs.decimal
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [spec-tools.core :as st])
  (:import (java.math RoundingMode)))

(defn- scaled [scale]
  (fn [x]
    (>= scale (.scale x))))

(defmacro scaled-decimal?
  [scale]
  `(st/spec {:spec (s/and decimal? (scaled ~scale))
             :type :decimal
             :gen  (fn []
                     (gen/fmap #(.setScale % ~scale RoundingMode/FLOOR)
                               (s/gen decimal?)))}))

(s/def :decimal/scale-1dp (scaled-decimal? 1))
(s/def :decimal/scale-2dp (scaled-decimal? 2))
(s/def :decimal/scale-3dp (scaled-decimal? 3))
(s/def :decimal/scale-4dp (scaled-decimal? 4))
(s/def :decimal/scale-5dp (scaled-decimal? 5))
(s/def :decimal/scale-6dp (scaled-decimal? 6))
(s/def :decimal/scale-7dp (scaled-decimal? 7))
(s/def :decimal/scale-8dp (scaled-decimal? 8))
(s/def :decimal/scale-9dp (scaled-decimal? 9))
(s/def :decimal/scale-10dp (scaled-decimal? 10))

(s/def :decimal/scale-unknown
  (st/spec {:spec decimal? :type :decimal}))