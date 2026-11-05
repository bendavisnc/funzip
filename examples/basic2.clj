(require '[funzip.core :as funzip])
(require '[funzip.zipper :as funzip-zipper])
(require '[funzip.protocols :as fp])
(require '[clojure.string :as s])
(require '[clojure.pprint :as pp])

(def cat
  {:children (list {:node :p}
                   {:node "My cat is "}
                   {:node [:strong "very"]}
                   {:node " grumpy."})})

(extend-type clojure.lang.IPersistentMap
 fp/CanBeZipper
   (unzip [this]
     (:children this))
   (zip [this children]
     (assoc this :children children)))

(defn is-grumpy-cat?
  [cat]
  (boolean (funzip/find (funzip-zipper/node->zipper cat)
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
      funzip-zipper/node->zipper
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
