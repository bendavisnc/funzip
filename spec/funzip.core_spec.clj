(ns funzip.core-spec
  (:require [speclj.core :refer :all]
            [funzip.core :as funzip]))

(def tree {:node 1
           :children [{:node 11
                       :children [{:node 111}, {:node 112}]},
                      {:node 12
                       :children [{:node 121},
                                  {:node 122
                                   :children [{:node 1221}, {:node 1222}]}
                                  {:node 123}]},
                      {:node 13}]})

(def test-zipper (funzip.zipper/node->zipper tree))

(describe "funzip spec"
          (it "Should return nil on a failed (impossible) move."
            (should (nil? (funzip/move-left test-zipper))))
          (it "Should be consistent between moves."
            (let [a (funzip/move-down-left test-zipper)
                  b (funzip/move-right a)
                  c (funzip/move-left b)]
              (should (= a c)))))

(run-specs)