(ns funzip.core-test
  (:require [clojure.test :refer :all]
            [funzip.core :as funzip]))


(def tree {:node 1
           :children [{:node 11
                       :children [{:node 111}, {:node 112}]}
                      {:node 12
                       :children [{:node 121},
                                  {:node 122
                                   :children [{:node 1221}, {:node 1222}]}
                                  {:node 123}]}
                      {:node 13}]})

(def z (funzip.zipper/node->zipper z))

(deftest core-test
  (testing "A failed move result"
    (is (nil? (-> z funzip/move-left)))))
