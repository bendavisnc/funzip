
(require '[funzip.core :as funzip])
(require '[funzip.zipper :as funzip-zipper])
(require '[funzip.protocols :as fp])
(require '[clojure.string :as s])
(require '[clojure.pprint :as pp])
(require '[taipei-404.html :refer [html->hiccup]])

(def cat
  (first (html->hiccup "<p>My cat is <strong>very</strong> grumpy.</p>")))

(assert (= [:p "My cat is " [:strong "very"] " grumpy."] cat))

(extend-type clojure.lang.IPersistentVector
 fp/CanBeZipper
   (unzip [this]
     (assert (vector? this))
     ;(rest this))
     (subvec this 1))
   (zip [this children]
     (assert (vector? this))
     (vec (concat [(first this)] children))))

;; Stop when we get to a string.
(extend-type java.lang.String
 fp/CanBeZipper
   (unzip [this]
     nil))

(defn is-grumpy-cat?
  [cat]
  (boolean (funzip/find (funzip-zipper/node->zipper cat)
                        (comp #{"grumpy"}
                              #(cond-> %
                                 (string? %) (as-> $
                                               (filter Character/isLetterOrDigit
                                                       $)
                                               (s/join $)))))))

(assert (is-grumpy-cat? cat))

(defn grumpy-cat->happy-cat
  [cat]
  (-> cat
      funzip-zipper/node->zipper
      (funzip/find (comp #{"grumpy"}
                         #(cond-> %
                            (string? %) (as-> $
                                          (filter Character/isLetterOrDigit $)
                                          (s/join $)))))
      (funzip/update #(s/replace % "grumpy" "happy"))
      funzip/commit))

(assert (= [:p "My cat is " [:strong "very"] " happy."]
           (grumpy-cat->happy-cat cat)))

(assert (not (is-grumpy-cat? (grumpy-cat->happy-cat cat))))

