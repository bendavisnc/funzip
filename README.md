
# funzip - a protocol based implementation of Huet's Zipper [![Build Status](https://github.com/bendavisnc/funzip/actions/workflows/test.yml/badge.svg)](https://github.com/bendavisnc/funzip/actions/workflows/test.yml) [![Build Status](https://github.com/bendavisnc/funzip/actions/workflows/testcljs.yml/badge.svg)](https://github.com/bendavisnc/funzip/actions/workflows/testcljs.yml) [![Build Status](https://github.com/bendavisnc/funzip/actions/workflows/lintcheck.yml/badge.svg)](https://github.com/bendavisnc/funzip/actions/workflows/lintcheck.yml)

<img src="https://upload.wikimedia.org/wikipedia/en/8/85/Gene_Wilder_as_Willy_Wonka.jpeg"
 alt="Picture of Willy Wonka, from Willy Wonka & the Chocolate Factory (1971)" title="Willy Wonka, from Willy Wonka & the Chocolate Factory (1971)"
 align="right" />

> "If you want to view paradise, simply look around and view it.
>  Anything you want to, do it; want to change the world... there's nothing to it."
> - Willy Wonka, from "Willy Wonka and the Chocolate Factory" (1971)

## Installation
In Leiningen:

[![version](https://clojars.org/funzip/latest-version.svg)](https://clojars.org/funzip)


## What?
A zipper is a sort of deconstruction of a tree-like data structure that makes navigation and modification of said data structure easy and straight forward.

To learn more about the idea, check out the [wikipedia page](https://en.wikipedia.org/wiki/Zipper), or read the [original paper](https://www.st.cs.uni-saarland.de/edu/seminare/2005/advanced-fp/docs/huet-zipper.pdf).

This is a port from a Scala implementation that can be found here: [https://github.com/stanch/zipper]

example usage:

```clojure
(require '[funzip.core :as funzip]   ; The core api that includes all
                                     ; functions for navigation and
                                     ; modification
         '[funzip.zipper :as zipper] ; ns for raw data -> zipper
         '[clojure.pprint :as pp])

; vectors are the default data structure
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


```

```clojure
(require '[funzip.core :as funzip]   ; The core api that includes all
                                     ; functions for navigation and
                                     ; modification
         '[funzip.zipper :as zipper] ; ns for raw data -> zipper
         '[funzip.protocols :as protocols]
         '[clojure.pprint :as pp]
         '[clojure.data.xml :as xml])


; the api can be extended with protocols to support additional kinds of data

(extend-type clojure.data.xml.Element
 protocols/CanBeZipper
   (unzip [this]
     (:content this))
   (zip [this children]
     (assoc this :content children)))

(def cat
  (->
    (xml/element :cat)
    zipper/node->zipper

    ;; Give the cat a head and body.
    (funzip/insert-down-left (xml/element :head)
                             (xml/element :body))

    ;; Add some facial features.
    (funzip/insert-down-left
     (xml/element :eyes)
     (xml/element :nose)
     (xml/element :mouth))

    ;; Move to the body.
    funzip/move-up
    funzip/move-right

    ;; Give the cat some fur, paws, and legs.
    (funzip/insert-down-left
     (xml/element :fur)
     (xml/element :paws)
     (xml/element :legs))

    funzip/commit
    xml/emit-str))


(assert
 (=
  (->
    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>
      <cat>
        <head>
          <eyes></eyes>
          <nose></nose>
          <mouth></mouth>
        </head>
        <body>
          <fur></fur>
          <paws></paws>
          <legs></legs>
        </body>
      </cat>"
    xml/parse-str
    xml/emit-str)
  cat))
```

## Why?
In one honest, hearty nutshell: I stumbled upon the Scala implementation and thought that a Clojure port would be a straight forward and perhaps  worthwhile endeavor.

clojure.zip is great, but it's a bit more difficult to use than it should be, considering that it came out before protocols was a thing.




