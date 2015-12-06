(ns hackerdict.components
  (:require [ajax.core :refer [GET POST]]
            [cljsjs.moment]
            [sablono.core :refer-macros [html]]))


(defn like-seymore [data]
  (html [:div
             [:h1 "sdfsdf: " (:likes @data)]
             [:div [:a {:href "#"
                        :onClick #(swap! data update-in [:likes] inc)}
                    "Thumbs up"]]]))

(defn login-logout
  ""
  [data]
  (html [:div.login-logout.pure-u-1-3
         (if-let [user (:username @data)]
           [:div
            [:span "Logged in as: "]
            [:span.username user]
            [:a.pure-button {:href "/logout"} "Logout"]]
           [:div [:a.pure-button {:href "/login"} "Login/Register"]])]))


(defn header [data]
  (html [:header.pure-g [:h2.pure-u-2-3 "HackerDict"] (login-logout data)]))

(defn subject [subject-data]
  (html [:li.subject {:key (:subject-text subject-data)}
         [:a
          {:onClick (fn [e] (.preventDefault e) (js/alert (:subject-text subject-data)))}
          (:subject-text subject-data)]]))


(defn side-bar [data]
  (html [:div.side-bar.pure-u-1-3
             [:div "Today's Topics"
              ;  (str @data (:sidebar-items @data))
              [:ul (map subject (:sidebar-items @data))]]]))


(defn entry [entry-data]
  (html [:li.entry {:key (:text entry-data)} (:text entry-data) (:username entry-data) (str (.fromNow (new js/moment (:date-added entry-data))))]))


(defn post-entry
  [data]
  (html [:form.entry-form.pure-form.pure-form-stacked {:method "POST" :action "/api/dict/entry/"}
         [:input {:name "subject" :placeholder "Subject"}]
         [:textarea {:name "entry"}]
         [:button.pure-button.pure-button-primary "Submit"]]))

(defn main-section [data]
  (html [:div.main-section.pure-u-2-3 [:h2 (:subject (:main-items @data))]
         [:ul (map entry (:entries (:main-items @data)))]
         (post-entry data)]))


(defn bottom [data]
  (html [:div.bottom.pure-g
             (side-bar data)
         (main-section data)]))


(defn main [data]
  (html [:div.main
             (header data)
             (bottom data)
             ])
  )
