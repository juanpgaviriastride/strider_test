(ns pre-launch.model.invitation-request
  (:require [pre-launch.db.core :as db]))


(defn save-invitation-request [email referer_id]
  (println "printing the invitation request to the database")
  (db/create-invitation-request<! {:email email :referer_id referer_id}))

(defn invitation-request-count []
  (let [response (db/get-invitation-requests)]
    (println "the response in invitation-request-count is" response)
    (:total (first response))))
