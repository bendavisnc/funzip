(ns funzip.zipper
  (:require [clojure.spec.alpha :as spec]
            ;[orchestra.spec.test :as spec-test]))
            [clojure.spec.test.alpha :as spec-test]))

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

(defn copy-zipper [z & {:keys [left, focus, right, top] :as newz}]
  (-> z
    (assoc :left (if (contains? newz :left) left (:left z)))
    (assoc :focus (if (contains? newz :focus) focus (:focus z)))
    (assoc :right (if (contains? newz :right) right (:right z)))
    (assoc :top (if (contains? newz :top) top (:top z)))))

(defn top? [z]
  (= top (:top z)))

(spec/fdef top?
           :args (spec/cat :prev-steps zipper?)
           :ret integer?)

(spec-test/instrument)


