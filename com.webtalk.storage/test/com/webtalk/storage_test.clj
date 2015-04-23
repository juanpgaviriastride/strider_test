(ns com.webtalk.storage-test
  (:use midje.sweet)
  (:require [com.webtalk.storage :refer :all]))

(facts "about storage service"
       (fact "it works"
             (+ 1 2) => 3)
       (fact "it doesn't work"
             (+ 3 4 5) =not=> 3))
