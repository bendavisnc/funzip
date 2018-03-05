(ns funzip.core
  (:refer-clojure :exclude [cycle, update, set])
  (:require [funzip.move-result :as move-result :refer [move-result?]]
            [funzip.zipper :as zipper :refer [zipper?]]
            [funzip.unzip :refer [unzip, zip]]
            [clojure.spec.alpha :as spec]))


(defn stay [z]
  (move-result/successful-move :origin z, :zipper z))

(spec/fdef stay
           :args (spec/cat :z zipper?)
           :ret move-result?)

(defn move-to [origin, z]
  (move-result/successful-move :origin origin, :zipper z))

(spec/fdef move-to
           :args (spec/cat :origin zipper? :z zipper?)
           :ret move-result?)

(defn fail [origin]
  (move-result/failed-move :origin origin))

(spec/fdef fail
           :args (spec/cat :origin zipper?)
           :ret move-result?)


;;
;;
;; Movement

;;
;; Utils

(defn cycle [z, move-fn]
  (loop [acc z]
    (let [result (move-fn acc)]
      (if (move-result/fail? result)
        acc
        ;else
        (recur (move-result/get result))))))

(spec/fdef cycle
           :args (spec/cat :z zipper?, :move-fn fn?)
           :ret zipper?)

(defn try-repeat [z, n, move-fn]
  (move-result/with-origin
    (loop [n* n, acc z]
      (if (< n* 1)
        (stay acc)
        ;else
        (let [result (move-fn acc)]
          (if (move-result/fail? result)
            result
            ;else
            (recur (dec n*) (move-result/get result))))))
    z))

(spec/fdef try-repeat
           :args (spec/cat :z zipper?, :n integer?, :move-fn fn?)
           :ret move-result?)

(defn tap-focus [z, f]
  (do
    (f (:focus z))
    z))

(spec/fdef tap-focus
           :args (spec/cat :z zipper?, :f fn?)
           :ret zipper?)

(defn update [z, f]
  (zipper/copy-zipper z
                      :focus (f (:focus z))))
(spec/fdef update
           :args (spec/cat :z zipper?, :f fn?)
           :ret zipper?)

(defn set [z, v]
  (zipper/copy-zipper z
                      :focus v))

(spec/fdef set
           :args (spec/cat :z zipper?, :v any?)
           :ret zipper?)
;;
;; Sideways

;; Left

(defn try-move-left [z]
  (let [[head & tail] (:left z)]
    (if (nil? head)
      (fail z)
      ;else
      (move-to z
               (zipper/copy-zipper z
                                   :left tail
                                   :focus head
                                   :right (cons (:focus z) (:right z)))))))

(spec/fdef try-move-left
           :args (spec/cat :z zipper?)
           :ret move-result?)

(defn move-left [z]
  (move-result/get (try-move-left z)))

(spec/fdef move-left
           :args (spec/cat :z zipper?)
           :ret (spec/nilable zipper?))

(defn rewind-left [z]
  (cycle z try-move-left))

(spec/fdef rewind-left
           :args (spec/cat :z zipper?)
           :ret (spec/nilable zipper?))

(defn try-move-left-by [z, n]
  (try-repeat z, n, try-move-left))

(spec/fdef try-move-left-by
           :args (spec/cat :z zipper?, :n integer?)
           :ret move-result?)

(defn move-left-by [z, n]
  (move-result/get (try-move-left-by z n)))

(spec/fdef move-left-by
           :args (spec/cat :z zipper?, :n integer?)
           :ret (spec/nilable zipper?))

;; Right

(defn try-move-right [z]
  (let [[head & tail] (:right z)]
    (if (nil? head)
      (fail z)
      ;else
      (move-to z
               (zipper/copy-zipper z
                                   :right tail
                                   :focus head
                                   :left (cons (:focus z) (:left z)))))))

(spec/fdef try-move-right
           :args (spec/cat :z zipper?)
           :ret move-result?)

(defn move-right [z]
  (move-result/get (try-move-right z)))

(spec/fdef move-right
           :args (spec/cat :z zipper?)
           :ret (spec/nilable zipper?))

