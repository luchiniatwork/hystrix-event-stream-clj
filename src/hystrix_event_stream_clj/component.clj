(ns hystrix-event-stream-clj.component
  (:require [com.stuartsierra.component :as component]
            [hystrix-event-stream-clj.core :as core]
            [aleph.http.server :as http]))


(defrecord HystrixEventStream [config http-server metric-stream-future]
  component/Lifecycle
  (start [self]
    (if (nil? http-server)
      (let [{:keys [request-handler metric-stream-future]}
            (core/mk-hystrix-stream-req-handler)]
        (assoc self
               :http-server
               (http/start-server request-handler
                                  {:port (get-in config
                                                 [:hystrix-event-stream :http-port]
                                                 8080)})
               :metric-stream-future
               metric-stream-future))
      ;; else
      self))

  (stop [self]
    (if-not (nil? http-server)
      (do
        (.close http-server)
        (future-cancel metric-stream-future)
        (dissoc self
                :http-server
                :metric-stream-future))
      ;; else
      self)))

(defn hystrix-event-stream []
  (component/using (map->HystrixEventStream {})
                   [:config]))
