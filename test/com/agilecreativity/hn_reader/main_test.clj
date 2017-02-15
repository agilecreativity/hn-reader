(ns com.agilecreativity.hn_reader.main_test
  (:require [clojure.test :refer :all]
            [com.agilecreativity.hn_reader.main :as hr]))

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

(deftest repeat-string-test
  (is (= ""
         (hr/repeat-string 0 "a")) "zero number will result in empty string")
  (is (= ""
         (hr/repeat-string -1 "a")) "negative number will result in empty string")
  (is (= "1111"
         (hr/repeat-string 4 "1")) "positive number will result in proper result"))

(deftest hn-make-org-list-test
  (is (= " - "  (hr/hn-make-org-list 1)))
  (is (= "- "   (hr/hn-make-org-list 0)))
  (is (= "  - " (hr/hn-make-org-list 2)))
  (is (= "  * " (hr/hn-make-org-list 2 "*"))))

(deftest hacker-new-collector-test
  (hn-link-url-test)
  (hn-link-url-item-test))

(run-tests 'com.agilecreativity.hn_reader.main_test)
