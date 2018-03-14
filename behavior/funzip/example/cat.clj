(ns funzip.example.cat
  (:require [funzip.zipper :as zipper]
            [funzip.core :as funzip]
            [speclj.core :refer :all]))


(def typical-house-cat
  (-> :cat
      (zipper/create-zipper)
      (funzip/insert-down-left [:head] [:body])
      (funzip/insert-down-left [:cute-eyes] [:cute-nose] [:adorable-smile])
      (funzip/move-up)
      (funzip/move-right)
      (funzip/insert-down-left [:soft-fur] [:playful-paws] [:bouncy-legs])
      (funzip/commit)))

(def atypical-house-cat
  (-> typical-house-cat
      (zipper/node->zipper)
      (funzip/find #(= :head (first %)))
      (funzip/move-down)
      (funzip/update #(assoc % 0 :laser-beam-eyes))
      (funzip/move-right)
      (funzip/move-right)
      (funzip/set [:smirk-of-hitler])
      (funzip/find #(= :body (first %)))
      (funzip/set [:body])
      (funzip/insert-down-right [:hyper-allergic-fur] [:ninja-assassin-paws] [:bionic-legs])
      (funzip/commit)))


(describe "Two similar but different sorts of cats."
          (describe "Typical house cat:"
                    (it "Should represent a structured model of a cat"
                        (should= [:cat [[:head [[:cute-eyes] [:cute-nose] [:adorable-smile]]]
                                        [:body [[:soft-fur] [:playful-paws] [:bouncy-legs]]]]]
                                 typical-house-cat)))
          (describe "Atypical house cat:"
                    (it "Should represent a structured model of a ...somewhat modified cat"
                        (should= [:cat [[:head [[:laser-beam-eyes] [:cute-nose] [:smirk-of-hitler]]]
                                        [:body [[:hyper-allergic-fur] [:ninja-assassin-paws] [:bionic-legs]]]]]
                                 atypical-house-cat))))






