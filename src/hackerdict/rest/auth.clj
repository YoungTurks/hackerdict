(ns hackerdict.rest.auth
  (:require [compojure.core :refer [defroutes GET]]
            [hackerdict.helpers.auth :as auth]
            [hackerdict.helpers.user :as user]
            [hackerdict.util.rest :as rest]))

(defroutes auth-routes
  (GET "/login" {session :session}
    (if-let [token (:token session)]
      (rest/response {:status 400
                      :body   "Already logged in."})
      (let [state (auth/random-state)
            uri (auth/authorize-uri state)]
         (rest/response {:status  302
                         :headers {"Location" uri}
                         :session (assoc session :state state)}))))

  (GET "/auth" {params :params session :session}
    (if (= (:state params) (:state session))
      (if-let [token (auth/access-token (:code params))]
        (do
          (let [user (user/upsert-user! token)]
            (rest/response {:status  302
                            :headers {"Location" "/"}
                            :session (merge (dissoc session :state)
                                            {:token token :user user})})))
        (rest/response {:status 500
                        :body   "Cannot get token."
                        :session (dissoc session :state)}))
      (rest/response {:status 400
                      :body   "Sessions doesn't match."
                      :session (dissoc session :state)})))

  (GET "/logout" {session :session}
    (if-let [token (:token session)]
      (rest/response {:status  302
                      :headers {"Location" "/"}
                      :session (dissoc session :token :user)})
      (rest/response {:status 400
                      :body   "Not logged in."}))))
