(ns funzip.traversal-utils)

(defn preorder [t]
      (letfn [(preorder* [acc t]
                         (do
                           (swap! acc conj (first t))
                           (doseq [c (second t)]
                                  (preorder* acc c))))]
             (let [a (atom [])]
                  (do
                    (preorder* a t)
                    (deref a)))))


(defn postorder [t]
      (letfn [(postorder* [acc t]
                          (do
                            (doseq [c (second t)]
                                   (postorder* acc c))
                            (swap! acc conj (first t))))]
             (let [a (atom [])]
                  (do
                    (postorder* a t)
                    (deref a)))))



