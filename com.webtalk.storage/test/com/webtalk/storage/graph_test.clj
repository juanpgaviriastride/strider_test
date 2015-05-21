(ns com.webtalk.storage.graph-test
  (:use midje.sweet)
  (:require [com.webtalk.storage.graph :refer :all]))

(facts "about connection-session"
       (fact "return a titanium graph"
             (type (connection-session)) => com.thinkaurelius.titan.graphdb.database.StandardTitanGraph))

(facts "about shutdown"
       (fact "shutdows the titanium graph"
             (type (shutdown (connection-session))) => nil))
