(ns funzip.tree-protocol-example-exts
  (:require [clojure.xml :as xml]
            [funzip.protocols :refer [CanBeZipper]])
  (:import (clojure.lang IPersistentMap PersistentStructMap)))

(extend-type IPersistentMap
  CanBeZipper
  (unzip [this]
    (:children this))
  (zip [this, children]
    (assoc this :children children)))

(extend-type PersistentStructMap
  CanBeZipper
  (unzip [this]
    (when (-> this xml/content second)
      (-> this xml/content second xml/content)))
  (zip [this, children]
    (let [children-template (into (struct-map xml/element) {:tag :Children, :attrs nil})]
      (assoc-in this
                [:content 1]
                (assoc children-template :content (vec children))))))
