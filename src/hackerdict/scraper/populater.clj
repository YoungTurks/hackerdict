(ns hackerdict.scraper.populater
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [hackerdict.db :as db]
            [clojure.java.io :as io]))


;; get 120 most popular clojure repos from github and dump it


(def popular-clojure-repos "https://api.github.com/search/repositories?q=clojure?language=clojure&sort=stars&order=desc")
(def bot-user-name "bot")
(def data-path "./resources/clojure-data.json")





(defn getJSON [url]
  (json/read-str (:body @(http/get url))))

(def git-data
  (concat  ((getJSON (str popular-clojure-repos "&page=1")) "items")
          ((getJSON (str popular-clojure-repos "&page=2")) "items")
          ((getJSON (str popular-clojure-repos "&page=3")) "items")
          ((getJSON (str popular-clojure-repos "&page=4")) "items")
          ))

(comment
  (db/create-or-update-user! {:username bot-user-name})
  (spit data-path (json/write-str git-data)))
