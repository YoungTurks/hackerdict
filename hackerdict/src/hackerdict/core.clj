(ns hackerdict.core
  (:import datomic.Util java.util.Random)
  (:require
   [datomic.api :as d]))

(def uri "datomic:dev://localhost:4334/main")

(d/create-database uri)

(def conn  (d/connect uri))
