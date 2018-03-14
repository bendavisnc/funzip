(ns funzip.move-result
  "Introduces the notion of a `move result`.
   This prevents ambiguity of a movement being nil because it's impossible,
   and a node having the value of nil."
  (:refer-clojure :exclude [map, get])
  (:require [funzip.zipper :refer [zipper?]]))

(def fail ::fail)
(def success ::success)

(defrecord MoveResult [zipper, ; The current zipper.
                       origin, ; The original zipper before trying the relevant move.
                       result]); Indicates whether the relevant move was a success or failure.

(defn move-result? [m]
  (instance? MoveResult m))

(defn successful-move
  "Convenience method to create a successful move result with the supplied zippers: origin and zipper."
  [& {:keys [zipper, origin]}]
  (->MoveResult zipper, origin, success))

(defn failed-move
  "Convenience method to create a failed move result with the supplied origin zipper."
  [& {:keys [origin]}]
  (->MoveResult nil, origin, fail))

(defn success?
  "Was the move successful?"
  [m]
  (= (:result m) success))

(defn fail?
  "Was the move NOT successful?"
  [m]
  (= (:result m) fail))

(defn get
  "Returns the underlying zipper or nil upon failure."
  [m]
  (cond
    (fail? m)
    nil
    (success? m)
    (:zipper m)))

(defn map
  "Applies f the underlying zipper of m if m is a successful move. Simply returns m otherwise.
   `f` should accept and return a zipper."
  [m, f]
  (cond
    (fail? m)
    m
    (success? m)
    (successful-move :zipper (f (:zipper m))
                     :origin (:origin m))))

(defn flatmap
  "Applies f the underlying zipper of m if m is a successful move. Simply returns m otherwise.
   `f` should accept a zipper and return a move result."
  [m, f]
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

