(defproject hackerdict "1.0.0-SNAPSHOT"
  :description "The community driven dictionary of hacker interests."
  :url "http://hackerdict.herokuapp.com"
  :main hackerdict.web
  :aot [hackerdict.web]
  :repositories {"my.datomic.com" {:url "https://my.datomic.com/repo"
                                   :username [:env/datomic-username]
                                   :password [:env/datomic-password]}}
  :dependencies [[com.cemerick/url "0.1.1"]
                 [com.cemerick/drawbridge "0.0.7" :exclusions [ring/ring-core]]
                 [com.datomic/datomic-pro "0.9.5206" :exclusions [joda-time]]
                 [compojure "1.4.0"]
                 [environ "1.0.1"]
                 [org.postgresql/postgresql "9.4-1206-jdbc42"]
                 [http-kit "2.1.18"]
                 [org.clojure/clojure "1.7.0"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/data.generators "0.1.2"]
                 [org.clojure/tools.nrepl "0.2.11"]
                 [ring-basic-authentication "1.0.5"]
                 [ring/ring-codec "1.0.0"]
                 [ring/ring-devel "1.4.0"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [ring/ring-json "0.4.0"]]
  :min-lein-version "2.0.0"
  :plugins [[lein-environ "1.0.1"]]
  :uberjar-name "hackerdict-standalone.jar"
  :profiles {:production {:env {:production true}}})
