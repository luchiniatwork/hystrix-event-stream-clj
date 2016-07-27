# hystrix-event-stream-clj

[![Build Status](https://travis-ci.org/unbounce/hystrix-event-stream-clj.png?branch=master)](https://travis-ci.org/unbounce/hystrix-event-stream-clj)

Easy way to setup a Hystrix (https://github.com/Netflix/Hystrix) event stream without having to use servlets.

## Install

Add to your `project.clj`

```
:dependencies [ ;; ..
                [hystrix-event-stream-clj "0.2.0"]
                ;; ..
              ]
```

https://clojars.org/hystrix-event-stream-clj

## Usage with Aleph

```clojure
 (ns example.handler
  (:require [aleph.server :as http]
            [com.netflix.hystrix.core :refer [defcommand]]
            [hystrix-event-stream-clj.core :refer [hystrix-stream]))

(defcommand hello
  "Safe hello!"
  []
  "Hello world!")

(defn ring-app [req]
  (cond
    (= (:request-uri req) "/")
    {:status 200 :headers {} :body (hello)}

    (= (:request-uri req) "/hystrix.stream")
    (hystrix-stream)

    :else
    {:status 404 :body "not found" :headers {}}))

(defn -main
  [port]
  (http/start-server ring-app {:port (Integer. port)}))
```

Test the event stream by curling:

```
curl locahost:5000/hystrix.stream

data: []

data: []
```

## But Why?

The event stream can be consumed by the Hystrix Dashboard. Giving you pretty mointoring of all circuit breakers.

![Hystrix Dashboard](https://monosnap.com/image/nOFxuqgzQ6evEeGa2iA2r4ANn.png)

## License

(The MIT License)

Copyright © 2013 Joseph Wilk

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the 'Software'), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
