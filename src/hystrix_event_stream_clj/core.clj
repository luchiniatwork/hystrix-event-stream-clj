(ns hystrix-event-stream-clj.core
  (:require
   [cheshire.core :as json]
   [manifold.stream :as s]
   [hystrix-event-stream-clj.metrics :as metrics]))

(def default-delay-ms 2000)

(defn- write-metrics [stream]
  (try
    (s/put! stream (str "\nping: \n"))

    (doseq [cmd-metric (metrics/commands)]
      (s/put! stream (str "\ndata: " (json/encode cmd-metric) "\n")))

    (doseq [tp-metric (metrics/thread-pools)]
      (s/put! stream (str "\ndata: " (json/encode tp-metric) "\n")))

    true
    (catch java.io.IOException e
      false)
    (catch Exception e
      false)))

(defn- metric-streaming [delay-ms stream]
  (future
    (loop [connected true]
      (Thread/sleep delay-ms)
      (when connected (recur (write-metrics stream))))))

(defn- init-stream-channel [delay-ms stream]
  (metric-streaming delay-ms stream))

(defn mk-hystrix-stream-req-handler
  ([]
   (mk-hystrix-stream-req-handler default-delay-ms))
  ([delay-ms]
   (let [hystrix-stream (s/stream* {:permanent? true})
         metric-stream-future (init-stream-channel delay-ms hystrix-stream)]
     {:request-handler
      (fn hystrix-stream-req-handler [_]
        (let [req-stream (s/stream)
              _ (s/connect hystrix-stream req-stream)]
          {:status 200
           :headers {"Content-Type" "text/event-stream;charset=UTF-8"
                     "Cache-Control" "no-cache, no-store, max-age=0, must-revalidate"
                     "Pragma" "no-cache"}
           :body req-stream}))
      :metric-stream-future metric-stream-future})))
