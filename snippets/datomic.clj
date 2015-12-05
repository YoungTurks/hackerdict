(ns snippets.datomic
  (:import datomic.Util java.util.Random)
  (:require [datomic.api :as d]))

(def uri "datomic:dev://localhost:4334/test")


(def uri  "datomic:sql://hackerdict?jdbc:postgresql://localhost:5432/datomic?user=datomic&password=datomic")

(d/create-database uri)

(def conn  (d/connect uri))

; (d/delete-database uri)





;; Schema

(def schema [
            ;; takimlar

            {:db/id #db/id[:db.part/db]
             :db/ident :takim/name
             :db/valueType :db.type/string
             :db/cardinality :db.cardinality/one
             :db/fulltext true
             :db/unique :db.unique/identity
             :db/doc "takim adi"
             :db.install/_attribute :db.part/db}

             {:db/id #db/id[:db.part/db]
             :db/ident :takim/yil
             :db/valueType :db.type/long
             :db/cardinality :db.cardinality/one
             :db/fulltext true
             :db/doc "takim yili"
             :db.install/_attribute :db.part/db}])

(d/transact conn schema)


(def facts [
            {:db/id #db/id[:db.part/user]
             :takim/name "Besiktas"
             :takim/yil 1903}
            {:db/id #db/id[:db.part/user]
             :takim/name "FB"
             :takim/yil 1907}
            {:db/id #db/id[:db.part/user]
             :takim/name "GS"
             :takim/yil 1905}
            ])
(d/transact conn facts)


(def trabzon [            {:db/id #db/id[:db.part/user]
             :takim/name "Trabzon"
             :takim/yil 2010}])


(def sonuc  (d/transact conn trabzon))

(def prev-db (:db-before @sonuc))
(def after-db (:db-after @sonuc))


(takim-adlari-ve-idleri-ve-yillari-for-db after-db)
(keys sonuc)
(keys (deref sonuc))


;; Queries

(def db (d/db conn))

(def result-2 (d/q
               '[:find [?n ?tx ?add-or-retract]
                 :in $ ?takimadi
                 :where
                 [?e :takim/name ?takimadi ?tx ?add-or-retract]
                 [?e :takim/yil ?n]
                 ]
               db "FB"))

result-2

conn

(def trabzon-id  (d/q '[:find ?e .
                        :where [?e :takim/name "Trabzon"]] (d/db conn)))

(def trabzon-id-yil  (d/q '[:find ?e ?yil
                        :where [?e :takim/name "Trabzon"]

                        [?e :takim/yil ?yil]] (d/db conn)))

trabzon-id

(def retract-facts [[:db/retract trabzon-id :takim/yil 2010]])

(d/transact conn retract-facts)

(def raw-data (d/datoms db :eavt))

raw-data


(use 'clojure.reflection)
(def db-when-fener-was-added (d/as-of db (second result-2)))

(defn takim-adlari []
  (d/q '[:find [?name ...]
         :where
         [?e :takim/name ?name]] (d/db conn)))

(defn takim-adlari-ve-idleri []
  (d/q '[:find ?name ?e
         :where
         [?e :takim/name ?name]] (d/db conn)))


(defn takim-adlari-ve-idleri-ve-yillari []
  (d/q '[:find ?name ?e ?yil
         :where
         [?e :takim/name ?name]
         [?e :takim/yil ?yil]]
       (d/db conn)))

(defn takim-adlari-ve-idleri-ve-yillari-for-db [db]
  (d/q '[:find ?name ?e ?yil
         :where
         [?e :takim/name ?name]
         [?e :takim/yil ?yil]]
       db))


(takim-adlari-ve-idleri-ve-yillari-for-db db-when-fener-was-added)
(takim-adlari)

(takim-adlari-ve-idleri)

(takim-adlari-ve-idleri-ve-yillari)

(defn takim-sayisi []
  (count (d/q '[:find ?e
                :where
                [?e :takim/name ?name]] (d/db conn))))


(takim-sayisi)

(def my-entity-id  (d/q
                    '[:find ?e .
                      :in $ ?takimadi
                      :where
                      [?e :takim/name ?takimadi]
                      [?e :takim/yil ?n]]
                    db "FB"))


(def my-entity (d/entity db my-entity-id))

my-entity

(:takim/name my-entity)

(get my-entity :takim/sehir)

(get  {:takim/name "Trabzon"} :takim/name)





;; Transacts

(def besiktas-id (d/q '[:find ?e .
                        :where
                        [?e :takim/name "Besiktas"]]
                      (d/db conn)))

besiktas-id

(d/transact conn [{:db/id besiktas-id
                   :takim/name "Besiktas Jimnastik Klubu"}])

(d/transact conn [[:db/add besiktas-id :takim/name "Besiktas"]])

(d/transact conn [{:db/id #db/id[:db.part/db]
                   :db/ident :takim/teknik-direktor
                   :db/valueType :db.type/string
                   :db/cardinality :db.cardinality/one
                   :db.install/_attribute :db.part/db}])

(d/transact conn [[:db/add besiktas-id :takim/teknik-direktor "Senol Gunes"]])

(defn teknik-direktor [takim-adi]
  (d/q '[:find ?direktor .
         :in $ ?takim-adi
         :where
         [?e :takim/name ?takim-adi]
         [?e :takim/teknik-direktor ?direktor]]
       (d/db conn)
       takim-adi))

(teknik-direktor "Besiktas")
