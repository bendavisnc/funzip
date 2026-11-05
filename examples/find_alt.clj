(require '[funzip.core :as funzip]
         '[funzip.zipper :as zipper]
         '[funzip.protocols :as protocols]
         '[clojure.string :as s]
         '[clojure.pprint :as pp])

(def cat
  {:children (list {:node :p}
                   {:node "My cat is "}
                   {:node [:strong "very"]}
                   {:node " grumpy."})})

(extend-type clojure.lang.IPersistentMap
 protocols/CanBeZipper
   (unzip [this]
     (:children this))
   (zip [this children]
     (assoc this :children children)))

(defn is-grumpy-cat?
  [cat]
  (boolean (funzip/find (zipper/node->zipper cat)
                        (comp #{"grumpy"}
                              #(cond-> %
                                 (string? %) (as-> $
                                               (filter Character/isLetterOrDigit
                                                       $)
                                               (s/join $)))
                              :node))))

(assert (is-grumpy-cat? cat))

(defn grumpy-cat->happy-cat
  [cat]
  (-> cat
      zipper/node->zipper
      (funzip/find (comp #{"grumpy"}
                         #(cond-> %
                            (string? %) (as-> $
                                          (filter Character/isLetterOrDigit $)
                                          (s/join $)))
                         :node))
      (funzip/update
       #(update % :node (fn [node] (s/replace node "grumpy" "happy"))))
      funzip/commit))

(assert (= {:children (list {:node :p}
                            {:node "My cat is "}
                            {:node [:strong "very"]}
                            {:node " happy."})}
           (grumpy-cat->happy-cat cat)))

(assert (not (is-grumpy-cat? (grumpy-cat->happy-cat cat))))
