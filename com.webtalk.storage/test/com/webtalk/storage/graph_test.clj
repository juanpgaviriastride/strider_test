(ns com.webtalk.storage.graph-test
  (:use midje.sweet)
  (:require [com.webtalk.storage.graph :refer :all]))

(facts "about connection-session"
       (fact "return a titanium graph"
             (type (connection-session)) => "Graph"))

(facts "about shutdown"
       (fact "shutdows the titanium graph"
             (type (shutdown "Graph")) => nil))
