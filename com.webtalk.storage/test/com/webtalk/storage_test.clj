(ns com.webtalk.storage-test
  (:user midje.sweet)
  (:require [com.webtalk.storage :refer :all]))

(facts "about storage service"
       (fact "it works"
             (+ 1 2) => 3))

