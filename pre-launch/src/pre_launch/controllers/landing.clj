(ns pre-launch.controllers.landing
  (:require [pre-launch.model.invitation-request :as invitation-model]
            [pre-launch.model.stripe-account :as stripe-model]))


(defn get-overal-app-stats []
  (let [waitlist (invitation-model/invitation-request-count)
        prelaunch (stripe-model/stripe-users-count)
        pp (println "the prelaunch is" prelaunch)
        credits (* prelaunch 100)
        comissions (/ credits 2)
        ]
    {:waitlist-users waitlist
     :pre-launch-users prelaunch
     :webtalk-credits credits 
     :comissions comissions
     }))
