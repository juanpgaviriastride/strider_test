(ns pre-launch.controllers.user
  (:require [clj-wt.queue :as queue]
            [crypto.random :refer [url-part]]
            [pre-launch.model.user :as model]))


(defn update-user [{titan_id :__id__ email :email :as payload}]
  (do
    (model/set-titan-id email titan_id)
    titan_id))

(defn create-user! [{{first_name :first_name last_name :last_name
                      email :email password :password} :params
                     session :session}]
  (let [puser  (model/save {:name (str first_name " " last_name)
                            :email email
                            :password password
                            :stripe_account_id (session :stripe-costumer)})
        callback-queue-name (str "com.webtalk.pre-launch.user." (url-part 15))
        insertion-result (queue/promise-subscription callback-queue-name update-user)]
    
    (queue/publish-with-qname "com.webtalk.pre-launch.create-user" callback-queue-name {:email email
                                                                                       :name (str first_name " " last_name)
                                                                                       :refererID (:refererID session)
                                                                                       })
    (assoc puser :titan_id @insertion-result)))
