(ns hackerdict.core
  (:require
   [ajax.core :refer [GET POST]]
   [hackerdict.mock-data :refer [mock-side-bar-data mock-main-feed-data]]
   [hackerdict.components :refer [like-seymore main]]))

(enable-console-print!)

(def app-state (atom { :likes 0 :sidebar-items mock-side-bar-data :main-items mock-main-feed-data}))


(defn handler [response]
  ;(.log js/console (str response))
  )

(GET "http://hackerdictlocal.com:5000/api/dict/latest-subjects" {:handler handler})


(defn render! []
  (.render js/React
           (main app-state)
           (.getElementById js/document "app")))

(add-watch app-state :on-change (fn [_ _ _ _] (render!)))

(render!)
