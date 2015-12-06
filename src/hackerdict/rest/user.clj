(ns hackerdict.rest.user
  (:require [compojure.core :refer [defroutes GET]]
            [hackerdict.util.rest :as rest]))

(defroutes user-routes
  (GET "/users" {session :session}
    (rest/response {:body "/users response."}))

  (GET "/user/:username" {session :session})
    (rest/response {:body "/user/:username response."}))
