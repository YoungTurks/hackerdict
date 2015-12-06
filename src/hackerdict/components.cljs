(ns hackerdict.components
  (:require [ajax.core :refer [GET POST]]
            [cljsjs.moment]
            [cemerick.url :refer [url-encode]]
            [sablono.core :refer-macros [html]]))


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
  (html [:header.pure-g [:div.pure-u-2-3
                         [:h2 "hacker-dict"]
                         [:span "dictionary for programmers by programmers"]]
         (login-logout data)]))

(defn subject-entries-handler [data subject-text response]
    (swap! data update :main-items (fn [_] response))
    (swap! data update :form-subject (fn [_] subject-text)))


(defn get-subject-data [data subject-text]
  (GET (str "/api/dict/subject/" (url-encode subject-text))
       {:handler (partial subject-entries-handler data subject-text)
        :response-format :json
        :keywords? true}))

(defn latest-subjects-handler [data response]
  (swap! data update :sidebar-items (fn [_] response)))


(defn get-latest-subjects [data]
  (GET "/api/dict/latest-subjects" {:handler (partial latest-subjects-handler data)
                                    :response-format :json
                                    :keywords? true}))

(defn subject [data subject-data]
  (html [:li.subject {:key (:text subject-data)}
         [:a
          {:onClick (fn [e]
                      (.preventDefault e)
                      (.scrollTo js/window 0 0)
                      ((partial get-subject-data data) (:text subject-data)))}
          (:text subject-data)]]))


(defn side-bar [data]
  (html [:div.side-bar.pure-u-1-3
         [:div [:h3 "recent topics"]
          [:ul (map (partial subject data) (:sidebar-items @data))]]]))


(defn entry [entry-data]
  (html [:li.entry
         {:key (:text entry-data)}
         [:span.entry {:dangerouslySetInnerHTML {:__html (:processed-text entry-data)}}]
         [:div.entry-bottom
          [:span.username (:username entry-data)]
          [:span.date (.fromNow (js/moment (:date-added entry-data)))]]]))

(defn post-entry
  [data]
  (html [:form.entry-form.pure-form.pure-form-stacked
         {:method "POST" :action "/api/dict/entry/"
          :onSubmit (fn [e]
                      (print "initiating request")
                      (.preventDefault e)
                      (POST "/api/dict/entry/"
                            {:format :json
                             :params {:subject (:form-subject @data) :text (:form-entry @data)}
                             :handler (fn [_]
                                        (get-subject-data data (:form-subject @data))
                                        (get-latest-subjects data)
                                        (swap! data update :form-entry (fn [_] ""))
                                        ;(swap! data update :form-subject (fn [_] ""))
                                        )
                             }))}
         [:label {:for "subject"} "Subject"]
         [:input {:name "subject"
                  :autoComplete false
                  :onChange (fn [e]
                              (swap! data update :form-subject (fn [_] (.-value (.-target e))))
                              )
                  :value (:form-subject @data) :placeholder "Subject (a library, a language, phrase)"}]
         [:label {:for "entry"} "Entry"]

         [:textarea {:name "entry" :value (:form-entry @data)
                     :onChange (fn [e]
                                 (swap! data update :form-entry (fn [_] (.-value (.-target e))))
                                 )
                     :placeholder "Please enter a definition for the subject"}]
         [:button.pure-button.pure-button-primary "Submit"]]))


(defn request-login []
  (html [:a.pure-button {:href "/login"} "Login/Register to add entries"]))

(defn main-section [data]
  (html [:div.main-section.pure-u-2-3 [:h2 (:subject (:main-items @data))]
         [:ul (map entry (:entries (:main-items @data)))]
         (if (:username @data)
           (post-entry data)
           (request-login))]))


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
