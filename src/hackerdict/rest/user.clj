(ns hackerdict.rest.user
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [hackerdict.util.rest :as rest]))

(defroutes user-routes
  (GET "/users" {session :session}
    (rest/response {:body "/users response."}))
  (GET "/user/:username" {session :session})
    (rest/response {:body "/user/:username response."}))