(defn rewind-right [z]
  (cycle z try-move-right))

(spec/fdef rewind-right
           :args (spec/cat :z zipper?)
           :ret (spec/nilable zipper?))

(defn try-move-right-by [z, n]
  (try-repeat z, n, try-move-right))

(spec/fdef try-move-right-by
           :args (spec/cat :z zipper?, :n integer?)
           :ret move-result?)

(defn move-right-by [z, n]
  (move-result/get (try-move-right-by z n)))

(spec/fdef move-right-by
           :args (spec/cat :z zipper?, :n integer?)
           :ret (spec/nilable zipper?))

;; Down - left

(defn try-move-down-left [z]
  (let [[head & tail] (unzip (:focus z))]
    (if (nil? head)
      (fail z)
      ;else
      (move-to z
               (zipper/copy-zipper z
                                   :left nil
                                   :focus head
                                   :right tail
                                   :top z)))))

(spec/fdef try-move-down-left
           :args (spec/cat :z zipper?)
           :ret move-result?)

(defn move-down-left [z]
  (move-result/get (try-move-down-left z)))

(spec/fdef move-down-left
           :args (spec/cat :z zipper?)
           :ret (spec/nilable zipper?))

;; Down - right

(defn try-move-down-right [z]
  (move-result/map (try-move-down-left z) rewind-right))

(spec/fdef try-move-down-right
           :args (spec/cat :z zipper?)
           :ret move-result?)

(defn move-down-right [z]
  (move-result/get (try-move-down-right z)))

(spec/fdef move-down-right
           :args (spec/cat :z zipper?)
           :ret (spec/nilable zipper?))

;; Down

(defn try-move-down-at [z, i]
  (move-result/map (try-move-down-left z) #(move-right-by % i)))

(spec/fdef try-move-down-at
           :args (spec/cat :z zipper?, :i integer?)
           :ret move-result?)

(defn move-down-at [z, i]
  (move-result/get (try-move-down-at z i)))

(spec/fdef move-down-at
           :args (spec/cat :z zipper?, :i integer?)
           :ret (spec/nilable zipper?))

(defn try-delete-and-move-left [z]
  (let [[head & tail] (:left z)]
    (if (nil? head)
      (fail z)
      ;else
      (move-to z
               (zipper/copy-zipper z
                                   :left tail
                                   :focus head)))))

(spec/fdef try-delete-and-move-left
           :args (spec/cat :z zipper?)
           :ret move-result?)

(defn delete-and-move-left [z]
  (move-result/get (try-delete-and-move-left z)))

(spec/fdef delete-and-move-left
           :args (spec/cat :z zipper?)
           :ret (spec/nilable zipper?))

;; Up

(defn try-move-up [z]
  (let [head (:top z)]
    (if (zipper/top? z)
      (fail z)
      (move-to z
               (zipper/copy-zipper head
                                   :focus (let [children
                                                 (-> (:focus z)
                                                     (cons (:left z))
                                                     (reverse)
                                                     (concat (:right z)))]
                                            (zip (:focus head) children)))))))

(spec/fdef try-move-up
           :args (spec/cat :z zipper?)
           :ret move-result?)

(defn move-up [z]
  (move-result/get (try-move-up z)))

(spec/fdef move-up
           :args (spec/cat :z zipper?)
           :ret (spec/nilable zipper?))

(defn try-delete-and-move-up [z]
  (let [head (:top z)]
    (if (zipper/top? z)
      (fail z)
      (move-to z
               (zipper/copy-zipper head
                                   :focus (let [children
                                                (-> (:left z)
                                                    (reverse)
                                                    (concat (:right z)))]
                                            (zip (:focus head) children)))))))

(spec/fdef try-delete-and-move-up
           :args (spec/cat :z zipper?)
           :ret move-result?)

(defn delete-and-move-up [z]
  (move-result/get (try-delete-and-move-up z)))

(spec/fdef delete-and-move-up
           :args (spec/cat :z zipper?)
           :ret (spec/nilable zipper?))
