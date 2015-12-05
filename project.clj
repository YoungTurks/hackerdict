(defproject hackerdict "1.0.0-SNAPSHOT"
  :description "The community driven dictionary of hacker interests."
  :url "http://hackerdict.herokuapp.com"
  :main hackerdict.web
  :repositories {"my.datomic.com" {:url "https://my.datomic.com/repo"
                                   :username [:env/datomic_username]
                                   :password [:env/datomic_password]}}
  :dependencies [[com.cemerick/url "0.1.1"]
                 [com.cemerick/drawbridge "0.0.7" :exclusions [ring/ring-core]]
                 [com.datomic/datomic-pro "0.9.5206"]
                 [compojure "1.4.0"]
                 [environ "1.0.1"]
                 [http-kit "2.1.18"]
                 [org.clojure/clojure "1.6.0"]
                 [org.clojure/data.generators "0.1.2"]
                 [org.clojure/tools.nrepl "0.2.11"]
                 [ring-basic-authentication "1.0.5"]
                 [ring/ring-devel "1.4.0"]
                 [ring/ring-jetty-adapter "1.4.0"]]
  :min-lein-version "2.0.0"
  :plugins [[environ/environ.lein "0.2.1"]]
  :hooks [environ.leiningen.hooks]
  :uberjar-name "hackerdict-standalone.jar"
  :profiles {:production {:env {:production true}}})
