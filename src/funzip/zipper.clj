(ns funzip.zipper)

;;
;;
;; Defines a Zipper record and facilities for creation.

(defrecord Zipper [left, focus, right, top])

(defn zipper? [z]
  (instance? Zipper z))

(def top ::top)

(defn top? [z]
  (= top (:top z)))

(defn create-zipper [& args]
  (cond
    (:focus (apply hash-set args))
    (let [{:keys [left, focus, right, top] :or {top funzip.zipper/top}} args]
      (->Zipper left, focus, right, top))
    (= 1 (count args))
    (create-zipper :focus [(first args)]) ; default collection type = vector
    :else
      (throw (new IllegalArgumentException (str "Don't know how to create zipper with " args)))))

(defn node->zipper [n]
  (create-zipper :left nil
                 :focus n
                 :right nil
                 :top top))

(defn copy-zipper [z & {:keys [left, focus, right, top] :or {left (:left z), focus (:focus z), right (:right z), top (:top z)}}]
  (-> z
      (assoc :left left)
      (assoc :focus focus)
      (assoc :right right)
      (assoc :top top)))


