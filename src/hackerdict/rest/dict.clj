(ns hackerdict.rest.dict
  (:require [compojure.core :refer [defroutes GET POST]]
            [hackerdict.db :as db]
            [cemerick.url :refer [url-encode]]
            [ring.middleware.transit :refer [wrap-transit-response wrap-transit-body]]
            [ring.util.response :refer [redirect]]
            [hackerdict.util.rest :as rest]))

(defn add-entry
  "View fn for adding entry"
  [{:keys [session] :as request}]
  (let [username (-> session :user :username) params (-> request :body)]
    (do
      (print username)
      (db/add-entry! {:subject (params "subject")
                      :username username
                      :text (params "text")})
      (str "/subject/" (url-encode (params "subject")))
     )
    {:body "foo"}))

(comment
  (add-entry {:session {:user {:username "ustun"}}
              :params {:text "deneme"
                       :subject "clojure"}})
  )

(defroutes dict-routes
  (GET "/api/dict/latest-subjects" {session :session}
       (rest/response {:body (db/get-latest-subjects)}))

  (POST "/api/dict/entry/" request add-entry)

  (GET "/api/dict/subject/:text" [text :as {session :session}]
       (rest/response {:body {:subject text
                              :entries (db/get-entries-for-subject-text text)}})))
