(ns hackerdict.util.common
  (:require
    [instaparse.core :as insta][markdown.core :as markdown] [ring.util.codec :as codec]))

(defn clean-nil-vals
  "Cleans nil values from a map"
  [original-map]
  (into {} (filter second original-map)))


;; pre-markdown rules
;; convert [word] in entries to [text] ("/subject/text")
;; convert [word1 word2 ...] to [word1 word2 ..]("/subject/word1%20word2...")
;; so that final links are created from markdown

(defn replace-single-world-link [text]
  (clojure.string/replace text #"\#([\w\-]+)" "[#$1](/subject/$1)"))

(defn replace-spaces-with-url-quotes [text]
  (clojure.string/replace text #"\s+" "%20"))


;;pre-markdown for [word word ..]. Also prevent conflicts with original markup rule for links.
(defn replace-multiple-world-link [text]
  (clojure.string/replace text #"\[(.*?)\]($|[^\(])"
                          (fn [x] (let [gr2 (x 2) gr1 (x 1) conv (str "[" gr1 "]" "(/subject/"
                                                            (replace-spaces-with-url-quotes gr1) ")" gr2) ] conv))))


(comment (replace-single-world-link "#java is cool but #clojure"))

(comment (replace-multiple-world-link "[java vs clojure(paren)] blabla"))


(defn preMarkDown [text]
  ((comp replace-single-world-link replace-multiple-world-link) text))


(def entry-parser (insta/parser "S = (markup-link | word | whitespace )+
                               <markup-link> = (single-word-markup-link | multi-words-markup-link)
                               single-word-markup-link = <'#'>word
                               orig-markup-link = <'['> recursive-in-bracket <']'> <'('> recursive-in-paren <')'>
                               multi-words-markup-link = !orig-markup-link <'['> recursive-in-bracket  <']'>
                               <recursive-in-bracket> = <'['> recursive-in-bracket  <']'>  | text-without-bracket
                               <recursive-in-paren> = <'('> recursive-in-paren <')'> | text-without-paren
                               <word> = !single-word-markup-link !multi-words-markup-link #'\\S+'
                               <text-without-bracket> = anychar-except-bracket+
                               <text-without-paren> = anychar-except-paren+
                               <anychar-except-bracket> = #'[^\\[\\]]'
                               <anychar-except-paren> = #'[^\\(\\)]'
                               <whitespace> = #'(\\ | \\t)+'"))



(defn convert-single-word [token] (str "["  token  "]"  "(/subject/"  (codec/url-encode token )")"))

(defn convert-multi-word [token & others] (let [text (apply str token others)](str "["  text  "]"  "(/subject/" (codec/url-encode   text) ")")) )


(defn transform [parsed] (insta/transform {:single-word-markup-link convert-single-word :multi-words-markup-link convert-multi-word :S str} parsed))

(defn parse-transform [entry] ((comp transform entry-parser) entry))



(defn process-text [text]
  ((comp markdown/md-to-html-string parse-transform ) text))

(comment
  (process-text "[cnn(great website))](http://cnn.com)")
  (process-text "[java vs clojure(not discussable)] blabla\""))


(process-text "[cnn(great website))](http://cnn.com)")
(markdown/md-to-html-string "[cnn(great website))](http://cnn.com)")
