# hystrix-event-stream-clj

Easy way to setup a Hystrix (https://github.com/Netflix/Hystrix) event stream without
having to use servlets.

## Table of Contents

* [Getting Started](#getting-started)
* [Usage](#usage)
* [The Real Fun](#the-real-fun)
* [In Action](#in-action)
* [Recommended Usage with Components](#recommended-usage-with-components)
* [Acknowledgements](#acknowledgements)

## Getting Started

Add the following dependency to your `project.clj` file:

[![Clojars Project](http://clojars.org/luchiniatwork/hystrix-event-stream-clj/latest-version.svg)](http://clojars.org/luchiniatwork/hystrix-event-stream-clj)

## Usage

The function `mk-hystrix-stream-req-handler` in the `hystrix-event-stream-clj.core`
namespace returns a map with two entries:

* `:metric-stream-future` - a future that can be canceled to stop collecting metrics
* `:request-handler` - a request handler to be attached to your web server routing system

Here's an example of attaching the returned `request-handler` to a Pedestal route:

```clojure
#{["/hystrix.stream"
   :get request-handler
   :route-name :hystrix-stream]}
```

If you need to cancel the collection of metrics, cancel the future with:

```clojure
(future-cancel metric-stream-future)
```

## In Action

Assuming the endpoint you attach the request-handler to was `http://localhost:5000/hystrix.streamTest`, you can test the event stream by curling:

```
curl http://locahost:5000/hystrix.stream

data: []

data: []
```

## The Real Fun

This stream makes it possible to connect your Hystrix-enabled servers with a
[Hystrix Dashboard](https://github.com/Netflix/Hystrix/tree/master/hystrix-dashboard)
(or [Hystrix Turbine](https://github.com/Netflix/Turbine)).
You will be able to see something like this on your dashboard:

![Hystrix Dashboard](https://github.com/Netflix/Hystrix/wiki/images/hystrix-dashboard-netflix-api-example-iPad.png)

## Recommended Usage with Components

It's recommended to wrap `hystrix-event-stream-clj` in a Component to make initialization
and dependency management easier:

```clojure
(ns my-project.components.hystrix-stream
  (:require [com.stuartsierra.component :as component]
            [hystrix-event-stream-clj.core :as stream]))

(defrecord HystrixStream [request-handler metric-stream-future]
  component/Lifecycle
  (start [this]
    (println "HystrixStream: starting...")
    (let [{:keys [request-handler metric-stream-future]}
          (stream/mk-hystrix-stream-req-handler)]
      (assoc this
             :metric-stream-future metric-stream-future
             :request-handler request-handler)))
  
  (stop [this]
    (println "HystrixStream: stopping...")
    (if metric-stream-future
      (future-cancel metric-stream-future))
    (assoc this :metric-stream-future nil)))

(defn new-hystrix-stream []
  (map->HystrixStream {}))

```

Then just injecting it in your system with:

```clojure
(component/system-map
  ;; Hystrix Stream as a dependency
  :hystrix-stream (hystrix-stream/new-hystrix-stream))
```

## Acknowledgements

This project is an updated fork from [hystrix-event-stream-clj](https://github.com/josephwilk/hystrix-event-stream-clj)
by Joseph Wilk. I needed this on a project and his codebase was pretty dated. Most of the legwork
was done by him.

## License

(The MIT License)

Copyright © 2017 Tiago Luchini

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the 'Software'), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
