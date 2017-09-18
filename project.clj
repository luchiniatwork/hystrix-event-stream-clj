(defproject luchiniatwork/hystrix-event-stream-clj "0.3.0"
  :description "Generate hystrix event streams"
  :url "http://github.com/unbounce/hystrix-event-stream-clj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.3.443"]
                 [com.stuartsierra/component "0.3.2"]
                 [com.fasterxml.jackson.core/jackson-core "2.9.0"]
                 [com.netflix.hystrix/hystrix-clj "1.5.12"]
                 [com.netflix.hystrix/hystrix-metrics-event-stream "1.5.12"]
                 [cheshire "5.8.0"
                  :exclusions [com.fasterxml.jackson.core/jackson-core]]]

  :profiles {:dev {:plugins [[jonase/eastwood "0.2.1"]]}}

  :release-tasks [["clean"]
                  ["eastwood"]                  ]

  :deploy-repositories [["releases" :clojars]])
