(ns funzip.move-result
  (:refer-clojure :exclude [map, get])
  (:require [clojure.spec.alpha :as spec]
            [funzip.zipper :refer [zipper?]]))

(def fail ::fail)
(def success ::success)

(defrecord MoveResult [zipper, origin, result])

(defn move-result? [m]
  (instance? MoveResult m))


(spec/fdef ->MoveResult
           :args (spec/cat :zipper (spec/nilable zipper?), :origin zipper?, :result #{fail, success})
           :ret move-result?)

(spec/def ::zipper zipper?)
(spec/def ::origin zipper?)

(defn successful-move [& {:keys [zipper, origin]}]
  (->MoveResult zipper, origin, success))

(spec/fdef successful-move
           :args (spec/cat :kwargs (spec/keys* :req-un [::zipper ::origin]))
           :ret move-result?)


(defn failed-move [& {:keys [origin]}]
  (->MoveResult nil, origin, fail))

(spec/fdef failed-move
           :args (spec/cat :kwargs (spec/keys* :req-un [::origin]))
           :ret move-result?)


(defn success? [m]
  (= (:result m) success))

(spec/fdef success?
           :args (spec/cat :m move-result?)
           :ret boolean?)


(defn fail? [m]
  (= (:result m) fail))

(spec/fdef fail?
           :args (spec/cat :m move-result?)
           :ret boolean?)

(defn get [m]
  (cond
    (fail? m)
    nil
    (success? m)
    (:zipper m)))

(spec/fdef get
           :args (spec/cat :m move-result?)
           :ret (spec/nilable zipper?))


(defn map [m, f]
  (cond
    (fail? m)
    nil
    (success? m)
    (successful-move :zipper (f (:zipper m))
                     :origin (:origin m))))

(spec/fdef map
           :args (spec/cat :m move-result?, :f fn?)
           :ret (spec/nilable move-result?))


(defn with-origin [m, origin]
  (cond
    (fail? m)
    (failed-move :origin origin)
    (success? m)
    (successful-move :zipper (:zipper m)
                     :origin origin)))

(spec/fdef with-origin
           :args (spec/cat :m move-result?, :origin zipper?)
           :ret move-result?)


