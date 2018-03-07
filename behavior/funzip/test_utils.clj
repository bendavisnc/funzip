(ns funzip.test-utils
  (:require [clojure.xml :as xml]))


(defn get-xml-node [e]
  (-> e xml/content first xml/content first Integer/parseInt))

(defn create-xml-node [v]
  (assoc-in (into (struct-map xml/element) {:tag :Tree,
                                            :attrs nil,
                                            :content [(into (struct-map xml/element) {:tag
                                                                                      :RootVal,
                                                                                      :attrs nil,
                                                                                      :content []})]})
            [:content 0 :content 0]
            (str v)))

(defn update-xml-node [e, v]
  (assoc-in e [:content 0 :content 0] (str v)))
