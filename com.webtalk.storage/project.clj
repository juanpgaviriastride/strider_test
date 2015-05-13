(defproject com.webtalk.storage "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clojurewerkz/titanium "1.0.0-beta2"]
                 [clojurewerkz/cassaforte "2.0.0"]
                 [clojurewerkz/elastisch "2.2.0-beta1"]
                 ;; [clojurewerkz/route-one "1.1.0"]
                 ;; [clojurewerkz/mailer "1.2.0"]
                 ;; [clojurewerkz/serialism "1.3.0"]
                 [org.clojure/data.json "0.2.5"]
                 ;; [com.taoensso/timbre "3.4.0"]
                 [com.novemberain/langohr "3.1.0"]
                 ;; [com.novemberain/validateur "2.4.2"]
                 [com.netflix.hystrix/hystrix-clj "1.4.1"]
                 [hystrix-event-stream-clj "0.1.3"]
                 [compojure "1.3.2"]
                 [ring "1.3.2"]
                 [org.eclipse.jetty/jetty-servlet "7.6.13.v20130916"]
                 [com.thinkaurelius.titan/titan-all "0.5.0"]
                 [environ "1.0.0"]
                 [org.apache.commons/commons-daemon "1.0.9"]
                 [sendgrid-java-wrapper "0.1.0-SNAPSHOT"]
                 [crypto-random "1.2.0"]
                 [de.ubercode.clostache/clostache "1.4.0"]]
  :main ^:skip-aot com.webtalk.storage
  :target-path "target/%s"
  :resource-paths ["resources"]
  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[midje "1.6.3" :exclusions [org.clojure/clojure]]]
                   :plugins [[lein-midje "3.1.3"]]}})
