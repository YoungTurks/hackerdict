(ns hackerdict.db
  (:import datomic.Util java.util.Random)
  (:require [clojure.java.io :as io]
            [datomic.api :as d]
            [environ.core :refer [env]]
            [hackerdict.util.common :refer [clean-nil-vals process-text]]))


(def uri (:datomic-uri env))

;;;;;;;;;;;;;;;;;
;; Maintenance and Migration Functions
;;;;;;;;;;;;;;;;;

(def schema (read-string (slurp (io/resource "schema.edn"))))

(defn create-database []
  (d/create-database uri))

(defn delete-database []
  (d/create-database uri))

(defn get-connection []
  (d/connect uri))

(defn get-latest-db []
  (d/db (get-connection)))

(defn create-schema []
  (d/transact (get-connection) schema))

(comment
  (delete-database)
  (create-database)
  (create-schema)
  )


;;;;;;;;;;;;;;;;;
;; Database Connection
;;;;;;;;;;;;;;;;;

; (def conn (atom nil))

; (def db (atom nil))

;; (defn connect! []
;;   (println "Connecting to Datomic uri " uri ".")
;;   (reset! conn (d/connect uri)))

;; (defn get-db! []
;;   (println "Setting database of the connection " @conn ".")
;;   (reset! db (d/db @conn)))

(defn create-schema []
  (d/transact (get-connection) schema))


;;;;;;;;;;;;;;;;;
;; User functions
;;;;;;;;;;;;;;;;;

(defn get-user-id-by-username [username]
  (d/q '[:find ?e .
         :in $ ?username
         :where [?e :user/username ?username]]
       (get-latest-db) username))


(comment
  (get-user-id-by-username "ustunozgur"))

(defn create-or-update-user
  "Creates or updates user for a given user map. The username field is required in the usermap. Other fields are optional."
  [{:keys [username name email github-username github-token]}]
  (let [shared-map {:user/username username
                    :user/name name
                    :user/email email
                    :user/github-username github-username
                    :user/github-token github-token}
        user-id (get-user-id-by-username username)
        custom-map (if user-id
                     {:db/id user-id
                      :user/date-updated (java.util.Date.)}
                     {:db/id #db/id[:db.part/user]
                      :user/date-added (java.util.Date.)})]
    (clean-nil-vals (merge shared-map custom-map))))

(comment
  (create-or-update-user {:username "ustunozgur" :github-token "foobar"})
  (create-or-update-user! {:username "ustunozgur" :github-token "foobar"})

  )


(defn get-user-ids
  "doc-string"
  []
  (d/q '[:find [?e ...]
         :where [?e :user/username]]
       (get-latest-db)))

(comment
  (get-user-ids))


(defn get-user-usernames
  "doc-string"
  []
  (d/q '[:find [?n ...]
         :where [?e :user/username ?n]]
       (get-latest-db)))

(comment (get-user-names))

(defn get-user-some-predefined-data
  "doc-string"
  []
  (d/q `[:find [(pull ?e [:user/username :user/email :user/name]) ...]
         :where [?e :user/username]]
       (get-latest-db)))

(defn get-user-some-data
  "doc-string"
  [wanted-fields]
  (d/q
   {:find `[(pull ?e ~wanted-fields) ...]
    :where '[?e :user/username]}
   (get-latest-db)))

(defn get-user-all-data
  "doc-string"
  []
  (d/q '[:find [(pull ?e [*] ...)]
         :where [?e :user/username]]
       (get-latest-db)))


(defn get-user-by-username
  "Gets a user by username"
  [username]
  (d/q '[:find (pull ?e [*]) .
         :in $ ?username
         :where [?e :user/username ?username]]
       (get-latest-db) username))

(comment
  (get-user-by-username "ustun")

  (create-or-update-user! {:username "ustunozgur" :github-token "my github token"})

  (pprint (first (get-user-all-data))))

(defn get-user-names
  "doc-string"
  []
  (d/q '[:find [?n ...]
         :where [?e :user/name ?n]]
       (get-latest-db)))




(defn get-user-emails
  "doc-string"
  []
  (d/q '[:find [?email ...]
         :where [?e :user/email ?email]]
       (get-latest-db)))


(comment
  (get-user-names)

  (get-user-usernames)

  (get-user-emails))


(defn create-or-update-user! [user-map]
  (d/transact (get-connection) [(create-or-update-user user-map)]))


(comment
  (create-or-update-user {:username "serkanozer" :name "SERKAN" })

  (add-user! {:username "serkanozer" :name "SERKAN"})

  (create-or-update-user! {:username "koraygulay" :name "Koray"})

  (add-user! {:username "ustunozgur" :name "Ustun Ozgur" :email "ustun@ustunozgur.com"}))


;;;;;;;;;;;;;;;;;;;
;; Subject functions
;;;;;;;;;;;;



(defn create-subject [subject username]
  {:db/id #db/id[:db.part/user]
   :subject/text (.toLowerCase subject)
   :subject/date-added (java.util.Date.)
   :subject/creator (get-user-id-by-username username)})

(defn create-subject! [subject username]
  (d/transact (get-connection) [(create-subject subject username)]))

(comment
  (create-subject! "merhaba" "ustunozgur"))


