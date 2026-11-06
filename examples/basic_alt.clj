(require '[funzip.core :as funzip]   ; The core api that includes all
                                     ; functions for navigation and
                                     ; modification
         '[funzip.zipper :as zipper] ; ns for raw data -> zipper
         '[funzip.protocols :as protocols]
         '[clojure.pprint :as pp]
         '[clojure.data.xml :as xml])

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



