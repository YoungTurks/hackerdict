(ns hackerdict.rest.dict
  (:require [compojure.core :refer [defroutes GET POST]]
            [hackerdict.db :as db]
            [cemerick.url :refer [url-encode]]
            [ring.middleware.transit :refer [wrap-transit-response wrap-transit-body]]
            [ring.util.response :refer [redirect]]
            [hackerdict.util.rest :as rest]))

(defn add-entry
  "View fn for adding entry"
  [{:keys [params session] :as request}]
  (print (-> session :user :username))
  (db/add-entry! {:subject (:subject params)
                  :username (-> session :user :username)
                  :text (:entry params)})
  (redirect (str "/subject/" (url-encode (:subject params)))))

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


;(comment (str ((wrap-transit-response {:fuck "that"}))))
