(ns hystrix-event-stream-clj.core
  (:require
   [cheshire.core :as json]
   [manifold.stream :as s]
   [hystrix-event-stream-clj.metrics :as metrics]))

(def default-delay 2000)

(defn- write-metrics [stream]
  (try
    (s/put! stream (str "\nping: \n"))
    (doall (map #(s/put! stream (str "\ndata: " (json/encode %) "\n")) (metrics/commands)))
    (doall (map #(s/put! stream (str "\ndata: " (json/encode %) "\n")) (metrics/thread-pools)))
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
