(ns com.webtalk.storage.persistence.referrer
  (:gen-class)
  (:require [clojurewerkz.cassaforte.cql :as cql]))

(def table-name "referrer")

(defn create-referrer
  [connection user-id referred-id]
  (cql/insert connection table-name {:user_id user-id
                                     :referred_user_id referred-id}))