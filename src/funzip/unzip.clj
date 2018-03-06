(ns funzip.unzip
  (:import (clojure.lang IPersistentVector)))

(defprotocol Unzip
  (unzip [this])
  (zip [this, children])
  (node [this] [this v]))

(extend-type IPersistentVector
  Unzip
  (unzip [this]
    (second this))
  (zip [this, children]
    (assoc this 1 children))
  (node
    ([this]
     (first this))
    ([this, v]
     (assoc this 0 v))))
