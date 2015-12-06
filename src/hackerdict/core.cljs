(ns hackerdict.core
  (:require
    [hackerdict.mock-data :refer [mock-side-bar-data mock-main-feed-data]]
            [hackerdict.components :refer [like-seymore main]]))

(def app-state (atom { :likes 0 :sidebar-items mock-side-bar-data :main-items mock-main-feed-data}))



(defn render! []
  (.render js/React
           (main app-state)
           (.getElementById js/document "app")))

(add-watch app-state :on-change (fn [_ _ _ _] (render!)))

(render!)

(.log js/console "Hey dd saadasadsup?!")