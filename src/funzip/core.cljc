(ns funzip.core
  "A protocol based implementation of Huet's zipper.

   A zipper is a mechanism for traversing and updating nodes in any tree like data structure.

   See `https://en.wikipedia.org/wiki/Zipper_(data_structure)`

   This implementation is a port of a Scala library that can be found here: `https://github.com/stanch/zipper`

   See funzip.zipper for facilities for creating a zipper and funzip.protocols for the actual protocols that enable creation (and conversion)."
  (:refer-clojure :exclude [cycle, update, set, repeat, into, find])
  (:require [funzip.move-result :as move-result :refer [move-result?]]
            [funzip.zipper :as zipper :refer [zipper?]]
            [funzip.protocols :refer [unzip, zip, node]]))


(defn stay
  "Returns a successful move result with the supplied zipper as also the origin."
  [z]
  (move-result/successful-move :origin z, :zipper z))

(defn move-to
  "Returns a successful move result with the supplied origin and zipper."
  [origin, z]
  (move-result/successful-move :origin origin, :zipper z))

(defn fail
  "Returns a failed move with the supplied origin."
  [origin]
  (move-result/failed-move :origin origin))

;;
;; Utils

(defn cycle
  "Returns the zipper of the last successful move-result before failure, by repeatedly calling the supplied move-fn on its own result.
   The supplied move-fn should take a zipper and return a move result."
  [z, move-fn]
  (loop [acc z]
    (let [result (move-fn acc)]
      (if (move-result/fail? result)
        acc
        ;else
        (recur (move-result/get result))))))

