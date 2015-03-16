(ns com.webtalk.storage.persistence.follower
  (:gen-class)
  (:require [clojurewerkz.cassaforte.cql :as cql]))

(def table-name "user_followers")

(defn create-follower
  [connection user-id follower-id]
  (cql/insert connection table-name {:user_id user-id
                                     :follower_id follower-id}))