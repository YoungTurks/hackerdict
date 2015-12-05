(defproject hackerdict "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :repositories {"my.datomic.com" {:url "https://my.datomic.com/repo"
                                   :creds :gpg}}
  :profiles {:dev {:dependencies [[alembic "0.3.2"]]}}
  :main hackerdict.web
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [ring "1.4.0"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/data.generators "0.1.2"]
                 [com.datomic/datomic-pro "0.9.5206" :exclusions [joda-time]]])
