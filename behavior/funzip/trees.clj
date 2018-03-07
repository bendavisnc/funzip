(ns funzip.trees)


(def tree-simple-map {:node 1 :children [{:node 2,
                                          :children [{:node 4}, {:node 5}],}
                                         {:node 3}]})

(def tree-simple-vec [1, [[2, [[4], [5]]]
                          [3]]])

(def tree-simple-vec-mirrored [1, [[3],
                                   [2, [[5], [4]]]]])

(def tree-map {:node   1
               :children [{:node 11
                           :children [{:node 111}, {:node 112}]},
                          {:node 12
                           :children [{:node 121},
                                      {:node 122
                                       :children [{:node 1221}, {:node 1222}]}
                                      {:node 123}]},
                          {:node 13}]})

(def tree-vec [1 [[11, [[111], [112]]]
                  [12, [[121],
                        [122, [[1221], [1222]]]
                        [123]]]
                  [13]]])
