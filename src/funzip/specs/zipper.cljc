(ns funzip.specs.zipper
  (:require [clojure.spec.alpha :as spec]
            [funzip.zipper :refer [zipper?, zipperable?]]))

(spec/fdef funzip.zipper/top?
           :args (spec/cat :z zipper?)
           :ret boolean?)

(spec/fdef funzip.zipper/create-zipper
           :args (spec/or :single-val
                          #(= (count %) 1)
                          :zipper-fields
                          (spec/or :zipper-field-key #{:left :focus :right :top},
                                   :zipper-field-val (spec/nilable (spec/or :child-nodes coll? :top-ind #{:funzip.zipper/top}))))
           :ret zipper?)

(spec/fdef funzip.zipper/node->zipper
           :args (spec/cat :n zipperable?)
           :ret zipper?)

(spec/fdef funzip.zipper/copy-zipper
           :args (spec/cat :z zipper?
                           :zipper-fields
                           (spec/+ (spec/or :zipper-field-key #{:left :focus :right :top},
                                            :zipper-field-val (spec/nilable (spec/or :child-nodes coll? :top-ind #{:funzip.zipper/top})))))
           :ret zipper?)

