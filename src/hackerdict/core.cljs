(ns hackerdict.core
  (:require
   [ajax.core :refer [GET POST]]
   [cemerick.url :refer [url-encode]]
   [hackerdict.mock-data :refer [mock-side-bar-data mock-main-feed-data]]
   [hackerdict.components :refer [main]]))

(enable-console-print!)
;; mock users: {:username "ustun" :name "Ustun Ozgur" :email "ustun@ustunozgur.com"}
(def app-state (atom {:username js/HD_USERNAME
                      :sidebar-items mock-side-bar-data
                      :form-subject (or js/HD_INITIAL_TOPIC "hacker-dict")
                      :main-items mock-main-feed-data}))

                                  ;(print (str "app state is "@app-state))

(defn latest-subjects-handler [response]
  (print "got latest subjects data")
  (swap! app-state update :sidebar-items (fn [_] response))
  (.log js/console (str response))
  )

(defn subject-entries-handler [response]
  (print "got subject entries dat")
  (swap! app-state update :main-items (fn [_] response))
  (.log js/console (str response))
  )

(defn current-user-handler [response]
  (print "got current user data")
  (swap! app-state update :username (fn [_] "deneme"))
  (.log js/console (str response)))



(GET "/api/dict/latest-subjects" {:handler latest-subjects-handler :response-format :json
         :keywords? true})

(GET (str "/api/dict/subject/" (url-encode (or js/HD_INITIAL_TOPIC "hacker-dict"))) {:handler subject-entries-handler
                                                        :response-format :json
         :keywords? true})

(GET "/api/user/current-user" {:handler current-user-handler :response-format :json
         :keywords? true})


(defn render! []
  (.render js/React
           (main app-state)
           (.getElementById js/document "app")))

(add-watch app-state :on-change (fn [_ _ _ _]
;                                  (print (str "app state is "@app-state))
                                  (render!)))

(render!)
