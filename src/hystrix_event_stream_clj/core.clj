(ns hystrix-event-stream-clj.core
  (:require
   [cheshire.core :as json]
   [clojure.core.async :refer [chan go >! pipe]]
   [hystrix-event-stream-clj.metrics :as metrics]))

(def default-delay-ms 2000)

(defn ^:private write-metrics [chan]
  (try
    (go
      (>! chan (str "\nping: \n"))

      (doseq [cmd-metric (metrics/commands)]
        (>! chan (str "\ndata: " (json/encode cmd-metric) "\n")))

      (doseq [tp-metric (metrics/thread-pools)]
        (>! chan (str "\ndata: " (json/encode tp-metric) "\n"))))

    true
    (catch java.io.IOException e
      false)
    (catch Exception e
      false)))

(defn ^:private metric-streaming [delay-ms chan]
  (future
    (loop [connected true]
      (Thread/sleep delay-ms)
      (when connected (recur (write-metrics chan))))))

(defn ^:private init-stream-channel [delay-ms chan]
  (metric-streaming delay-ms chan))

(defn mk-hystrix-stream-req-handler
  ([]
   (mk-hystrix-stream-req-handler default-delay-ms))
  ([delay-ms]
   (let [hystrix-chan (chan)
         metric-stream-future (init-stream-channel delay-ms hystrix-chan)]
     {:request-handler
      (fn hystrix-stream-req-handler [_]
        (let [out-chan (chan)]
          (pipe hystrix-chan out-chan)
          {:status 200
           :headers {"Content-Type" "text/event-stream;charset=UTF-8"
                     "Cache-Control" "no-cache, no-store, max-age=0, must-revalidate"
                     "Pragma" "no-cache"}
           :body out-chan}))
      :metric-stream-future metric-stream-future})))
