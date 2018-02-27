(ns funzip.move-result
  (:refer-clojure :exclude [map, get]))

(def fail ::fail)
(def success ::success)

(defrecord MoveResult [zipper, origin, result])

(defn successful-move [& {:keys [zipper, origin]}]
  (new MoveResult zipper, origin, success))

(defn failed-move [& {:keys [origin]}]
  (new MoveResult nil, origin, fail))

(defn get [m]
  (case (:result m)
    fail nil
    success (:zipper m)))

(defn map [m, f]
  (case (:result m)
    fail nil
    success (f (:zipper m))))

(defn success? [m]
  (= (:result m) success))

(defn fail? [m]
  (= (:result m) fail))
