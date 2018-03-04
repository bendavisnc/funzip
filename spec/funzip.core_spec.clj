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



(describe "funzip spec"
  (it "Should perform basic operations correctly"
    (let [modified (-> (funzip.zipper/node->zipper tree)
                       (funzip/move-down-at 1) (funzip/tap-focus #(should= (:node %) 12))
                       (funzip/move-down-right) (funzip/tap-focus #(should= (:node %) 123))
                       (funzip/delete-and-move-left) (funzip/tap-focus #(should= (:node %) 122))
                       (funzip/move-down-left) (funzip/tap-focus #(should= (:node %) 1221))
                       (funzip/update #(assoc % :node -1)) (funzip/tap-focus #(should= (:node %) -1))
                       (funzip/move-right) (funzip/tap-focus #(should= (:node %) 1222))
                       (funzip/set {:node -2}) (funzip/tap-focus #(should= (:node %) -2))
                       (funzip/move-up) (funzip/tap-focus #(should= (:node %) 122))
                       (funzip/move-up) (funzip/tap-focus #(should= (:node %) 12))
                       (funzip/rewind-left) (funzip/tap-focus #(should= (:node %) 11))
                       (funzip/move-down-right) (funzip/tap-focus #(should= (:node %) 112))
                       (funzip/move-left-by 1) (funzip/tap-focus #(should= (:node %) 111))
                       (funzip/delete-and-move-up) (funzip/tap-focus #(should= (:node %) 11)))])))



;(describe "funzip spec"
;          (it "Should return nil on a failed (impossible) move."
;            (should (nil? (funzip/move-left test-zipper)))
;            (should (not (nil? (-> test-zipper funzip/move-down-left funzip/move-right funzip/move-right))))
;            (should (nil? (-> test-zipper funzip/move-down-left funzip/move-right funzip/move-right funzip/move-right))))
;          (it "Should be consistent between moves."
;            (let [a (funzip/move-down-left test-zipper)
;                  b (funzip/move-right a)
;                  c (funzip/move-left b)]
;              (should= a c)))
;          (describe "cycling"
;            (it "Should have the left most value when rewinding left"
;                (let [a (-> test-zipper funzip/move-down-left)
;                      b (-> test-zipper funzip/move-down-right funzip/rewind-left)]
;                  (should= a b)))
;            (it "Should have the left value relative to the offset given when moving left by"
;                (let [a (-> test-zipper funzip/move-down-left)
;                      b (-> test-zipper funzip/move-down-right (funzip/move-left-by 2))]
;                  (should= a b)))
;            (it "Should have the right most value when rewinding right"
;                (let [a (-> test-zipper funzip/move-down-left funzip/move-right funzip/move-right)
;                      b (-> test-zipper funzip/move-down-left funzip/rewind-right)
;                      c (-> test-zipper funzip/move-down-right)]
;                  (should= a b)
;                  (should= b c)))
;            (it "Should have the right value relative to the offset given when moving right by"
;                (let [a (-> test-zipper funzip/move-down-right)
;                      b (-> test-zipper funzip/move-down-left (funzip/move-right-by 2))]
;                  (should= a b))))
;          (describe "movement"
;            (describe "downward"
;              (it "Should move down one level and to the right by the given index"
;                (let [a (-> test-zipper funzip/move-down-right)
;                      b (-> test-zipper (funzip/move-down-at 2))]
;                  (should= a b))))))

(run-specs)