(ns pre-launch.controllers.landing
  (:require [pre-launch.model.invitation-request :as invitation-model]
            [pre-launch.model.stripe-account :as stripe-model]
            [taoensso.timbre :refer [spy]]))


(defn get-overal-app-stats []
  (let [waitlist (invitation-model/invitation-request-count)
        prelaunch (spy (stripe-model/stripe-users-count))
        credits (* prelaunch 100)
        comissions (/ credits 2)]
    {:waitlist-users waitlist
     :pre-launch-users prelaunch
     :webtalk-credits credits 
     :comissions comissions}))