(defn update-date-last-entry-for-subject-id [subject-id]
  {:db/id subject-id
   :subject/date-last-entry (java.util.Date.)})

(comment
  (update-date-last-entry-for-subject-id 12))


(defn get-subject-id-by-text [text]
  (d/q
   '[:find ?e .
     :in $ ?text
     :where [?e :subject/text ?text]]
   (get-latest-db) text))

(comment
  (get-subject-id-by-text "bjk"))

(defn get-or-create-subject!
  "doc-string"
  [text username]
  (let [subject-id (get-subject-id-by-text text)]
    (if subject-id
      subject-id
      (do
        (create-subject! text username)
        (get-subject-id-by-text text)))))

(comment
  (get-or-create-subject! "go" "ustunozgur"))

(defn get-subject-by-text [text]
  (d/q '[:find (pull ?e [*]) .
         :in $ ?text
         :where
         [?e :subject/text ?text]
         ]
       (get-latest-db) text))

(comment
  (get-subject-by-text "deneme"))

(defn get-subjects []
  (d/q
   '[:find [(pull ?e [*]) ...]
     :where [?e :subject/text]]
   (get-latest-db)))

(defn get-latest-subjects []
  (defn subject-fn
    [subject]
    {:text (:subject/text subject)
     :date-last-entry (:subject/date-last-entry subject)}
    )
  (map subject-fn (vec (reverse (sort-by :subject/date-last-entry (get-subjects))))))

(comment
  (sort-by :db/id (get-latest-subjects))
  (get-latest-subjects)
  )

;;;;;;;;;;;;
;; Entry functions
;;;;;;;;;;;;

(defn get-entries []
  (d/q
   '[:find [(pull ?e [*]) ...]
     :where [?e :entry/text]]
   (get-latest-db)))

(comment
  (use 'clojure.pprint)
  (count (get-entries))

  (map :entry/text (get-entries)))



(defn add-entry-with-subject-id [subject-id text username]
  {:db/id #db/id[:db.part/user]
   :entry/text text
   :entry/processed-text (process-text text)
   :entry/creator (get-user-id-by-username username)
   :entry/date-added (java.util.Date.)
   :entry/subject subject-id})

(comment (def deneme-id (:db/id (get-subject-by-text "deneme")))
         (add-entry-with-subject-id deneme-id "#java is cool but #clojure is not" "ustunozgur")
         (add-entry-with-subject-id deneme-id "[java vs clojure] blabla" "ustunozgur")

         )


(defn add-entry-with-subject-id! [subject-id text username]
  (d/transact (get-connection) [(add-entry-with-subject-id subject-id text username)]))


(comment
  (add-entry-with-subject-id (get-subject-id-by-text "deneme") "deneme cok guzel bir yazi turudur" "ustunozgur")

  (add-entry-with-subject-id! (get-subject-id-by-text "deneme") "en guzel dene" "ustunozgur"))


(defn update-date-last-entry-for-subject-id! [subject-id]
  (d/transact (get-connection) [(update-date-last-entry-for-subject-id subject-id)]))

(comment
  (update-date-last-entry-for-subject-id! (get-subject-id-by-text "deneme"))
  (create-subject! "deneme" "ustunozgur")
  (get-subject-by-text "deneme"))


(defn add-entry! [{:keys [text subject  username]}]
  (let [subject-id (get-or-create-subject! (.toLowerCase subject) username)]
    (add-entry-with-subject-id! subject-id text username)
    (update-date-last-entry-for-subject-id! subject-id)))


(comment
  (add-entry! {:subject "Clojure" :text "Clojure boktan bir dildir!!!" :username "ustunozgur"})

  (add-entry! {:subject "Clojure" :text "Clojure bir Lisp turevidir" :username "ustunozgur"})

  (add-entry! {:subject "Clojure" :text "Clojure berbat bir dildir" :username "ustun"}))


(defn get-entries-for-subject-text [subject-text]

  (defn to-entry-map [[id text username date-added processed-text]]
    {:subject subject-text :id id :text text :username username :date-added date-added :processed-text processed-text})

  (sort-by
   :date-added
   (map to-entry-map
       (d/q '[:find ?e ?text ?username ?date-added ?processed-text
              :in $ ?subject-text
              :where
              [?e :entry/text ?text]
              [?e :entry/subject ?s]
              [?s :subject/text ?subject-text]
              [?e :entry/creator ?u]
              [?u :user/username ?username]
              [?e :entry/date-added ?date-added]
              [?e :entry/processed-text ?processed-text]
              ; [?e :entry/date-updated ?date-updated]


               ]
             (get-latest-db) subject-text))))

(defn n-entries-for-subject-text [subject-text]
  (count (get-entries-for-subject-text subject-text)))


(defn delete-subject-by-text [text]
 (if-let [id (get-subject-id-by-text text)]
   (d/transact (get-connection) [{:db/id #db/id[db.part/user],
                                  :db/excise id}])))

(defn delete-entry-by-id [id]
 (d/transact (get-connection) [{:db/id #db/id[db.part/user],
                                :db/excise id}]))

(comment
  (get-entries)
  (get-entries-for-subject-text "Clojure")
  (n-entries-for-subject-text "Clojure"))


;; (.touch ent)
 ;;(keys (.cache ent))


;; TODO: should we add id fields for
