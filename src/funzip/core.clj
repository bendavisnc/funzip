(ns funzip.core
  (:refer-clojure :exclude [cycle])
  (:require [clojure.core.match :refer [match]]
            [funzip.move-result :as move-result]
            [funzip.zipper :as zipper])
  (:import (clojure.lang IPersistentMap)))

(defn move-to [origin, z]
  (move-result/success :origin origin, :zipper z))

(defn fail [origin]
  (move-result/fail :origin origin))


;;
;;
;; Movement

;;
;; Sideways

;; Left

(defn try-move-left [z]
  (let [[head & tail] (:left z)]
    (if (nil? tail)
      (fail z)
      ;else
      (move-to z
               (zipper/copy-zipper z
                                   :left tail
                                   :focus head
                                   :right (cons (:focus z) (:right z)))))))

(defn move-left [z]
  (move-result/get (try-move-left z)))

;; Right

(defn try-move-right [z]
  (let [[head & tail] (:right z)]
    (if (nil? tail)
      (fail z)
      ;else
      (move-to z
               (zipper/copy-zipper z
                                   :right tail
                                   :focus head
                                   :left (cons (:focus z) (:left z)))))))

(defn move-right [z]
  (move-result/get (try-move-right z)))

  ;/** Unzip the current node and focus on the left child */
  ;def tryMoveDownLeft = unzip.unzip(focus) match {
  ;  case head :: tail ⇒
  ;    moveTo(copy(left = Nil, focus = head, right = tail, top = Some(this)))
  ;  case Nil ⇒ fail
  ;}

(defn try-move-down [z]
  (let [[head & tail] (:right z)]
    (if (nil? tail)
      (fail z)
      ;else
      (move-to z
               (zipper/copy-zipper z
                                   :right tail
                                   :focus head
                                   :left (cons (:focus z) (:left z)))))))


