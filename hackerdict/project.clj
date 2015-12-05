(defproject hackerdict "1.0.0-SNAPSHOT"
  :description "The community driven dictionary of hacker interests."
  :url "http://hackerdict.herokuapp.com"
  :repositories {"my.datomic.com" {:url "https://my.datomic.com/repo"
                                   :creds :gpg}}
  :dependencies [[com.cemerick/drawbridge "0.0.7"]
                 [com.datomic/datomic-pro "0.9.5206"]
                 [compojure "1.4.0"]
                 [environ "1.0.1"]
                 [org.clojure/clojure "1.6.0"]
                 [org.clojure/data.generators "0.1.2"]
                 [ring-basic-authentication "1.0.5"]
                 [ring/ring-devel "1.4.0"]
                 [ring/ring-jetty-adapter "1.4.0"]]
  :min-lein-version "2.0.0"
  :plugins [[environ/environ.lein "0.2.1"]]
  :hooks [environ.leiningen.hooks]
  :uberjar-name "hackerdict-standalone.jar"
  :profiles {:production {:env {:production true}}})
