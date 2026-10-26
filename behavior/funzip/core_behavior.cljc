(ns funzip.core-behavior
  #?(:clj (:require [speclj.core :refer [describe it should= should-not run-specs, should]])
     :cljs (:require-macros [speclj.core :refer [describe it should= should-not run-specs, should]]))
  (:require [speclj.core]
            [funzip.core :as funzip]
            [clojure.spec.gen.alpha]
            [funzip.tree-protocol-example-exts]
            [funzip.traversal-utils :as traversal-utils]
            [funzip.trees :refer [tree-map, tree-vec, tree-less-simple-vec, tree-simple-map, tree-simple-vec, tree-simple-vec-mirrored, tree-less-simple-vec, tree-less-simple-map, tree-map-super-complex]]
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
               (-> (zipper/node->zipper tree-simple-map) (funzip/tap-focus #(should= 1 (:node %)))
                   (funzip/advance-preorder-depth-first) (funzip/tap-focus #(should= 2 (:node %)))
                   (funzip/advance-preorder-depth-first) (funzip/tap-focus #(should= 4 (:node %)))
                   (funzip/advance-preorder-depth-first) (funzip/tap-focus #(should= 5 (:node %)))
                   (funzip/advance-preorder-depth-first) (funzip/tap-focus #(should= 3 (:node %))))
               (should (= [1 2 4 5 3]
                          (traversal-utils/preorder tree-simple-vec)
                          (->> tree-simple-vec zipper/node->zipper funzip/preorder-seq (map first))
                          (->> tree-simple-map zipper/node->zipper funzip/preorder-seq (map :node))))
               (should (= [1 3 2 5 4]
                          (traversal-utils/preorder tree-simple-vec-mirrored)
                          (->> tree-simple-vec-mirrored zipper/node->zipper funzip/preorder-seq (map first))))
               (should (= [1 11 111 112 12 121 122 1221 1222 123 13]
                          (traversal-utils/preorder tree-vec)
                          (->> tree-vec zipper/node->zipper funzip/preorder-seq (map first))
                          (->> tree-map zipper/node->zipper funzip/preorder-seq (map :node))))
               (should (= [1 2 4 5 3 6 8 9 -1]
                          (traversal-utils/preorder tree-less-simple-vec)
                          (->> tree-less-simple-vec zipper/node->zipper funzip/preorder-seq (map first)))))))

(describe "funzip/into"
          (it "Can be used to convert from one zipper data structure to another"
              (should= tree-simple-vec
                       (funzip/into (zipper/node->zipper tree-simple-map) []))
              (should= tree-simple-map
                       (funzip/into (zipper/node->zipper tree-simple-vec) {}))
              (should= tree-vec
                       (funzip/into (zipper/node->zipper tree-map) []))
              (should= tree-simple-vec-mirrored
                       (funzip/into (zipper/node->zipper (funzip/into (zipper/node->zipper tree-simple-vec-mirrored) {}))
                                    []))
              (should= tree-simple-vec-mirrored
                       (-> tree-simple-vec-mirrored
                           (zipper/node->zipper)
                           (funzip/into {})
                           (zipper/node->zipper)
                           (funzip/into [])))
              (should= tree-less-simple-map
                       (-> tree-less-simple-map
                           (zipper/node->zipper)
                           (funzip/into [])
                           (zipper/node->zipper)
                           (funzip/into {})))
              (should= tree-map-super-complex
                       (-> tree-map-super-complex
                           (zipper/node->zipper)
                           (funzip/into [])
                           (zipper/node->zipper)
                           (funzip/into {})))))

(run-specs)
