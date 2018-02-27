(ns funzip.move-result
  (:refer-clojure :exclude [map, get]))

(def fail ::fail)
(def success ::success)

(defrecord MoveResult [zipper, origin, result])

(defn successful-move [& {:keys [zipper, origin]}]
  (new MoveResult zipper, origin, success))

(defn failed-move [& {:keys [origin]}]
  (new MoveResult nil, origin, fail))

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
    nil
    (success? m)
    (successful-move :zipper (f (:zipper m))
                     :origin m)))

