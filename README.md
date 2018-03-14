
# funzip - a protocol based implementation of Huet's Zipper

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

To learn more about the idea, check out the [wikipedia page](https://github.com/stanch/zipper), or read the [original paper](https://www.st.cs.uni-saarland.de/edu/seminare/2005/advanced-fp/docs/huet-zipper.pdf).

This is a port from a Scala implementation that can be found here: [https://github.com/stanch/zipper]

 example usage:

```clojure
(require (require '[funzip.core :as funzip])   ; The core api that includes all functions for navigation and modification
                  '[funzip.zipper :as zipper]) ; ns for raw data -> zipper
(def typical-house-cat
  (-> :cat
      (zipper/create-zipper)
      (funzip/insert-down-left [:head] [:body])
      (funzip/insert-down-left [:cute-eyes] [:cute-nose] [:adorable-smile])
      (funzip/move-up)
      (funzip/move-right)
      (funzip/insert-down-left [:soft-fur] [:playful-paws] [:bouncy-legs])
      (funzip/commit)))
```
> ; typical-house-cat
>
>  [:cat [[:head [[:cute-eyes] [:cute-nose] [:adorable-smile]]]
>
> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; [:body [[:soft-fur] [:playful-paws] [:bouncy-legs]]]]]


```clojure
(def atypical-house-cat
  (-> typical-house-cat
      (zipper/node->zipper)
      (funzip/find #(= :head (first %)))
      (funzip/move-down)
      (funzip/update #(assoc % 0 :laser-beam-eyes))
      (funzip/move-right)
      (funzip/move-right)
      (funzip/set [:smirk-of-hitler])
      (funzip/find #(= :body (first %)))
      (funzip/set [:body])
      (funzip/insert-down-right [:hyper-allergic-fur] [:ninja-assassin-paws] [:bionic-legs])
      (funzip/commit)))
```
> ; atypical-house-cat
>
>   [:cat [[:head [[:laser-beam-eyes] [:cute-nose] [:smirk-of-hitler]]]
>
>   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[:body [[:hyper-allergic-fur] [:ninja-assassin-paws] [:bionic-legs]]]]]

## Why?
In one honest, hearty nutshell: I stumbled upon the Scala implementation and thought that a Clojure port would be a straight forward and perhaps  worthwhile endeavor.

clojure.zip is great, but it's a bit more difficult to use than it should be, considering that it came out before protocols was a thing.




