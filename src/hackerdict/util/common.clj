(ns hackerdict.util.common
  (:require
    [markdown.core :as markdown]))

(defn clean-nil-vals
  "Cleans nil values from a map"
  [original-map]
  (into {} (filter second original-map)))


;; pre-markdown rules
;; convert [word] in entries to [text] ("/subject/text")
;; convert [word1 word2 ...] to [word1 word2 ..]("/subject/word1%20word2...")
;; so that final links are created from markdown

(defn replace-single-world-link [text]
  (clojure.string/replace text #"\#(\w+)" "[#$1](/subject/$1)"))

(defn replace-spaces-with-url-quotes [text]
  (clojure.string/replace text #"\s+" "%20"))


;;pre-markdown for [word word ..]. Also prevent conflicts with original markup rule for links.
(defn replace-multiple-world-link [text]
  (clojure.string/replace text #"\[(.*?)\]([^\(]?)"
                          (fn [x] (let [gr2 (x 2) gr1 (x 1) conv (str "[" gr1 "]" "(/subject/"
                                                            (replace-spaces-with-url-quotes gr1) ")" gr2) ] conv)))
  )


(comment (replace-single-world-link "#java is cool but #clojure is not"))

(comment (replace-multiple-world-link "[java vs clojure] blabla"))

(defn preMarkDown [text]
  ((comp replace-single-world-link replace-multiple-world-link) text))

(defn process-text [text]
  ((comp markdown/md-to-html-string preMarkDown ) text))

(comment
  (process-text "[cnn](http://cnn.com)")
  (process-text "[java vs clojure] blabla\""))
