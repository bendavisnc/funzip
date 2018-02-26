(ns funzip.move-result)

(def fail ::fail)
(def success ::success)

(defrecord MoveResult [zipper, origin, result])

(defn successful-move [& {:keys [zipper, origin]}]
  (new MoveResult zipper, origin, success))

(defn failed-move [& {:keys [origin]}]
  (new MoveResult nil, origin, fail))

(defn get [m]
  (case (:result m)
    fail fail
    success (:zipper m)))
