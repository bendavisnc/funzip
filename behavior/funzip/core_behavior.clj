(ns funzip.core-behavior
  (:require [speclj.core :refer :all]
            [funzip.core :as funzip]
            [orchestra.spec.test :as spec-test]
            [clojure.spec.gen.alpha]
            [clojure.xml :as xml]
            [funzip.specs.core]
            [funzip.specs.move-result]
            [funzip.specs.zipper]
            [funzip.traversal-utils :as traversal-utils]
            [funzip.trees :refer :all]
            [funzip.protocols :refer [node]]
            [funzip.tree-protocol-example-exts]
            [funzip.test-utils :refer [get-xml-node, create-xml-node, update-xml-node]]
            [funzip.zipper :as zipper]))

(spec-test/instrument)

(describe "funzip spec"
          (it "Should perform basic operations correctly"
              (doseq [[raw-tree, raw-tree-after, get-node, create-node, update-node] [[tree-vec, tree-vec-modded, first, #(assoc [] 0 %), #(assoc %1 0 %2)]
                                                                                      [tree-map, tree-map-modded, :node, #(assoc {} :node %), #(assoc %1 :node %2)]
                                                                                      [(-> "./behavior/test_files/tree_xml.xml" clojure.java.io/file xml/parse),
                                                                                       (-> "./behavior/test_files/tree_xml_modded.xml" clojure.java.io/file xml/parse),
                                                                                       get-xml-node
                                                                                       create-xml-node
                                                                                       update-xml-node]]]

                (let [modified (-> (zipper/node->zipper raw-tree)
                                   (funzip/move-down-at 1)                                           (funzip/tap-focus #(should= 12 (get-node %)))
                                   (funzip/move-down-right)                                          (funzip/tap-focus #(should= 123 (get-node %)))
                                   (funzip/delete-and-move-left)                                     (funzip/tap-focus #(should= 122 (get-node %)))
                                   (funzip/move-down-left)                                           (funzip/tap-focus #(should= 1221 (get-node %)))
                                   (funzip/update #(update-node % -1))                               (funzip/tap-focus #(should= -1 (get-node %)))
                                   (funzip/move-right)                                               (funzip/tap-focus #(should= 1222 (get-node %)))
                                   (funzip/set (create-node -2))                                     (funzip/tap-focus #(should= -2 (get-node %)))
                                   (funzip/move-up)                                                  (funzip/tap-focus #(should= 122 (get-node %)))
                                   (funzip/move-up)                                                  (funzip/tap-focus #(should= 12 (get-node %)))
                                   (funzip/rewind-left)                                              (funzip/tap-focus #(should= 11 (get-node %)))
                                   (funzip/move-down-right)                                          (funzip/tap-focus #(should= 112 (get-node %)))
                                   (funzip/move-left-by 1)                                           (funzip/tap-focus #(should= 111 (get-node %)))
                                   (funzip/delete-and-move-up)                                       (funzip/tap-focus #(should= 11 (get-node %)))
                                   (funzip/insert-down-right (create-node 113), (create-node 114))   (funzip/tap-focus #(should= 114 (get-node %)))
                                   (funzip/move-up)                                                  (funzip/tap-focus #(should= 11 (get-node %)))
                                   (funzip/rewind-right)                                             (funzip/tap-focus #(should= 13 (get-node %)))
                                   (funzip/insert-down-left (create-node 131), (create-node 132))    (funzip/tap-focus #(should= 131 (get-node %)))
                                   (funzip/find #(= -2 (get-node %)))                                (funzip/tap-focus #(should= -2 (get-node %)))
                                   (funzip/commit))]
                  (should= raw-tree-after
                           modified))))

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
                         (->> tree-less-simple-vec zipper/node->zipper funzip/preorder-seq (map first)))))

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
                                     (funzip/into {}))))))


(run-specs)
