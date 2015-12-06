(ns hackerdict.web
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [hackerdict.auth :as auth]
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
  (ANY "/repl" {:as req}
       (drawbridge req))
  
  (GET "/" {session :session}
    {:status 200
     :headers {"Content-Type" "text/plain"}
     :body (str "Main Page \n"
                "========= \n"
              (when-let [token (:token session)] 
                (str "User token is " token ".\n"
                     "Username is " (auth/get-username token) ".\n" 
                     "Name is " (auth/get-name token) ".\n" 
                     "Email is " (auth/get-email token) ".\n")))})
  
  (GET "/login" {session :session}
    (if-let [token (:token session)]
      {:status 404
       :headers {"Content-Type" "text/plain"}
       :body "Already logged in."}
      (let [state (auth/random-state)
            uri (auth/authorize-uri state)]
         {:status 302 
          :headers {"Location" uri}
          :session (assoc session :state state)})))

  (GET "/logout" {session :session}
    (if-let [token (:token session)]
      {:status 302 
       :headers {"Location" "/"}
       :session (dissoc session :token)}
      {:status 404
       :headers {"Content-Type" "text/plain"}
       :body "Not logged in."}))
  
  (ANY "/auth" {params :params session :session}
    (if (= (:state params) (:state session))
      (if-let [token (auth/access-token (:code params))]
        {:status 302 
         :headers {"Location" "/"}
         :session (assoc session :token token)}
        {:status 400
         :headers {"Content-Type" "text/plain"}
         :body "Cannot get token."})
      {:status 400
       :headers {"Content-Type" "text/plain"}
       :body    "Sessions doesn't match."}))

  (ANY "*" []
       (route/not-found (slurp (io/resource "404.html")))))

(defn wrap-error-page [handler]
  (fn [req]
    (try (handler req)
         (catch Exception e
           {:status 500
            :headers {"Content-Type" "text/html"}
            :body (slurp (io/resource "500.html"))}))))

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
