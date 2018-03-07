(ns funzip.trees)


(def tree-a {:node 1 :children [{:node 2,
                                 :children [{:node 4}, {:node 5}],}
                                {:node 3}]})

(def tree-a2 [1, [[2, [[4], [5]]]
                  [3]]])

(def tree-a2-mirrored [1, [[3],
                           [2, [[5], [4]]]]])

(def tree-b {:node   1
             :children [{:node 11
                         :children [{:node 111}, {:node 112}]},
                        {:node 12
                         :children [{:node 121},
                                    {:node 122
                                     :children [{:node 1221}, {:node 1222}]}
                                    {:node 123}]},
                        {:node 13}]})

(def tree-b2 [1 [[11, [[111], [112]]]
                 [12, [[121],
                       [122, [[1221], [1222]]]
                       [123]]]
                 [13]]])
