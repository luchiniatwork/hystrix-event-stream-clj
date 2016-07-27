(ns hystrix-event-stream-clj.component
  (:require [com.stuartsierra.component :as component]
            [hystrix-event-stream-clj.core :as core]
            [aleph.http.server :as http]))


(defrecord HystrixEventStream [config http-server]
  component/Lifecycle
  (start [self]
    (if (nil? http-server)
      (assoc self
             :http-server
             (http/start-server (fn [_] (core/stream))
                                {:port (get-in config
                                               [:hystrix-event-stream :http-port]
                                               8080)}))
      ;; else
      self))

  (stop [self]
    (if-not (nil? http-server)
      (do
        (.stop http-server)
        (dissoc self :http-server))
      ;; else
      self)))

(defn hystrix-event-stream []
  (component/using (map->HystrixEventStream {})
                   [:config]))
