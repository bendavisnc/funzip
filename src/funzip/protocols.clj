(ns funzip.protocols
  (:import (clojure.lang IPersistentVector)))

(defprotocol CanBeZipper
  (unzip [this])
  (zip [this, children]))

(defprotocol CanConvertZipper
  (node [this], [this, v])
  (children [this]))


(extend-type IPersistentVector
  CanBeZipper
  (unzip [this]
    (second this))
  (zip [this, children]
    (assoc this 1 (vec children)))

  CanConvertZipper
  (node
    ([this]
     (first this))
    ([this, v]
     (assoc this 0 v)))
  (children [this]
    (second this)))
