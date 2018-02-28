(ns calmen.core-test
  (:require [clojure.test :refer :all]
            [calmen.core :refer :all]))

(deftest get-html-test
  (testing "Simple http get test."
    (def ret (calmen.core/fetch-url "http://example.com"))
    (is (< 0 (count ret)))))
