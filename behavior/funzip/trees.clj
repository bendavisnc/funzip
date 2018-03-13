(ns funzip.trees)


(def tree-simple-map {:node 1 :children [{:node 2,
                                          :children [{:node 4}, {:node 5}]}
                                         {:node 3}]})

(def tree-less-simple-map {:node 1 :children [{:node 2,
                                               :children [{:node 4}, {:node 5}]}
                                              {:node 3
                                               :children [{:node 6
                                                           :children [{:node 8}, {:node 9}]}]}
                                              {:node -1}]})

(def tree-less-simple-vec [1 [[2 [[4] [5]]]
                              [3 [[6 [[8] [9]]]]]
                              [-1]]])


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


(def tree-map-modded {:node 1,
                      :children [{:node 11,
                                  :children [{:node 112} {:node 113} {:node 114}]}
                                 {:node 12,
                                  :children [{:node 121}
                                             {:node 122,
                                              :children [{:node -1} {:node -2}]}]}
                                 {:node 13,
                                  :children [{:node 131} {:node 132}]}]})


(def tree-vec-modded [1 [[11, [[112], [113], [114]]]
                         [12, [[121],
                               [122, [[-1], [-2]]]]]
                         [13, [[131], [132]]]]])


(def tree-map-super-complex {:node :process-tree,
                             :children [{:node {:dir-key :important, :dir-name "important_directory"},
                                         :children
                                               [{:node [500 500]
                                                 :children [{:node "helga.svg"} {:node "pookie.svg"}]}
                                                {:node [400 400]
                                                 :children [{:node "helga.svg"} {:node "pookie.svg"}],}]}

                                        {:node {:dir-key :super-important, :dir-name "super_important_directory"},
                                         :children [{:node [1500 1500]
                                                     :children [{:node "pookie.svg"}]}]}
                                        {:node {:dir-key :lots-o-sizes,
                                                :dir-name "super_important_directory/small"},
                                         :children [{:node [50 50]
                                                     :children [{:node "pookie.svg"}]},
                                                    {:node [100 100]
                                                     :children [{:node "pookie.svg"}]}]}
                                        {:node {:dir-key :lots-o-sizes, :dir-name "super_important_directory/mid"},
                                         :children [{:node [200 200]
                                                     :children [{:node "pookie.svg"}],}]}
                                        {:node {:dir-key :lots-o-sizes,
                                                :dir-name "super_important_directory/large"},
                                         :children [{:node [600 600]
                                                     :children [{:node "pookie.svg"}],}]}]})
