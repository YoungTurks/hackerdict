(ns hackerdict.web
  (:require [compojure.core :refer [defroutes GET ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [hackerdict.helpers.auth :as auth]
            [hackerdict.db :as db]
            [hackerdict.helpers.user :as user]
            [hackerdict.rest.auth :refer [auth-routes]]
            [hackerdict.rest.user :refer [user-routes]]
            [hackerdict.rest.dict :refer [dict-routes]]
            [hackerdict.util.rest :as rest]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.basic-authentication :as basic]
            [ring.middleware.json :as json]
            [ring.middleware.reload :as reload]
            [ring.middleware.session :as session]
            [ring.middleware.session.cookie :as cookie]
            [ring.middleware.stacktrace :as trace]
            [ring.util.response :as response]
            [cemerick.drawbridge :as drawbridge]
            [environ.core :refer [env]]
            [selmer.parser :refer [render-file]])
  (:gen-class))

;; Private functions

(defn- authenticated? [user pass]
  ;; TODO: heroku config:add REPL_USER=[...] REPL_PASSWORD=[...]
  (= [user pass] [(env :repl-user false) (env :repl-password false)]))

(def ^:private drawbridge
  (-> (drawbridge/ring-handler)
      (session/wrap-session)
      (basic/wrap-basic-authentication authenticated?)))

(defn home-page [request]
  (let [session (:session request)
        context {:user (:user session)}
        body (render-file "index.html" context)]
    (rest/html-response {:body body})))


(defroutes app
  #'auth-routes
  #'user-routes
  #'dict-routes

  (GET "/" request home-page)

  (ANY "/repl" {:as req}
       (drawbridge req))

  (ANY "*" []
    (route/not-found (slurp (io/resource "404.html")))))

(defn wrap-error-page [handler]
  (fn [req]
    (try (handler req)
      (catch Exception e
        (rest/response {:status 500
                        :body (slurp (io/resource "500.html"))})))))

(defn wrap-app [app]
  ;; TODO: heroku config:add SESSION_SECRET=$RANDOM_16_CHARS
  (let [store (cookie/cookie-store {:key (env :session-secret)})]
    (-> app
        ((if (env :production)
           wrap-error-page
           trace/wrap-stacktrace))
        json/wrap-json-body
        json/wrap-json-response
        reload/wrap-reload
        (site {:session {:store store}}))))

(defn -main [& [port]]
  (db/connect!)
  (db/get-db!)
  (db/create-schema!)
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty (wrap-app #'app) {:port port :join? false})))

;; For interactive development:
(comment
  (def server (-main))
  (.stop server)
  )
