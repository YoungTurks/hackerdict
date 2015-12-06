(ns hackerdict.scraper.transact
  (:require [hackerdict.scraper.populater :refer [data-path bot-user-name]]
            [hackerdict.db :as db]
            [clojure.data.json :as json]))


;; (create subjects for each clojure repository by using their description

(defn git-data []
  (json/read-str (str (slurp data-path))))

;; github usernames can be useful later
(defn parse-owner-data [owner-data]
  (owner-data "login"))

(defn get-info-for-item [item]
  {:user (parse-owner-data (item "owner"))
   :description (item "description")
   :name (item "name")})


(defn load-data []
  (db/create-or-update-user! {:username bot-user-name})
  (doseq [item-data (map get-info-for-item (git-data))]
    (db/create-subject! (:name item-data) bot-user-name)
    (db/add-entry! {:subject (:name item-data) :text (:description item-data) :username bot-user-name}
    )))


(load-data)
;; Load data using:
;; lein run -m hackerdict.scraper.transact/load-data
