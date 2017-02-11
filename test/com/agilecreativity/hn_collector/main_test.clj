(ns com.agilecreativity.hn_collector.main_test
  (:require [clojure.test :refer :all]
            ;; Note: is :refer :all the good practices?, probably not!
            ;;[com.agilecreativity.hn_collector :refer :all]
))

(deftest addition
  (is (= 4 (+ 2 2)))
  (is (= 7 (+ 3 4))))

(deftest subtraction
  (is (= 1 (- 4 3)))
  (is (= 3 (- 7 4))))

(deftest arithmetic
  (addition)
  (subtraction))

(run-tests 'com.agilecreativity.hn_collector.main_test)
