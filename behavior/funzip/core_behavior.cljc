(ns funzip.core-behavior
  #?(:clj (:require [speclj.core :refer [describe it should= run-specs]])
     :cljs (:require-macros [speclj.core :refer [describe it should= should-not run-specs, should]]))
  (:require [speclj.core]
            [funzip.core :as funzip]
            [clojure.spec.gen.alpha]
            [funzip.zipper :as zipper]))

(describe "funzip spec"
          (let [tree [1 [[11, [[111], [112]]]
                         [12, [[121],
                               [122, [[1221], [1222]]]
                               [123]]]
                         [13]]]]
            (it "Should perform basic operations correctly"
                (let [modified (-> (zipper/node->zipper tree)
                                   (funzip/move-down-at 1)                                           (funzip/tap-focus #(should= 12 (first %)))
                                   (funzip/move-down-right)                                          (funzip/tap-focus #(should= 123 (first %)))
                                   (funzip/delete-and-move-left)                                     (funzip/tap-focus #(should= 122 (first %)))
                                   (funzip/move-down-left)                                           (funzip/tap-focus #(should= 1221 (first %)))
                                   (funzip/update (fn [_] [-1]))                                     (funzip/tap-focus #(should= -1 (first %)))
                                   (funzip/move-right)                                               (funzip/tap-focus #(should= 1222 (first %)))
                                   (funzip/set [-2])                                                 (funzip/tap-focus #(should= -2 (first %)))
                                   (funzip/move-up)                                                  (funzip/tap-focus #(should= 122 (first %)))
                                   (funzip/move-up)                                                  (funzip/tap-focus #(should= 12 (first %)))
                                   (funzip/rewind-left)                                              (funzip/tap-focus #(should= 11 (first %)))
                                   (funzip/move-down-right)                                          (funzip/tap-focus #(should= 112 (first %)))
                                   (funzip/move-left-by 1)                                           (funzip/tap-focus #(should= 111 (first %)))
                                   (funzip/delete-and-move-up)                                       (funzip/tap-focus #(should= 11 (first %)))
                                   (funzip/insert-down-right [113], [114])                           (funzip/tap-focus #(should= 114 (first %)))
                                   (funzip/move-up)                                                  (funzip/tap-focus #(should= 11 (first %)))
                                   (funzip/rewind-right)                                             (funzip/tap-focus #(should= 13 (first %)))
                                   (funzip/insert-down-left [131], [132])                            (funzip/tap-focus #(should= 131 (first %)))
                                   (funzip/find #(= -2 (first %)))                                   (funzip/tap-focus #(should= -2 (first %)))
                                   (funzip/commit))]
                  (should= [1 [[11 [[112] [113] [114]]] [12 [[121] [122 [[-1] [-2]]]]] [13 [[131] [132]]]]]
                           modified)))
            (it "Should support depth first traversal"
               (-> (zipper/node->zipper tree)
                   (funzip/advance-preorder-depth-first) (funzip/tap-focus #(should= 11 (first %)))
                   (funzip/advance-preorder-depth-first) (funzip/tap-focus #(should= 111 (first %)))
                   (funzip/advance-preorder-depth-first) (funzip/tap-focus #(should= 112 (first %)))
                   (funzip/advance-preorder-depth-first) (funzip/tap-focus #(should= 12 (first %)))))))

(run-specs)
