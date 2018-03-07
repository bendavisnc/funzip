(ns funzip.core-spec
  (:require [speclj.core :refer :all]
            [funzip.core :as funzip]
            [orchestra.spec.test :as spec-test]
            [clojure.spec.gen.alpha]
            [funzip.specs.core]
            [funzip.specs.move-result]
            [funzip.specs.zipper]
            [funzip.unzip :refer [Unzip]]
            [funzip.traversal-utils :as traversal-utils]
            [funzip.trees :refer :all])
  (:import (clojure.lang IPersistentMap)))

(spec-test/instrument)

(extend-type IPersistentMap
  Unzip
  (unzip [this]
    (:children this))
  (zip [this, children]
    (assoc this :children children)))


(describe "funzip spec"
  (it "Should perform basic operations correctly"
    (let [modified (-> (funzip.zipper/node->zipper tree-b)
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
                  modified)))

  (it "Should support depth first traversal"
    (-> (funzip.zipper/node->zipper tree-a)   (funzip/tap-focus #(should= 1 (:node %)))
        (funzip/advance-preorder-depth-first) (funzip/tap-focus #(should= 2 (:node %)))
        (funzip/advance-preorder-depth-first) (funzip/tap-focus #(should= 4 (:node %)))
        (funzip/advance-preorder-depth-first) (funzip/tap-focus #(should= 5 (:node %)))
        (funzip/advance-preorder-depth-first) (funzip/tap-focus #(should= 3 (:node %))))
    (should (= [1 2 4 5 3]
               (traversal-utils/preorder tree-a2)
               (->> tree-a2 funzip.zipper/node->zipper funzip/preorder-seq (map first))
               (->> tree-a funzip.zipper/node->zipper funzip/preorder-seq (map :node))))
    (should (= [1 3 2 5 4]
               (traversal-utils/preorder tree-a2-mirrored)
               (->> tree-a2-mirrored funzip.zipper/node->zipper funzip/preorder-seq (map first))))
    (should (= [1 11 111 112 12 121 122 1221 1222 123 13]
               (traversal-utils/preorder tree-b2)
               (->> tree-b2 funzip.zipper/node->zipper funzip/preorder-seq (map first))
               (->> tree-b funzip.zipper/node->zipper funzip/preorder-seq (map :node))))
    (should (= [4 5 2 3 1]
               (traversal-utils/postorder tree-a2)
               (->> tree-a2 funzip.zipper/node->zipper funzip/postorder-seq (map first))))
    (should= [3 5 4 2 1]
             ;(traversal-utils/postorder tree-a2-mirrored)
             (->> tree-a2-mirrored funzip.zipper/node->zipper funzip/postorder-seq (map first)))))

;(def stupid-seq (funzip/preorder-seq (funzip.zipper/node->zipper tree-a2)))

;(defn stupid-test []
;  (doseq [foci stupid-seq]
;    (println (funzip.unzip/node foci)))

;(defn stupid-test2 []
;  (funzip/into (funzip.zipper/node->zipper tree-a)
;               []))
;
;(stupid-test2)

(run-specs)