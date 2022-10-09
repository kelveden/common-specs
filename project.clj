(defproject net.clojars.kelveden/common-specs "0.1.0"
  :description "Useful common specs."
  :url "https://github.com/kelveden/common-specs"
  :license {:name "Eclipse Public License"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [clojure.java-time "1.1.0"]
                 [metosin/spec-tools "0.10.5"]]
  :profiles {:dev          [:project/dev :profiles/dev]
             :repl         {:prep-tasks   ^:replace ["javac" "compile"]
                            :repl-options {:init-ns user}}
             :profiles/dev {}
             :project/dev  {:source-paths ["dev/src"]
                            :dependencies [[com.gfredericks/test.chuck "0.2.13"]
                                           [lambdaisland/kaocha "1.70.1086"]
                                           [medley "1.4.0"]
                                           [org.clojure/test.check "1.1.1"]]}}
  :aliases {"kaocha" ["run" "-m" "kaocha.runner"]
            "watch"  ["run" "-m" "kaocha.runner" "--watch"]}
  :repl-options {:init-ns common-specs.core})
