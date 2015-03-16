(ns com.webtalk.storage.persistence.user
  (:gen-class)
  (:require [clojurewerkz.cassaforte.cql :as cql]))

(def table-name "users")

(defn create-user
  [connection id payload]
  (cql/insert connection table-name {:user_id id
                                     :email (payload "email")
                                     :full_name (payload "full_name")
                                     :username (payload "username")}))