(ns com.webtalk.mailer.invite-test
  (:use midje.sweet)
  (:require [com.webtalk.mailer.invite :refer :all]))

(facts "about mailing invites"
       (fact "simple example"
             (deliver-email "" "" "sebastianarcila@gmail.com") => ""))

