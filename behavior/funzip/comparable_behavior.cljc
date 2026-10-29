(ns funzip.comparable-behavior
  #?(:clj (:require [speclj.core :refer [describe it should= run-specs with]]
                    [orchestra.spec.test :as spec-test])
     :cljs (:require-macros [speclj.core :refer
                             [describe it should= should-not run-specs should
                              with]]))
  (:require [clojure.zip :as zip]
            [funzip.core :as fzc]
            [funzip.zipper :as fz]
            [speclj.core]
            [funzip.specs.core]
            [funzip.specs.move-result]
            [funzip.specs.zipper]
            [clojure.spec.gen.alpha]))

(defn zip-zipper
  [t]
  (zip/zipper
   vector?
   second
   (fn [node children]
     (assoc node 1 (vec children)))
   t))

(defn zip-zipper-tap-node
  [z node-fn]
  (do (node-fn (zip/node z))
      z))

;!zprint {:format :skip}
(describe "funzip spec"
          (with tree [1 [[11 [[111] [112]]]
                         [12 [[121]
                             [122 [[1221] [1222]]]
                             [123]]]
                         [13]]])
          (with expected [1 [[11 [[112] [113] [114]]]
                             [12 [[121] [122 [[-1] [-2]]]]]
                             [13 [[131] [132]]]]])
          (it "Should have comparable operations to clojure zip."
              (doseq [[zipper-create, tap-node, ops]
                      [[fz/node->zipper fzc/tap-focus
                                                      {:move-down-at-1        #(fzc/move-down-at % 1)
                                                       :move-down-right       fzc/move-down-right
                                                       :delete-and-move-left  fzc/delete-and-move-left
                                                       :move-down-left        fzc/move-down-left
                                                       :update                #(fzc/update % (fn [_] [-1]))
                                                       :move-right            fzc/move-right
                                                       :set                   #(fzc/set % [-2])
                                                       :move-up               fzc/move-up
                                                       :rewind-left           fzc/rewind-left
                                                       :move-left-by-1        #(fzc/move-left-by % 1)
                                                       :delete-and-move-up    fzc/delete-and-move-up
                                                       :insert-down-right     #(fzc/insert-down-right % [113] [114])
                                                       :rewind-right          fzc/rewind-right
                                                       :insert-down-left      #(fzc/insert-down-left % [131] [132])
                                                       :find                  (fn [z] (fzc/find z #(= -2 (first %))))
                                                       :commit                fzc/commit}]
                       [zip-zipper zip-zipper-tap-node {:move-down-at-1       #(zip/right (zip/down %))
                                                        :move-down-right      #(zip/rightmost (zip/down %))
                                                        :delete-and-move-left #(zip/up (zip/remove %))
                                                        :move-down-left       #(zip/leftmost (zip/down %))
                                                        :update               #(zip/edit % (fn [_] [-1]))
                                                        :move-right           zip/right
                                                        :set                  #(zip/edit % (fn [_] [-2]))
                                                        :move-up              zip/up
                                                        :rewind-left          zip/leftmost
                                                        :move-left-by-1       zip/left
                                                        :delete-and-move-up   zip/remove
                                                        :insert-down-right    (comp zip/rightmost zip/down #(zip/append-child % [114]) #(zip/append-child % [113]))
                                                        :rewind-right         zip/rightmost
                                                        :insert-down-left     (comp zip/leftmost zip/down #(zip/insert-child % [131]) #(zip/insert-child % [132]))
                                                        :find                 (comp zip/right zip/down zip/right zip/down zip/left zip/up) ;; zip doesn't have a find. todo: perhaps implement for testing
                                                        :commit               zip/root}]]]

               (let [modified   (as-> (zipper-create @tree) z
                                      ((:move-down-at-1 ops) z)       (tap-node z #(should= 12 (first %)))
                                      ((:move-down-right ops) z)      (tap-node z #(should= 123 (first %)))
                                      ((:delete-and-move-left ops) z) (tap-node z #(should= 122 (first %)))
                                      ((:move-down-left ops) z)       (tap-node z #(should= 1221 (first %)))
                                      ((:update ops) z)               (tap-node z #(should= -1 (first %)))
                                      ((:move-right ops) z)           (tap-node z #(should= 1222 (first %)))
                                      ((:set ops) z)                  (tap-node z #(should= -2 (first %)))
                                      ((:move-up ops) z)              (tap-node z #(should= 122 (first %)))
                                      ((:move-up ops) z)              (tap-node z #(should= 12 (first %)))
                                      ((:rewind-left ops) z)          (tap-node z #(should= 11 (first %)))
                                      ((:move-down-right ops) z)      (tap-node z #(should= 112 (first %)))
                                      ((:move-left-by-1 ops) z)       (tap-node z #(should= 111 (first %)))
                                      ((:delete-and-move-up ops) z)   (tap-node z #(should= 11 (first %)))
                                      ((:insert-down-right ops) z)    (tap-node z #(should= 114 (first %)))
                                      ((:move-up ops) z)              (tap-node z #(should= 11 (first %)))
                                      ((:rewind-right ops) z)         (tap-node z #(should= 13 (first %)))
                                      ((:insert-down-left ops) z)     (tap-node z #(should= 131 (first %)))
                                      ((:find ops) z)                 (tap-node z #(should= -2 (first %)))
                                      ((:commit ops) z))]
                   (should= @expected modified)))))

#?(:clj (spec-test/instrument))

(run-specs)
