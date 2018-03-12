(ns funzip.protocols
  (:import (clojure.lang IPersistentVector)))

(defprotocol CanBeZipper
  (unzip [this])
  (zip [this, children]))

(extend-type IPersistentVector
  CanBeZipper
  (unzip [this]
    (second this))
  (zip [this, children]
    (assoc this 1 children)))

