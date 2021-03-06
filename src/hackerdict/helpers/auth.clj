(ns hackerdict.helpers.auth
  (:require [cemerick.url :refer [url-encode]]
            [environ.core :refer [env]]
            [org.httpkit.client :as http]
            [ring.util.codec :as codec]))

(def ^:private oauth2-params
   {:client-id        (env :github-client-id)
    :client-secret    (env :github-client-secret)
    :authorize-uri    "https://github.com/login/oauth/authorize"
    :redirect-uri     (str (:app-host env) "/auth")
    :access-token-uri "https://github.com/login/oauth/access_token"
    :scope            "user:email"})

(:app-host env)
(defn random-state []
  (let [chars (map char (range 65 91))]
    (reduce str (take 10 (repeatedly #(rand-nth chars))))))

(defn- params->map [params]
  (clojure.walk/keywordize-keys
    (codec/form-decode params)))

(defn authorize-uri [state]
  (str
    (:authorize-uri oauth2-params)
    "?response_type=" "code"
    "&client_id="     (url-encode (:client-id oauth2-params))
    "&redirect_uri="  (url-encode (:redirect-uri oauth2-params))
    "&scope="         (url-encode (:scope oauth2-params))
    "&state="         (url-encode state)))

(defn access-token [code]
  (let [resp (http/post (:access-token-uri oauth2-params)
                 {:form-params {:code         code
                                :grant_type   "authorization_code"
                                :client_id    (:client-id oauth2-params)
                                :redirect_uri (:redirect-uri oauth2-params)}
                  :basic-auth [(:client-id oauth2-params) (:client-secret oauth2-params)]
                  :as          :text})
       token (:access_token (params->map (:body @resp)))]
    token))
