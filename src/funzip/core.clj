(ns funzip.core
  (:refer-clojure :exclude [cycle, update, set])
  (:require [clojure.core.match :refer [match]]
            [funzip.move-result :as move-result]
            [funzip.zipper :as zipper]
            [funzip.unzip :refer [unzip, zip]]))


(defn stay [z]
  (move-result/successful-move :origin z, :zipper z))

(defn move-to [origin, z]
  (move-result/successful-move :origin origin, :zipper z))

(defn fail [origin]
  (move-result/failed-move :origin origin))


;;
;;
;; Movement

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

(defn tap-focus [z, f]
  (do
    (f (:focus z))
    z))

(defn update [z, f]
  (zipper/copy-zipper z
                      :focus (f (:focus z))))

(defn set [z, v]
  (zipper/copy-zipper z
                      :focus v))

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


  ;/** Delete the value in focus and move left */
  ;def tryDeleteAndMoveLeft = left match {
  ;  case head :: tail ⇒ moveTo(copy(left = tail, focus = head))
  ;  case Nil ⇒ fail
  ;}

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

  ;/** Zip the current layer and move up */
  ;def tryMoveUp = top.fold(fail) { z ⇒
  ;  moveTo {
  ;    z.copy(focus = {
  ;      val children = (focus :: left) reverse_::: right
  ;      unzip.zip(z.focus, children)
  ;    })
  ;  }
  ;}

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

