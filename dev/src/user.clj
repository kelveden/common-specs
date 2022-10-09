(ns user
  (:require [kaocha.repl :as kaocha]))

(defn test*
  [& [ns]]
  (if ns
    (kaocha/run ns)
    (kaocha/run-all {:reporter [kaocha.report/dots]})))
