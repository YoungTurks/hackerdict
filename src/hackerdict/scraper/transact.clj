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
   :name (item "name")
   :url (item "html_url")})


(defn load-data []
  (db/create-or-update-user! {:username bot-user-name})
  (doseq [item-data (map get-info-for-item (git-data))]
    (let [subject-name (:name item-data)]
      (db/create-subject! subject-name bot-user-name)
      (db/add-entry! {:subject subject-name :text (str "A project: " (:description item-data) ". [See github repo](" (:url item-data) ")") :username bot-user-name})

    )))


;; Load data using:
;; lein run -m hackerdict.scraper.transact/load-data
