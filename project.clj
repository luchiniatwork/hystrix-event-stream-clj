(defproject com.unbounce/hystrix-event-stream-clj "0.2.0"
  :description "Generate hystrix event streams"
  :url "http://github.com/unbounce/hystrix-event-stream-clj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [com.stuartsierra/component "0.3.1"]
                 [com.fasterxml.jackson.core/jackson-core "2.7.5"]
                 [com.netflix.hystrix/hystrix-clj "1.5.3"]
                 [com.netflix.hystrix/hystrix-metrics-event-stream "1.5.3"]
                 [aleph "0.4.2-alpha6"]
                 [cheshire "5.6.3"
                  :exclusions [com.fasterxml.jackson.core/jackson-core]]]

  :profiles {:dev {:plugins [[lein-kibit "0.1.2"]
                             [jonase/eastwood "0.2.1"]]}}

  :release-tasks [["clean"]
                  ["kibit"]
                  ["eastwood"]
                  ["test"]
                  ["change" "version"
                   "leiningen.release/bump-version" "release"]
                  ["vcs" "commit"]
                  ["vcs" "tag"]
                  ["deploy" "clojars"]
                  ["change" "version"
                   "leiningen.release/bump-version"]
                  ["vcs" "commit"]
                  ["vcs" "push"]]

  :deploy-repositories [["releases" :clojars]])
