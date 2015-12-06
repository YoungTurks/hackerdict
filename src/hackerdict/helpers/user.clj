(ns hackerdict.helpers.user
  (:require [clojure.data.json :as json]
            [hackerdict.db :as db]
            [org.httpkit.client :as http]))

(defn get-username [token]
  (when token
    (let [resp (http/get "https://api.github.com/user"
                         {:oauth-token token
                          :as :text})]
      (get (json/read-str (:body @resp)) "login"))))

(defn get-name [token]
  (when token
    (let [resp (http/get "https://api.github.com/user"
                         {:oauth-token token
                          :as :text})]
      (get (json/read-str (:body @resp)) "name"))))

(defn get-email [token]
  (when token
    (let [resp (http/get "https://api.github.com/user/emails"
                         {:oauth-token token
                          :as :text})]
      (get (first (filter #(get % "primary") (json/read-str (:body @resp)))) "email"))))

(defn upsert-user! [token]
  (let [username (get-username token)
        name (get-name token)
        email (get-email token)]
    (db/create-or-update-user! {:github-token    token
                                :username        username
                                :github-username username
                                :name            name
                                :email           email})
    {:username username
     :name name
     :email email})
  )
