(ns com.agilecreativity.hn_collector.main_test
  (:require [clojure.test :refer :all]
            [com.agilecreativity.hn_collector.main :as hr]))

(deftest hn-link-url-test
  (is (= "[[http://www.google.com][http://www.google.com]]" (hr/hn-link-url "http://www.google.com")))
  (is (= "[[http://www.google.com][Google]]") (hr/hn-link-url "http://www.google.com" "Google")))

(deftest hn-link-url-item-test
  (is (= "[[http://www.google.com][http://www.google.com]]"
          (hr/hn-link-url-item "http://www.google.com" "http://www.google.com" 0)))
  (is (= "[[http://www.google.com][http://www.google.com]]"
         (hr/hn-link-url-item "http://www.google.com" "http://www.google.com")))
  (is (= "[[http://www.google.com][Google]]"
         (hr/hn-link-url-item "http://www.google.com" "Google")))
  (is (= "  - [[http://www.google.com][Google]]"
         (hr/hn-link-url-item "http://www.google.com" "Google" 2))))

(deftest hacker-new-collector-test
  (hn-link-url-test)
  (hn-link-url-item-test))

(run-tests 'com.agilecreativity.hn_collector.main_test)
