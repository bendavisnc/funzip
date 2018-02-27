(ns funzip.core
  (:refer-clojure :exclude [cycle])
  (:require [clojure.core.match :refer [match]]
            [funzip.move-result :as move-result]
            [funzip.zipper :as zipper]
            [funzip.unzip :refer [unzip, zip]]))

(defn move-to [origin, z]
  (move-result/successful-move :origin origin, :zipper z))

(defn fail [origin]
  (move-result/failed-move :origin origin))


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

;; Down - left

(defn try-move-down-left [z]
  (let [[head & tail] (unzip (:focus z))]
    (if (nil? tail)
      (fail z)
      ;else
      (move-to z
               (zipper/create-zipper :left nil
                                     :focus head
                                     :right tail
                                     :top z)))))

(defn move-down-left [z]
  (move-result/get (try-move-down-left z)))

;; Down - right

(defn cycle [z, move-fn]
  (loop [acc z]
    (let [result (move-fn acc)]
      (if (move-result/fail? result)
        acc
        ;else
        (recur (move-result/get result))))))

(defn rewind-right [z]
  (cycle z try-move-right))

;   def tryMoveDownRight = tryMoveDownLeft.map(_.rewindRight)
(defn try-move-down-right [z]
  (move-result/map (try-move-left z) rewind-right))

(defn move-down-right [z]
  (move-result/get (try-move-down-right z)))

