(defproject guiced-apps "0.0.1-SNAPSHOT"
  :description "Clojure Oauth Sample App"
  :url ""
  :license {:name "MIT"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.3.1"]
                 [ring/ring-json "0.1.2"]
                 [ring/ring-jetty-adapter "1.2.1"]
                 [ring "1.3.1"]
                 [cheshire "5.3.1"]
                 [com.cemerick/friend "0.2.0" :exclusions [ring/ring-core]]
                 [friend-oauth2 "0.1.1" :exclusions [org.apache.httpcomponents/httpcore]]
                 [congomongo "0.4.4"]
                 [environ "1.0.0"]
                 [org.clojure/clojurescript "0.0-2138"]
                 [cljs-http "0.1.2" :exclusions [org.clojure/clojure]]
                 [om "0.3.6"]
                 [reagent "0.4.3"]]

  :preamble ["reagent/react.js"]

  :plugins [[lein-cljsbuild "1.0.1"]
            [lein-ring "0.8.7"]
            [lein-environ "1.0.0"]]

  :resource-paths ["resources"]

  :target-path "target/$s"

  :main ^:skip-aot clj-auth-sample.core

  :profiles {:uberjar {:aot :all}})
