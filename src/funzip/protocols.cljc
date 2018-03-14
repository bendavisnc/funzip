(ns funzip.protocols
  "Defines the two relevant open protocols.

   `CanBeZipper` is the main protocol that lets a data structure be a zipper.
   `CanConvertZipper` is currently only necessary for funzip.core/into to work.
  "
  (:import (clojure.lang IPersistentVector)))

(defprotocol CanBeZipper
  "Defines all of the methods needed to support zipper functionality."
  (unzip [this]
    "Gets the children of this node.")
  (zip [this, children]
    "Sets the children of this node."))

(defprotocol CanConvertZipper
  "Defines a node getter and setter method for funzip.core/into to work."
  (node [this], [this, v]))

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
     (assoc this 0 v))))

