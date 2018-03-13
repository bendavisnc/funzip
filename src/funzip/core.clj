(ns funzip.core
  (:refer-clojure :exclude [cycle, update, set, repeat, into])
  (:require [funzip.move-result :as move-result :refer [move-result?]]
            [funzip.zipper :as zipper :refer [zipper?]]
            [funzip.protocols :refer [unzip, zip, node, children]]))


(defn stay [z]
  (move-result/successful-move :origin z, :zipper z))

(defn move-to [origin, z]
  (move-result/successful-move :origin origin, :zipper z))

(defn fail [origin]
  (move-result/failed-move :origin origin))

;;
;; Utils

(defn cycle [z, move-fn]
  (loop [acc z]
    (let [result (move-fn acc)]
      (if (move-result/fail? result)
        acc
        ;else
        (recur (move-result/get result))))))

(defn try-repeat [z, n, move-fn]
  (move-result/with-origin
    (loop [n* n, acc z]
      (if (< n* 1)
        (stay acc)
        ;else
        (let [result (move-fn acc)]
          (if (move-result/fail? result)
            result
            ;else
            (recur (dec n*) (move-result/get result))))))
    z))

(defn repeat [z, n, move-fn]
  (move-result/get (try-repeat z n move-fn)))

(defn tap-focus [z, f]
  (do
    (f (:focus z))
    z))

(defn set [z, v]
  (zipper/copy-zipper z
                      :focus v))

(defn update [z, f]
  (clojure.core/update z :focus f))

(defn first-success [z & moves]
  (loop [moves* moves, acc nil]
    (cond
      (and acc (move-result/success? acc))
      acc
      (empty? moves*)
      (fail z)
      :else
      (recur (rest moves*)
             ((first moves*) z)))))

;;
;;
;; Movement

;;
;; Sideways

;; Left

(defn try-move-left [z]
  (let [[head & tail] (:left z)]
    (if (nil? head)
      (fail z)
      ;else
      (move-to z
               (zipper/copy-zipper z
                                   :left tail
                                   :focus head
                                   :right (cons (:focus z) (:right z)))))))


(defn move-left [z]
  (move-result/get (try-move-left z)))


(defn rewind-left [z]
  (cycle z try-move-left))


(defn try-move-left-by [z, n]
  (try-repeat z, n, try-move-left))


(defn move-left-by [z, n]
  (move-result/get (try-move-left-by z n)))


(defn try-delete-and-move-left [z]
  (let [[head & tail] (:left z)]
    (if (nil? head)
      (fail z)
      ;else
      (move-to z
               (zipper/copy-zipper z
                                   :left tail
                                   :focus head)))))


(defn delete-and-move-left [z]
  (move-result/get (try-delete-and-move-left z)))


;; Right

(defn try-move-right [z]
  (let [[head & tail] (:right z)]
    (if (nil? head)
      (fail z)
      ;else
      (move-to z
               (zipper/copy-zipper z
                                   :right tail
                                   :focus head
                                   :left (cons (:focus z) (:left z)))))))


(defn move-right [z]
  (move-result/get (try-move-right z)))


(defn rewind-right [z]
  (cycle z try-move-right))


(defn try-move-right-by [z, n]
  (try-repeat z, n, try-move-right))


(defn move-right-by [z, n]
  (move-result/get (try-move-right-by z n)))


;; Down - left

(defn try-move-down-left [z]
  (let [[head & tail] (unzip (:focus z))]
    (if (nil? head)
      (fail z)
      ;else
      (move-to z
               (zipper/copy-zipper z
                                   :left nil
                                   :focus head
                                   :right tail
                                   :top z)))))


(defn move-down-left [z]
  (move-result/get (try-move-down-left z)))


;; Down - right

(defn try-move-down-right [z]
  (move-result/map (try-move-down-left z) rewind-right))


(defn move-down-right [z]
  (move-result/get (try-move-down-right z)))


;; Down

(defn try-move-down-at [z, i]
  (move-result/map (try-move-down-left z) #(move-right-by % i)))


(defn move-down-at [z, i]
  (move-result/get (try-move-down-at z i)))


(defn try-insert-down-left [z, & values]
  (let [[head & tail] (concat values (unzip (:focus z)))]
    (if (nil? head)
      (fail z)
      ;else
      (move-to z
               (zipper/copy-zipper z
                                   :left nil
                                   :focus head
                                   :right tail
                                   :top z)))))

(defn insert-down-left [z, & values]
  (move-result/get (apply try-insert-down-left (cons z values))))


(defn try-insert-down-right [z, & values]
  (let [[head & tail] (concat (unzip (:focus z)) values)]
    (if (nil? head)
      (fail z)
      ;else
      (move-to z
               (rewind-right (zipper/copy-zipper z
                                                 :left nil
                                                 :focus head
                                                 :right tail
                                                 :top z))))))

(defn insert-down-right [z, & values]
  (move-result/get (apply try-insert-down-right (cons z values))))

;; Up

(defn try-move-up [z]
  (let [head (:top z)]
    (if (zipper/top? z)
      (fail z)
      (move-to z
               (zipper/copy-zipper head
                                   :focus (let [children
                                                 (-> (:focus z)
                                                     (cons (:left z))
                                                     (reverse)
                                                     (concat (:right z)))]
                                            (zip (:focus head) children)))))))


(defn move-up [z]
  (move-result/get (try-move-up z)))


(defn try-delete-and-move-up [z]
  (let [head (:top z)]
    (if (zipper/top? z)
      (fail z)
      (move-to z
               (zipper/copy-zipper head
                                   :focus (let [children
                                                (-> (:left z)
                                                    (reverse)
                                                    (concat (:right z)))]
                                            (zip (:focus head) children)))))))


(defn delete-and-move-up [z]
  (move-result/get (try-delete-and-move-up z)))

;; Traversal

(defn try-advance-preorder-depth-first [z]
  (first-success z
                 try-move-down-left,
                 try-move-right,
                 (fn [z]
                   (loop [z* z]
                     (if (or (nil? z*) (zipper/top? z*))
                       (fail z)
                       ;else
                       (let [m-right (try-move-right z*)]
                         (if (move-result/success? m-right)
                           m-right
                           (recur (move-up z*)))))))))

(defn advance-preorder-depth-first [z]
  (move-result/get (try-advance-preorder-depth-first z)))

(defn commit [z]
  (:focus (cycle z try-move-up)))

(defn preorder-seq [z]
  (letfn [(->seq* [m]
            (if (move-result/fail? m)
              nil
              ;else
              (let [z* (move-result/get m)]
                (lazy-seq (cons (:focus z*)
                                (->seq* (try-advance-preorder-depth-first z*)))))))]
    (->seq* (stay z))))


(defn into [z, to]
  (let [z-to (-> to
                 (zipper/node->zipper)
                 (set (node to (node (:focus z)))))]
    (loop [z* z, z-to* z-to]
      (let [next-z* (advance-preorder-depth-first z*)]
        (assert (= (-> z* :focus node)
                   (-> z-to* :focus node)))
        (assert (not (nil? z-to*)))
        (cond
          (nil? next-z*)
          (commit z-to*)
          (empty? (children (:focus z*)))
          (recur next-z*
                 (advance-preorder-depth-first z-to*))
          :else
          (recur next-z*
                 (advance-preorder-depth-first (move-up (apply insert-down-left
                                                               (cons z-to* (map (partial node to) (map node (children (:focus z*))))))))))))))

