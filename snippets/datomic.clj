(ns snippets.datomic
  (:import datomic.Util java.util.Random)
  (:require [datomic.api :as d]))

(def uri "datomic:dev://localhost:4334/test")

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





;; Queries

(def db (d/db conn))

(d/q
 '[:find ?n .
  :in $ ?takimadi
   :where
   [?e :takim/name ?takimadi]
   [?e :takim/yil ?n]
   ]
 db "FB")

(defn takim-adlari []
  (d/q '[:find [?name ...]
         :where
         [?e :takim/name ?name]] (d/db conn)))

(defn takim-adlari-ve-idleri []
  (d/q '[:find ?name ?e
         :where
         [?e :takim/name ?name]] (d/db conn)))

(takim-adlari)

(takim-adlari-ve-idleri)

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
