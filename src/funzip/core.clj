(ns funzip.core
  (:require [clojure.core.match :refer [match]])
  (:import (clojure.lang IPersistentMap)))

(defrecord Zipper [left, focus, right, top])

(defn create-zipper [& {:keys [left, focus, right, top]}]
  (new Zipper left, focus, right, top))

(defn node->zipper [n]
  (create-zipper :left nil
                 :focus n
                 :right nil
                 :top nil))

(defn copy-zipper [z & {:keys [left, focus, right, top]}]
  (create-zipper :left (or left (:left z))
                 :focus (or focus (:focus z))
                 :right (or right (:right z))
                 :top (or top (:top z))))

(defn move-left [z]
  (if (nil? (:left z))
    nil
    ;else
    (copy-zipper z
                 :left (rest (:left z))
                 :focus (first (:left z))
                 :right (cons (:focus z) (:right z)))))

(defn move-right [z]
  (if (nil? (:right z))
    nil
    ;else
    (copy-zipper z
                 :right (rest (:right z))
                 :focus (first (:right z))
                 :left (cons (:focus z) (:left z)))))

; trait Unzip[A] {
  ; def unzip(node: A): List[A]
  ; def zip(node: A, children: List[A]): A
; }

(defprotocol Unzip
  (unzip [this])
  (zip [this, node, children]))

(extend-protocol Unzip
  IPersistentMap
  (unzip [this]
    (:children this))
  (zip [this]
    nil))

(defn move-down-left [z]
  (let [unzipped (unzip (:focus z))]
    (if (nil? unzipped)
      nil
      ;else
      (create-zipper :left nil
                     :focus (first unzipped)
                     :right (rest unzipped)
                     :top z))))

(defn rewind-right [z])

(defn move-down-right [z]
  (let [down-left (move-down-left z)]
    (if (nil? down-left)
      nil
      ;else
      (rewind-right down-left))))


(defn testmatch []
  (let [x [1 2 3]]
    (match [x]
           [[_ _ 2]] :a0
           [[1 1 3]] :a1
           [[1 2 3]] :a2
           :else :a3)))



