(ns com.webtalk.storage.persistence.following
  (:gen-class)
  (:require [clojurewerkz.cassaforte.cql :as cql]))

(def table-name "user_followings")

(defn create-following
  [connection user-id following-id]
  (cql/insert connection table-name {:user_id user-id
                                     :following_id following-id}))