(defn try-repeat
  "Returns the last successful move result by repeatedly calling the supplied move-fn on its own result n times.
   The supplied move-fn should take a zipper and return a move result."
  [z, n, move-fn]
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

(defn repeat
  "Returns the zipper of the last successful move result (or nil) by repeatedly calling the supplied move-fn on its own result n times.
   The supplied move-fn should take a zipper and return a move result."
  [z, n, move-fn]
  (move-result/get (try-repeat z n move-fn)))

(defn tap-focus
  "Calls f on the focus of the supplied zipper, and returns the supplied zipper."
  [z, f]
  (do
    (f (:focus z))
    z))

(defn set
  "Returns a zipper with a new focus that is the value of the supplied v.
   `v` should be a data type that satisfies the CanBeZipper protocol."
  [z, v]
  (zipper/copy-zipper z
                      :focus v))

(defn update [z, f]
  "Returns a zipper with a new focus that is the result of calling f on the old focus.
   `f` should be a function that takes one parameter that satisfies the CanBeZipper protocol and return a value of the same type."
  (clojure.core/update z :focus f))

(defn first-success [z & moves]
  "Returns the first successful move on z.
   Each supplied move should be a function that takes a zipper and returns a move result."
  (loop [moves* moves, acc nil]
    (cond
      (and acc (move-result/success? acc))
      acc
      (empty? moves*)
      (fail z)
      :else
      (recur (rest moves*)
             ((first moves*) z)))))

(declare advance-preorder-depth-first)
(declare move-to-top)

(defn find [z, pred]
  "Traverses the supplied z and stops when pred returns true on the current focus."
  (loop [z* (move-to-top z)]
    (cond
      (nil? z*)
      nil
      (pred (:focus z*))
      z*
      :else
      (recur (advance-preorder-depth-first z*)))))

;;
;;
;; Movement

;;
;; Sideways

;; Left

(defn try-move-left
  "Returns the move result of moving the supplied zipper one node to the left."
  [z]
  (let [[head & tail] (:left z)]
    (if (nil? head)
      (fail z)
      ;else
      (move-to z
               (zipper/copy-zipper z
                                   :left tail
                                   :focus head
                                   :right (cons (:focus z) (:right z)))))))


(defn move-left
  "Returns the zipper of the move result of moving the supplied zipper one node to the left, or nil upon failure."
  [z]
  (move-result/get (try-move-left z)))


(defn rewind-left
  "Returns a zipper focused at the left-most node."
  [z]
  (cycle z try-move-left))


(defn try-move-left-by
  "Returns the move result of moving left n times."
  [z, n]
  (try-repeat z, n, try-move-left))


(defn move-left-by
  "Returns the zipper of the move result of moving left n times, or nil upon failure."
  [z, n]
  (move-result/get (try-move-left-by z n)))


(defn try-delete-and-move-left
  "Returns the move result of deleting the currently focused node and moving left."
  [z]
  (let [[head & tail] (:left z)]
    (if (nil? head)
      (fail z)
      ;else
      (move-to z
               (zipper/copy-zipper z
                                   :left tail
                                   :focus head)))))


(defn delete-and-move-left
  "Returns the zipper of the move result of deleting the currently focused node and moving left, or nil upon failure."
  [z]
  (move-result/get (try-delete-and-move-left z)))


;; Right

(defn try-move-right
  "Returns the move result of moving the supplied zipper one node to the right."
  [z]
  (let [[head & tail] (:right z)]
    (if (nil? head)
      (fail z)
      ;else
      (move-to z
               (zipper/copy-zipper z
                                   :right tail
                                   :focus head
                                   :left (cons (:focus z) (:left z)))))))


(defn move-right
  "Returns the zipper of the move result of moving the supplied zipper one node to the right, or nil upon failure."
  [z]
  (move-result/get (try-move-right z)))


(defn rewind-right
  "Returns a zipper focused at the right-most node."
  [z]
  (cycle z try-move-right))


(defn try-move-right-by
  "Returns the move result of moving right n times."
  [z, n]
  (try-repeat z, n, try-move-right))


(defn move-right-by
  "Returns the zipper of the move result of moving right n times, or nil upon failure."
  [z, n]
  (move-result/get (try-move-right-by z n)))


;; Down - left

(defn try-move-down-left
  "Returns the move result of moving to the left most child node."
  [z]
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


(defn move-down-left
  "Returns the zipper of the move result of moving to the left most child node, or nil upon failure."
  [z]
  (move-result/get (try-move-down-left z)))

(defn try-insert-down-left
  "Returns the move result of moving down left with the supplied values prepended."
  [z, & values]
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

(defn insert-down-left
  "Returns the zipper of the move result of moving down left with the supplied values prepended, or nil upon failure."
  [z, & values]
  (move-result/get (apply try-insert-down-left (cons z values))))


;; Down - right

(defn try-move-down-right
  "Returns the move result of moving to the right most child node."
  [z]
  (move-result/map (try-move-down-left z) rewind-right))


(defn move-down-right
  "Returns the zipper of the move result of moving to the right most child node, or nil upon failure."
  [z]
  (move-result/get (try-move-down-right z)))


;; Down

(defn move-down
  "Returns the zipper of the move result of moving to the left most child node, or nil upon failure.
   Alias of move-down-left."
  [z]
  (move-down-left z))

(defn try-move-down-at
  "Returns the move result of moving down and focusing on the i'th child."
  [z, i]
  (move-result/map (try-move-down-left z) #(move-right-by % i)))


(defn move-down-at
  "Returns the zipper of the move result of moving down and focusing on the i'th child, or nil upon failure."
  [z, i]
  (move-result/get (try-move-down-at z i)))


(defn try-insert-down-right
  "Returns the move result of moving down right with the supplied values appended."
  [z, & values]
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

(defn insert-down-right
  "Returns the zipper of the move result of moving down right with the supplied values appended, or nil upon failure."
  [z, & values]
  (move-result/get (apply try-insert-down-right z values)))

;; Up

(defn try-move-up
  "Returns the move result of moving up one level in the zipper structure."
  [z]
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


(defn move-up
  "Returns the zipper of the move result of moving up one level in the zipper structure, or nil upon failure."
  [z]
  (move-result/get (try-move-up z)))

(defn move-to-top
  [z]
  "Returns a zipper focused at the top most (root) of the zipper structure."
  (cycle z try-move-up))

(defn try-delete-and-move-up
  "Returns the move result of deleting the current focus and moving up one level in the zipper structure."
  [z]
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


(defn delete-and-move-up
  "Returns the zipper of the move result of deleting the current focus and moving up one level in the zipper structure, or nil upon failure."
  [z]
  (move-result/get (try-delete-and-move-up z)))

;; Traversal

(defn try-advance-preorder-depth-first
  "Returns the move result of moving to the next node in the zipper structure in a left to right, depth first traversal."
  [z]
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

(defn advance-preorder-depth-first
  "Returns zipper of the move result of moving to the next node in the zipper structure in a left to right, depth first traversal, or nil upon failure."
  [z]
  (move-result/get (try-advance-preorder-depth-first z)))

(defn commit
  "Returns the raw data structure (in its current state) behind the zipper."
  [z]
  (:focus (move-to-top z)))

(defn preorder-seq [z]
  "Returns a lazy seq of all of the nodes of the zipper, in a left to right, depth first order."
  (letfn [(->seq* [m]
            (if (move-result/fail? m)
              nil
              ;else
              (let [z* (move-result/get m)]
                (lazy-seq (cons (:focus z*)
                                (->seq* (try-advance-preorder-depth-first z*)))))))]
    (->seq* (stay z))))

;; Conversion

(defn into [z, to]
  "Returns a raw data structure populated with the contents of the supplied zipper.
   `to` should be a data type that satisfies both protocols, CanBeZipper and CanConvertZipper."
  (let [z-to (zipper/create-zipper :focus (node to (node (:focus z))))]
    (loop [z* z, z-to* z-to]
      (let [next-z* (advance-preorder-depth-first z*)]
        (cond
          (nil? next-z*)
          (commit z-to*)
          (empty? (unzip (:focus z*)))
          (recur next-z*
                 (advance-preorder-depth-first z-to*))
          :else
          (recur next-z*
                 (advance-preorder-depth-first (move-up (apply insert-down-left
                                                               z-to*
                                                               (map (partial node to) (map node (unzip (:focus z*)))))))))))))





