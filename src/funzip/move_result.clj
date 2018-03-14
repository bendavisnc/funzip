(ns funzip.move-result
  "Introduces the notion of a `move result`.
   This prevents ambiguity of a movement being nil because it's impossible,
   and a node having the value of nil."
  (:refer-clojure :exclude [map, get])
  (:require [funzip.zipper :refer [zipper?]]))

(def fail ::fail)
(def success ::success)

(defrecord MoveResult [zipper, origin, result])

(defn move-result? [m]
  (instance? MoveResult m))

(defn successful-move [& {:keys [zipper, origin]}]
  (->MoveResult zipper, origin, success))

(defn failed-move [& {:keys [origin]}]
  (->MoveResult nil, origin, fail))

(defn success? [m]
  (= (:result m) success))

(defn fail? [m]
  (= (:result m) fail))

(defn get [m]
  (cond
    (fail? m)
    nil
    (success? m)
    (:zipper m)))

(defn map [m, f]
  (cond
    (fail? m)
    m
    (success? m)
    (successful-move :zipper (f (:zipper m))
                     :origin (:origin m))))

(defn flatmap [m, f]
  (cond
    (fail? m)
    m
    (success? m)
    (f (:zipper m))))

(defn with-origin [m, origin]
  (cond
    (fail? m)
    (failed-move :origin origin)
    (success? m)
    (successful-move :zipper (:zipper m)
                     :origin origin)))

