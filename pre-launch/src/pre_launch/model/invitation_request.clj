(ns pre-launch.model.invitation-request
  (:require [pre-launch.db.core :as db]
            [taoensso.timbre :refer [debug]]))


(defn save-invitation-request [email referer_id]
  (debug "the invitation request to the database" email referer_id)
  (db/create-invitation-request<! {:email email :referer_id referer_id}))

(defn invitation-request-count []
  (let [response (db/get-invitation-requests)]
    (debug "the response in invitation-request-count is" response)
    (:total (first response))))
