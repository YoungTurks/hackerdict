(ns hackerdict.rest.dict
  (:require [compojure.core :refer [defroutes GET POST]]
            [hackerdict.db :as db]
            [hackerdict.util.rest :as rest]))

(defn add-entry
  "View fn for adding entry"
  [{:keys [params session] :as request}]
  (db/add-entry! {:subject (:subject params)
                  :username (-> session :user :username)
                  :text (:entry params)})
  (rest/response {:body (str "Entry added")}))

(comment
  (add-entry {:session {:user {:username "ustun"}}
              :params {:text "deneme"
                       :subject "Clojure"}})
  )

(defroutes dict-routes
  (GET "/api/dict/latest-subjects" {session :session}
       (rest/response {:body (db/get-latest-subjects)}))

  (POST "/api/dict/entry/" request add-entry)

  (GET "/api/dict/subject/:text" [text :as {session :session}]
    (rest/response {:body (db/get-entries-for-subject-text text)})))
