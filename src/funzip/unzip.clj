(ns funzip.unzip
  (:import (clojure.lang IPersistentMap)))

(defprotocol Unzip
  (unzip [this])
  (zip [this, node, children]))

(extend-protocol Unzip
  IPersistentMap
  (unzip [this]
    (:children this))
  (zip [this]
    nil))
