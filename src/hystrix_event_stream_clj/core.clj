(ns hystrix-event-stream-clj.core
  (:require
   [cheshire.core :as json]
   [manifold.stream :as s]
   [hystrix-event-stream-clj.metrics :as metrics]))

(def default-delay 2000)

(defn- write-metrics [stream]
  (try
    (s/put! stream (str "\nping: \n"))

    (doseq [command-metric (metrics/commands)]
      (s/put! stream (str "\ndata: " (json/encode command-metric) "\n")))

    (doseq [thread-pool-metric (metrics/thread-pools)]
      (s/put! stream (str "\ndata: " (json/encode thread-pool-metric) "\n")))

    true
    (catch java.io.IOException e
      false)
    (catch Exception e
      false)))

(defn- metric-streaming [stream]
  (future
    (loop [connected true]
      (Thread/sleep default-delay)
      (when connected (recur (write-metrics stream))))))

(defn- init-stream-channel [stream]
  (metric-streaming stream))

(defn hystrix-stream []
  (let [hystrix-stream_ (s/stream)
        _ (init-stream-channel hystrix-stream)]
    {:status 200
     :headers {"Content-Type" "text/event-stream;charset=UTF-8"
               "Cache-Control" "no-cache, no-store, max-age=0, must-revalidate"
               "Pragma" "no-cache"}
     :body hystrix-stream_}))
