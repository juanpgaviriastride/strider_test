(ns com.webtalk.mailer.invite-test
  (:use midje.sweet)
  (:require [com.webtalk.mailer.invite :refer :all]))

(facts "about mailing invites"
       (fact "simple example"
             (deliver-email "" "" "sebastianarcila@gmail.com") =>  "{\"message\":\"success\"}")
       ;; (fact "simple example for rj"
       ;;       (deliver-email "" "" "rj@webtalk.org"))
       ;; (fact "simple example for david"
       ;;       (deliver-email "" "" "david@nullindustries.co"))
       ;; (fact "simple example for tillo"
       ;;       (deliver-email "" "" "davidcastillobuiles@gmail.com"))
       ;; (fact "simple example for pachangas"
       ;;       (deliver-email "" "" "juanpablo@nullindustries.co"))
       )

