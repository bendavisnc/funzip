(ns funzip.tree-protocol-example-exts
  (:require [funzip.protocols :refer [CanBeZipper, CanConvertZipper]]))

(extend-type #?(:clj clojure.lang.IPersistentMap
                :cljs cljs.core.PersistentArrayMap)
  CanBeZipper
  (unzip [this]
    (:children this))
  (zip [this, children]
    (assoc this :children children))
  CanConvertZipper
  (node
    ([this]
     (:node this))
    ([this, v]
     (assoc this :node v))))

