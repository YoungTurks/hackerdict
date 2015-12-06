(ns hackerdict.scraper.transact
  (:require [hackerdict.scraper.populater :refer [dataPath bot-user-name]]
            [hackerdict.db :as db]
            [clojure.data.json :as json]
           ))


;; (create subjects for each clojure repository by using their description

(def gitData (json/read-str (str (slurp dataPath))))

;; github usernames can be useful later
(defn parse-owner-data [owner-data]
  (owner-data "login"))

(defn get-info-for-item [item]
  {:user (parse-owner-data (item "owner"))
   :description (item "description")
   :name (item "name")})


(doseq [item-data (map get-info-for-item gitData)]
  (println (:description item-data))
  (db/create-subject! (:description item-data) bot-user-name))



