(ns funzip.zipper
  (:require [clojure.spec.alpha :as spec]))

;;
;;
;; Defines a Zipper record and facilities for creation.

(defrecord Zipper [left, focus, right, top])

(defn zipper? [z]
  (instance? Zipper z))

(def top ::top)

(defn create-zipper [& {:keys [left, focus, right, top]}]
  (new Zipper left, focus, right, top))

(defn node->zipper [n]
  (create-zipper :left nil
                 :focus n
                 :right nil
                 :top top))

(defn copy-zipper [z & {:keys [left, focus, right, top] :or {left (:left z), focus (:focus z), right (:right z), top (:top z)}}]
  (-> z
      (assoc :left left)
      (assoc :focus focus)
      (assoc :right right)
      (assoc :top top)))

(defn top? [z]
  (= top (:top z)))

(spec/fdef top?
           :args (spec/cat :z zipper?)
           :ret boolean?)



