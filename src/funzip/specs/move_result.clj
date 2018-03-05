(ns funzip.specs.move-result
  (:refer-clojure :exclude [map, get])
  (:require [clojure.spec.alpha :as spec]
            [funzip.zipper :refer [zipper?]]
            [funzip.move-result :refer [move-result? fail, success]]))


(spec/fdef funzip.move-result/->MoveResult
           :args (spec/cat :zipper (spec/nilable zipper?), :origin zipper?, :result #{fail, success})
           :ret move-result?)

(spec/def ::zipper zipper?)
(spec/def ::origin zipper?)

(spec/fdef funzip.move-result/successful-move
           :args (spec/cat :kwargs (spec/keys* :req-un [::zipper ::origin]))
           :ret move-result?)


(spec/fdef funzip.move-result/failed-move
           :args (spec/cat :kwargs (spec/keys* :req-un [::origin]))
           :ret move-result?)

(spec/fdef funzip.move-result/success?
           :args (spec/cat :m move-result?)
           :ret boolean?)


(spec/fdef funzip.move-result/fail?
           :args (spec/cat :m move-result?)
           :ret boolean?)

(spec/fdef funzip.move-result/get
           :args (spec/cat :m move-result?)
           :ret (spec/nilable zipper?))

(spec/def ::map-fn (spec/fspec :args (spec/cat :z zipper?)
                               :ret zipper?))

(spec/fdef funzip.move-result/map
           :args (spec/cat :m move-result?, :f fn?)
           :ret (spec/nilable move-result?))

(spec/fdef funzip.move-result/with-origin
           :args (spec/cat :m move-result?, :origin zipper?)
           :ret move-result?)

