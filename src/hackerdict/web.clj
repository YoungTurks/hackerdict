(ns hackerdict.web
  (:gen-class)
  (:require [ring.adapter.jetty])
  (:require [clojure.pprint :refer [pprint]])
  (:require [clojure.data.json :as json]))


(defn html-response [body]
  {:status 200
   :body body
   :headers {"Context-Type" "text-html"}})

(defn json-response [body]
  {:status 200
   :body (json/write-str body)
   :headers {"Context-Type" "application/json"}})


(def index-view-regexs [#"/?"
                        #"/feed/\w+/?"
                        #"/question/\d+/?"])

(defn compare-uri-with-regexs
  [uri regexs]
  (not-every? nil? (map (fn [regex] (re-matches regex uri)) regexs)))


(defn handler [request]
  (pprint request)
  (let [uri (:uri request)]
    (if (compare-uri-with-regexs uri index-view-regexs)
      (html-response (str "this is the index view with uri: " uri))
      (json-response {:name "Ustun" :surname "Ozgur"}))))


(comment
  )

(defn restart-server
  "doc-string"
  [port]
  (defonce server (ring.adapter.jetty/run-jetty #'handler {:port port :join? false}))
  (.stop server)
  (.start server)
  (println (str "Started server at " port)))


(defn dev-restart-server
  "doc-string"
  []
  (restart-server 8000))

;(restart-server)
                                        ;

                                        ; (.start server)


(defn -main
  ""
  [& args]

  (restart-server (Integer/parseInt (first args))))
