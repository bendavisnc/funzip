(require '[funzip.core :as funzip]   ; The core api that includes all
                                     ; functions for navigation and
                                     ; modification
         '[funzip.zipper :as zipper] ; ns for raw data -> zipper
         '[clojure.pprint :as pp])

(def cat
  (-> :cat
      zipper/create-zipper

      ;; Give the cat a head and body.
      (funzip/insert-down-left [:head] [:body])

      ;; Add some facial features.
      (funzip/insert-down-left
       [:eyes]
       [:nose]
       [:mouth])

      ;; Move to the body.
      funzip/move-up
      funzip/move-right

      ;; Give the cat some fur, paws, and legs.
      (funzip/insert-down-left
       [:fur]
       [:paws]
       [:legs])

      funzip/commit))


(assert (= [:cat
            [[:head [[:eyes] [:nose] [:mouth]]]
             [:body [[:fur] [:paws] [:legs]]]]]
           cat))

