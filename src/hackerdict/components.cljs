(ns hackerdict.components
  (:require [ajax.core :refer [GET POST]]
            [sablono.core :refer-macros [html]]))


(defn like-seymore [data]
  (html [:div
             [:h1 "serkan's quantified popularity: " (:likes @data)]
             [:div [:a {:href "#"
                        :onClick #(swap! data update-in [:likes] inc)}
                    "Thumbs up"]]]))

(defn header [data]
  (html [:div "HackerDict"]))

(defn subject [subject-data]
  (html [:li [:a {:onClick (fn [e] (.preventDefault e) (js/alert (:subject-text subject-data)))} (:subject-text subject-data)]])
  )


(defn side-bar [data]
  (html [:div "Side bar"
             [:div "Today's Topics"
              ;  (str @data (:sidebar-items @data))
              [:ul (map subject (:sidebar-items @data))]]]))

(defn entry [entry-data]
  (html [:li (:text entry-data) (:creator entry-data) (str (:date-created entry-data))])
  )
(defn main-section [data]
  (html [:div [:h2 (:subject (:main-items @data))]
             [:ul (map entry (:entries (:main-items @data)))]]))


(defn bottom [data]
  (html [:div
             (side-bar data)
             (main-section data)]))
(defn main [data]
  (html [:div.main
             (header data)
             (bottom data)
             ])
  )
