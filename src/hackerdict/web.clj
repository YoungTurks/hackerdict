(ns hackerdict.web
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [hackerdict.auth :as auth]
            [hackerdict.rest.auth :refer [auth-routes]]
            [hackerdict.rest.user :refer [user-routes]]
            [hackerdict.util.rest :as rest]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.basic-authentication :as basic]
            [ring.middleware.reload :as reload]
            [ring.middleware.session :as session]
            [ring.middleware.session.cookie :as cookie]
            [ring.middleware.stacktrace :as trace]
            [ring.util.response :as response]
            [cemerick.drawbridge :as drawbridge]
            [environ.core :refer [env]]))

(defn- authenticated? [user pass]
  ;; TODO: heroku config:add REPL_USER=[...] REPL_PASSWORD=[...]
  (= [user pass] [(env :repl-user false) (env :repl-password false)]))

(def ^:private drawbridge
  (-> (drawbridge/ring-handler)
      (session/wrap-session)
      (basic/wrap-basic-authentication authenticated?)))

(defroutes app
  #'auth-routes
  #'user-routes
  
  (GET "/" {session :session}
    (rest/response {:body (str "Main Page \n"
                               "========= \n"
                               (when-let [token (:token session)] 
                                 (println (str "at root token is " token))
                                 (str "User token is " token ".\n"
                                      "Username is " (auth/get-username token) ".\n" 
                                      "Name is " (auth/get-name token) ".\n" 
                                      "Email is " (auth/get-email token) ".\n")))}))

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
        reload/wrap-reload
        (site {:session {:store store}}))))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty (wrap-app #'app) {:port port :join? false})))

;; For interactive development:
;; (.stop server)
;; (def server (-main))
