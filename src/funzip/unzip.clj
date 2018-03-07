(ns funzip.unzip
  (:import (clojure.lang IPersistentVector)))

(defprotocol Unzip
  (unzip [this])
  (zip [this, children]))

(extend-type IPersistentVector
  Unzip
  (unzip [this]
    (second this))
  (zip [this, children]
    (assoc this 1 children)))

