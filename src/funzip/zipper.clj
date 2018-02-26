(ns funzip.zipper)

(defrecord Zipper [left, focus, right, top])

(defn create-zipper [& {:keys [left, focus, right, top]}]
  (new Zipper left, focus, right, top))

(defn node->zipper [n]
  (create-zipper :left nil
                 :focus n
                 :right nil
                 :top nil))

(defn copy-zipper [z & {:keys [left, focus, right, top]}]
  (create-zipper :left (or left (:left z))
                 :focus (or focus (:focus z))
                 :right (or right (:right z))
                 :top (or top (:top z))))
