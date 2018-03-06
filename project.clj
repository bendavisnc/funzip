(defproject funzip "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"], [orchestra "2017.11.12-1"]]
  :profiles {:dev {:dependencies [[speclj "3.3.0"]]
                   :repl-options {:init-ns funzip.core-spec}
                   :test-paths ["spec"]}}
  :plugins [[speclj "3.3.0"], [lein-auto "0.1.3"]]
  :aliases {"test-continuously" ["auto" "spec" "-f" "d"]})




