(ns hackerdict.db
  (:require [clojure.edn :refer [read-string]])
  (:require [hackerdict.schema :refer [schema]])
  (:import datomic.Util java.util.Random)
  (:require [datomic.api :as d])
  (:require [clojure.java.io :as io]))


; (def uri "datomic:dev://localhost:4334/test")

(def uri  "datomic:sql://hackerdict?jdbc:postgresql://localhost:5432/datomic?user=datomic&password=datomic")

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
  (create-database)
  (delete-database)
  (create-schema)
  )


(defn clean-nil-vals
  "Cleans nil values from a map"
  [original-map]
  (into {} (filter second original-map)))


(defn create-or-update-user
  "Creates or updates user for a given user map. The username field is required in the usermap. Other fields are optional."
  [{:keys [username first-name last-name email github-username github-token]}]
  (let [shared-map {:user/username username
                    :user/first-name first-name
                    :user/last-name last-name
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

(create-or-update-user {:username "ustunozgur" :github-token "foobar"})


(defn get-user-ids
  "doc-string"
  []
  (d/q '[:find [?e ...]
         :where [?e :user/username]]
       (get-latest-db)))

(get-user-ids)


(defn get-user-names
  "doc-string"
  []
  (d/q '[:find [?n ...]
         :where [?e :user/username ?n]]
       (get-latest-db)))


(defn get-user-some-predefined-data
  "doc-string"
  []
  (d/q `[:find [(pull ?e [:user/username :user/email :user/first-name]) ...]
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
  "doc-string"
  [username]
  (d/q '[:find (pull ?e [*]) .
         :in $ ?username
         :where [?e :user/username ?username]]
       (get-latest-db) username))

(get-user-by-username "ustunozgur")

(create-or-update-user! {:username "ustunozgur" :github-token "my github token"})

(pprint (first (get-user-all-data)))


(defn get-user-first-names
  "doc-string"
  []
  (d/q '[:find [?n ...]
         :where [?e :user/first-name ?n]]
       (get-latest-db)))

(defn get-user-id-by-username [username]
  (d/q '[:find ?e .
         :in $ ?username
         :where [?e :user/username ?username]]
       (get-latest-db) username))

(get-user-id-by-username "ustunozgur")
(defn get-user-emails
  "doc-string"
  []
  (d/q '[:find [?email ...]
         :where [?e :user/email ?email]]
       (get-latest-db)))

(get-user-names)

(get-user-first-names)

(get-user-emails)


(defn create-or-update-user! [user-map]
  (d/transact (get-connection) [(create-or-update-user user-map)]))


(create-or-update-user {:username "serkanozer" :first-name "SERKAN" })

(add-user! {:username "serkanozer" :first-name "SERKAN"})

(create-or-update-user! {:username "koraygulay" :first-name "Koray"})

(add-user! {:username "ustunozgur" :first-name "Ustun" :last-name "Ozgur" :email "ustun@ustunozgur.com"})

(defn create-subject! [subject username]
  (d/transact (get-connection) [(create-subject subject username)]))

(defn create-subject [subject username]
  {:db/id #db/id[:db.part/user]
   :subject/text subject
   :subject/date-added (java.util.Date.)
   :subject/creator (get-user-id-by-username username)})

(create-subject! "merhaba" "ustunozgur")

(defn get-subject-id-by-text [text]
  (d/q
   '[:find ?e .
     :in $ ?text
     :where [?e :subject/text ?text]]
   (get-latest-db) text))

(get-subject-id-by-text "bjk")

(defn get-or-create-subject!
  "doc-string"
  [text username]
  (let [subject-id (get-subject-id-by-text text)]
    (if subject-id
      subject-id
      (do
        (create-subject! text username)
        (get-subject-id-by-text text)))))

(get-or-create-subject! "go" "ustunozgur")

(defn get-subject-by-text [text]
  (d/q '[:find (pull ?e [*]) .
         :in $ ?text
         :where
         [?e :subject/text ?text]
         ]
       (get-latest-db) text))


; (get-subject-by-text "deneme")

(defn get-subjects []
  (d/q
   '[:find [(pull ?e [*]) ...]
     :where [?e :subject/text]]
   (get-latest-db)))

(defn get-entries []
  (d/q
   '[:find [(pull ?e [*]) ...]
     :where [?e :entry/text]]
   (get-latest-db)))

(count (get-subjects))

(map :entry/text (get-entries))

(defn add-entry-with-subject-id [subject-id text username]
  {:db/id #db/id[:db.part/user]
   :entry/text text
   :entry/creator (get-user-id-by-username username)
   :entry/date-added (java.util.Date.)
   :entry/subject subject-id})


(defn add-entry-with-subject-id! [subject-id text username]
  (d/transact (get-connection) [(add-entry-with-subject-id subject-id text username)]))


(add-entry-with-subject-id (get-subject-id-by-text "deneme") "deneme cok guzel bir yazi turudur" "ustunozgur")

(add-entry-with-subject-id! (get-subject-id-by-text "deneme") "en guzel dene" "ustunozgur")


(defn update-date-last-entry-for-subject-id [subject-id]
  {:db/id subject-id
   :subject/date-last-entry (java.util.Date.)})

; (update-date-last-entry-for-subject-id 12)

(defn update-date-last-entry-for-subject-id! [subject-id]
  (d/transact (get-connection) [(update-date-last-entry-for-subject-id subject-id)]))

(update-date-last-entry-for-subject-id! (get-subject-id-by-text "deneme"))

(get-subject-by-text "deneme")


(defn add-entry! [{:keys [text subject  username]}]
  (let [subject-id (get-or-create-subject! subject username)]
    (add-entry-with-subject-id! subject-id text username)
    (update-date-last-entry-for-subject-id! subject-id)))


(add-entry! {:subject "Clojure" :text "Clojure guzel bir dildir" :username "ustunozgur"})

(add-entry! {:subject "Clojure" :text "Clojure bir Lisp turevidir" :username "ustunozgur"})

(add-entry! {:subject "Clojure" :text "Clojure berbat bir dildir" :username "ustunozgur"})


(defn lower
  "doc-string"
  [x]
  (.toLowerCase x)
  )

(lower "USTUN")


(defn get-entries-for-subject-text [subject-text]
  (d/q '[:find ?e ?text
         :in $ ?subject-text
         :where
         [?e :entry/text ?text]
         [?e :entry/subject ?s]
         [?s (comp lower :subject/text) ?subject-text]]
       (get-latest-db) subject-text))



(defn n-entries-for-subject-text [subject-text]
  (count (get-entries-for-subject-text subject-text)))


(n-entries-for-subject-text "Clojure")


;; (.touch ent)
 ;;(keys (.cache ent))


;; TODO: should we add id fields for
