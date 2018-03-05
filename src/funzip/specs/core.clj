(ns funzip.specs.core
  (:require [clojure.spec.alpha :as spec]
            [funzip.zipper :refer [zipper?]]
            [funzip.move-result :refer [move-result?]]))

(spec/fdef funzip.core/stay
           :args (spec/cat :z zipper?)
           :ret move-result?)

(spec/fdef funzip.core/move-to
           :args (spec/cat :origin zipper? :z zipper?)
           :ret move-result?)


(spec/fdef funzip.core/fail
           :args (spec/cat :origin zipper?)
           :ret move-result?)

(spec/fdef funzip.core/cycle
           :args (spec/cat :z zipper?, :move-fn fn?)
           :ret zipper?)

(spec/fdef funzip.core/try-repeat
           :args (spec/cat :z zipper?, :n integer?, :move-fn fn?)
           :ret move-result?)

(spec/fdef funzip.core/tap-focus
           :args (spec/cat :z zipper?, :f fn?)
           :ret zipper?)

(spec/fdef funzip.core/update
           :args (spec/cat :z zipper?, :f fn?)
           :ret zipper?)


(spec/fdef funzip.core/set
           :args (spec/cat :z zipper?, :v any?)
           :ret zipper?)

(spec/fdef funzip.core/try-move-left
           :args (spec/cat :z zipper?)
           :ret move-result?)

(spec/fdef funzip.core/move-left
           :args (spec/cat :z zipper?)
           :ret (spec/nilable zipper?))

(spec/fdef funzip.core/rewind-left
           :args (spec/cat :z zipper?)
           :ret (spec/nilable zipper?))

(spec/fdef funzip.core/try-move-left-by
           :args (spec/cat :z zipper?, :n integer?)
           :ret move-result?)

(spec/fdef funzip.core/move-left-by
           :args (spec/cat :z zipper?, :n integer?)
           :ret (spec/nilable zipper?))

(spec/fdef funzip.core/try-delete-and-move-left
           :args (spec/cat :z zipper?)
           :ret move-result?)

(spec/fdef funzip.core/delete-and-move-left
           :args (spec/cat :z zipper?)
           :ret (spec/nilable zipper?))


(spec/fdef funzip.core/try-move-right
           :args (spec/cat :z zipper?)
           :ret move-result?)

(spec/fdef funzip.core/move-right
           :args (spec/cat :z zipper?)
           :ret (spec/nilable zipper?))

(spec/fdef funzip.core/rewind-right
           :args (spec/cat :z zipper?)
           :ret (spec/nilable zipper?))

(spec/fdef funzip.core/try-move-right-by
           :args (spec/cat :z zipper?, :n integer?)
           :ret move-result?)

(spec/fdef funzip.core/move-right-by
           :args (spec/cat :z zipper?, :n integer?)
           :ret (spec/nilable zipper?))

(spec/fdef funzip.core/try-move-down-left
           :args (spec/cat :z zipper?)
           :ret move-result?)

(spec/fdef funzip.core/move-down-left
           :args (spec/cat :z zipper?)
           :ret (spec/nilable zipper?))

(spec/fdef funzip.core/try-move-down-right
           :args (spec/cat :z zipper?)
           :ret move-result?)

(spec/fdef funzip.core/move-down-right
           :args (spec/cat :z zipper?)
           :ret (spec/nilable zipper?))

(spec/fdef funzip.core/try-move-down-at
           :args (spec/cat :z zipper?, :i integer?)
           :ret move-result?)

(spec/fdef funzip.core/move-down-at
           :args (spec/cat :z zipper?, :i integer?)
           :ret (spec/nilable zipper?))


(spec/fdef funzip.core/try-move-up
           :args (spec/cat :z zipper?)
           :ret move-result?)

(spec/fdef funzip.core/move-up
           :args (spec/cat :z zipper?)
           :ret (spec/nilable zipper?))

(spec/fdef funzip.core/delete-and-move-up
           :args (spec/cat :z zipper?)
           :ret (spec/nilable zipper?))


