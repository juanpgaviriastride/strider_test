(ns com.webtalk.controllers.user
  (:require [wt.persistence.user :as model]))

;;TODO validation of mandatory fields
(defn save [user]
  (model/save user))
