(ns pre-launch.controllers.landing
  (:require [pre-launch.model.invitation-request :as invitation-model]
            [pre-launch.model.stripe-account :as stripe-model]
            [taoensso.timbre :refer [spy]]
            [pre-launch.config-mailer :as config-mailer]))


(defn get-overal-app-stats []
  (let [waitlist (+ (invitation-model/invitation-request-count) (config-mailer/waitlist-mig))
        prelaunch (spy (+ (stripe-model/stripe-users-count)  (config-mailer/prelaunch-mig)))
        credits (* prelaunch 100)
        comissions (/ credits 2)]
    {:waitlist-users waitlist
     :pre-launch-users prelaunch
     :webtalk-credits credits 
     :comissions comissions}))
