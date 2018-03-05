(ns funzip.core-spec
  (:require [speclj.core :refer :all]
            [funzip.core :as funzip]
            [orchestra.spec.test :as spec-test]
            [funzip.specs.core]
            [funzip.specs.move-result]
            [funzip.specs.zipper]))

(spec-test/instrument)

(def tree {:node 1
           :children [{:node 11
                       :children [{:node 111}, {:node 112}]},
                      {:node 12
                       :children [{:node 121},
                                  {:node 122
                                   :children [{:node 1221}, {:node 1222}]}
                                  {:node 123}]},
                      {:node 13}]})



(describe "funzip spec"
  (it "Should perform basic operations correctly"
    (let [modified (-> (funzip.zipper/node->zipper tree)
                       (funzip/move-down-at 1)                                           (funzip/tap-focus #(should= 12 (:node %)))
                       (funzip/move-down-right)                                          (funzip/tap-focus #(should= 123 (:node %)))
                       (funzip/delete-and-move-left)                                     (funzip/tap-focus #(should= 122 (:node %)))
                       (funzip/move-down-left)                                           (funzip/tap-focus #(should= 1221 (:node %)))
                       (funzip/update #(assoc % :node -1))                               (funzip/tap-focus #(should= -1 (:node %)))
                       (funzip/move-right)                                               (funzip/tap-focus #(should= 1222 (:node %)))
                       (funzip/set {:node -2})                                           (funzip/tap-focus #(should= -2 (:node %)))
                       (funzip/move-up)                                                  (funzip/tap-focus #(should= 122 (:node %)))
                       (funzip/move-up)                                                  (funzip/tap-focus #(should= 12 (:node %)))
                       (funzip/rewind-left)                                              (funzip/tap-focus #(should= 11 (:node %)))
                       (funzip/move-down-right)                                          (funzip/tap-focus #(should= 112 (:node %)))
                       (funzip/move-left-by 1)                                           (funzip/tap-focus #(should= 111 (:node %)))
                       (funzip/delete-and-move-up)                                       (funzip/tap-focus #(should= 11 (:node %)))
                       (funzip/insert-down-right {:node 113}, {:node 114})               (funzip/tap-focus #(should= 114 (:node %)))
                       (funzip/move-up)                                                  (funzip/tap-focus #(should= 11 (:node %)))
                       (funzip/rewind-right)                                             (funzip/tap-focus #(should= 13 (:node %)))
                       (funzip/insert-down-left {:node 131}, {:node 132})                (funzip/tap-focus #(should= 131 (:node %)))
                       (funzip/commit))]
         (should= {:node 1,
                   :children [{:node 11,
                               :children [{:node 112} {:node 113} {:node 114}]}
                              {:node 12,
                               :children [{:node 121} {:node 122,
                                                       :children [{:node -1} {:node -2}]}]}
                              {:node 13,
                               :children [{:node 131} {:node 132}]}]}
                  modified))))


(run-specs)