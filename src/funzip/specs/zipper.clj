(ns funzip.specs.zipper
  (:require [clojure.spec.alpha :as spec]
            [funzip.zipper :refer [zipper?]]))

(spec/fdef funzip.zipper/top?
           :args (spec/cat :z zipper?)
           :ret boolean?)


