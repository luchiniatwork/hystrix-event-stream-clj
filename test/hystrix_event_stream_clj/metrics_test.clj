(ns hystrix-event-stream-clj.metrics-test
  (:require
   [clojure.test :refer :all]
   [com.netflix.hystrix.core :as hystrix]
   [hystrix-event-stream-clj.metrics :as sut]))

(hystrix/defcommand testy
  {:hystrix/fallback-fn (constantly nil)}
  [] nil)

(deftest metrics-test

  (testing "with no hystrix commands run"
    (testing "it generates empty lists"
      (is (= [] (sut/commands)))
      (is (= [] (sut/thread-pools)))))

  (testing "with a hystrix command run"
    (testing "it returns command metrics"

      (hystrix/execute #'testy)

      (let [data (sut/commands)]
        (is (= 1 (count data)))
        (is (= "hystrix-event-stream-clj.metrics-test/testy" (-> data first :name)))
        (is (= "HystrixCommand" (-> data first :type)))))

    (testing "it returns thread pool metrics"
      (let [data (sut/thread-pools)]
        (is (= 1 (count data)))
        (is (= "HystrixThreadPool" (-> data first :type)))))))
