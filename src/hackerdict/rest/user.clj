(ns hackerdict.rest.user
  (:require [compojure.core :refer [defroutes GET]]
            [hackerdict.db :as db]
            [hackerdict.util.rest :as rest]))

(defroutes user-routes
  (GET "/users" {session :session}
    (rest/response {:body (str "All users are " (db/get-user-usernames))}))

  (GET "/user/:username" [username :as {session :session}]
    (rest/response {:body (db/get-user-by-username username)})))
