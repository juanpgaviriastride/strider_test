(ns com.webtalk.storage.persistence.invitation
  (:gen-class)
  (:require [clojurewerkz.cassaforte.cql :as cql]))

(def table-name "invitations")

(defn create-invitation
  [connection id payload]
  (cql/insert connection table-name {:invitation_id id
                                     :email (payload "email")
                                     :inviter_id (payload "inviter_id")}))