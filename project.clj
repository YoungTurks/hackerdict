(defproject hackerdict "1.0.0-SNAPSHOT"
  :description "The community driven dictionary of hacker interests."
  :url "http://hackerdict.herokuapp.com"
  :main hackerdict.web
  :aot [hackerdict.web]
  :resource-paths ["resources"]
  :dependencies [[com.cemerick/url "0.1.1"]
                 [com.cemerick/drawbridge "0.0.7" :exclusions [ring/ring-core]]
                 [com.datomic/datomic-pro "0.9.5206" :exclusions [joda-time]]
                 [compojure "1.4.0"]
                 [environ "1.0.1"]
                 [org.postgresql/postgresql "9.4-1206-jdbc42"]
                 [http-kit "2.1.19"]
                 [lein-cljsbuild "1.1.1"]
                 [org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [sablono "0.3.6"]
                 [cljs-ajax "0.5.2"]
                 [ring-transit "0.1.4"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/data.generators "0.1.2"]
                 [org.clojure/tools.nrepl "0.2.12"]
                 [ring-basic-authentication "1.0.5"]
                 [ring/ring-codec "1.0.0"]
                 [cljsjs/moment "2.10.6-0"]
                 [ring/ring-devel "1.4.0"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [ring/ring-json "0.4.0"]
                 [selmer "0.9.5"]
                 [markdown-clj "0.9.82"]]
  :clean-targets [:target-path "out" "resources/public/cljs"]

  :cljsbuild {
              :builds {:dev {
                             :source-paths ["src"]
                             :figwheel true
                             :compiler {:main "hackerdict.core"
                                        :asset-path "cljs/out"
                                        :output-to  "resources/public/cljs/main.js"
                                        :output-dir "resources/public/cljs/out"}
                             }

                       :production {
                                    :source-paths ["src"]
                                    :compiler {:main "hackerdict.core"
                                               ;:asset-path "cljs/out"
                                               :optimizations :advanced
                                               :output-to  "resources/public/cljsp/main.js"
                                               ;:output-dir "resources/public/cljsp/out"
                                               }
                                    }}
              }
  :figwheel { ;; <-- add server level config here
             :css-dirs ["resources/public/css"]
             }
  :min-lein-version "2.0.0"
  :plugins [[lein-environ "1.0.1"]
            [lein-figwheel "0.5.0-1"]
            [lein-cljsbuild "1.1.1"]]
  :uberjar-name "hackerdict-standalone.jar"
  :profiles {:production {:env {:production true}}})
