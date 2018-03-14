(ns funzip.zipper
  "Defines a Zipper record and facilities for creation."
  (:require [funzip.protocols :refer [CanBeZipper, CanConvertZipper]]))


(defrecord Zipper [left,  ; A list of all of the child nodes left of the current focus.
                   focus, ; The current focus
                   right, ; A list of all of the child nodes right of the current focus.
                   top])  ; A reference zipper before moving down, or `::top` to indicate that we're at the very top.

(defn zipper? [z]
  (instance? Zipper z))

(defn zipperable?
  "Can the data collection type of n (eg, PersistentVector) be used as a zipper?"
  [n]
  (satisfies? CanBeZipper n))

(defn convertable?
  "Can the data collection type of n (eg, PersistentVector) be used as a zipper AND convert from a zipper of a different type?"
  [n]
  (and (zipperable? n)
       (satisfies? CanConvertZipper n)))

(def top ::top)

(defn top?
  "Is the supplied zipper focused at the very top of the structure?"
  [z]
  (= top (:top z)))

(defn create-zipper
  "A convenience method for creating a zipper.
   Supports two sorts of arities.
   Either a keyval set corresponding to a Zipper record, with at least a  keyval for :focus,
   or a single value that will be used as the root focus of a new zipper, in a (default) vector structure."
  [& args]
  (cond
    (:focus (apply hash-set args))
    (let [{:keys [left, focus, right, top] :or {top funzip.zipper/top}} args]
      (->Zipper left, focus, right, top))
    (= 1 (count args))
    (create-zipper :focus [(first args)]) ; default collection type = vector
    :else
      (throw (new IllegalArgumentException (str "Don't know how to create zipper with " args)))))

(defn node->zipper
  "Returns a zipper given a root node, ie a raw tree like data structure."
  [n]
  (create-zipper :left nil
                 :focus n
                 :right nil
                 :top top))

(defn copy-zipper
  "Copies a supplied zipper, overriding any supplied fields corresponding to Zipper record fields."
  [z & {:keys [left, focus, right, top] :or {left (:left z), focus (:focus z), right (:right z), top (:top z)}}]
  (-> z
      (assoc :left left)
      (assoc :focus focus)
      (assoc :right right)
      (assoc :top top)))


