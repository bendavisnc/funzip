(defproject funzip "0.0.9"
  :description "A protocol based implementation of Huet's Zipper."
  :url "https://github.com/bendavisnc/funzip"
  :license {:name "MIT"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.12.6"]]
  :profiles {:dev {:dependencies [[speclj "3.13.0"], [orchestra "2021.01.01-1"]]
                   :repl-options {:init-ns funzip.core-behavior}
                   :test-paths ["behavior"]}}
  :plugins [[speclj "3.13.0"], [lein-auto "0.1.3"]]
  :aliases {"test-continuously" ["auto" "spec" "-f" "d"]}
  :pom-addition [:developers [:developer
                              [:name "Ben Davis"]
                              [:email "bendavisnc@gmail.com"]]]
  :deploy-repositories [["clojars" {:sign-releases false}]])


