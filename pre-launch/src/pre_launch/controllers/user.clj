(ns pre-launch.controllers.user
  (:require [clj-wt.queue :as queue]
            [crypto.random :refer [url-part]]
            [pre-launch.controllers.referal-notification :as referal-controller]
            [pre-launch.controllers.receipt :as receipt-controller]
            [pre-launch.controllers.confirmation-email :as confirmation-controller]
	    [taoensso.timbre :refer [debug spy]]
            [pre-launch.model.user :as model]))


(defn update-user [{titan_id :__id__ email :email :as payload}]
  (debug "inside update-user")
  (spy payload)
  (do
    (println "inside update-user. Payload:" payload)
    (confirmation-controller/deliver-email email titan_id payload)
    (model/set-titan-id email titan_id)
    titan_id))

(defn user-exists? [email]
  (debug "inside user-exists?")
  (let [count (model/user-count email)]
    (spy count)
    (> count 0)))

(defn create-user! [{{first_name :first_name last_name :last_name
                      email :email password :password} :params
                     session :session}]
  (let [puser  (model/save {:name (str first_name " " last_name)
                            :email email
                            :password password
                            :stripe_account_id (session :stripe-costumer)})
        callback-queue-name (str "com.webtalk.pre-launch.user." (url-part 15))
        insertion-result (queue/promise-subscription callback-queue-name update-user)]
    (receipt-controller/deliver-email email (:id puser))
    (when-let [referer-id (:refererID session)]
      (referal-controller/notify-referer referer-id (str first_name " " last_name)))
    (println "the refererID in create user is:" (:refererID session))
    (queue/publish-with-qname "com.webtalk.pre-launch.create-user" callback-queue-name {:email email
                                                                                        :name (str first_name " " last_name)
                                                                                        :refererID (:refererID session)})
    (assoc puser :titan_id @insertion-result)))
