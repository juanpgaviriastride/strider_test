(ns wt.controllers.user
  (:require [wt.persistence.user :as model]
            [compojure.core :refer [defroutes GET POST ANY]]
            [clojure.string :as str]
            [ring.util.response :as ring]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [environ.core :refer [env]]
            [clojure.data.json :as json]
            ))

;;TODO validation of mandatory fields
(defn save [user]
  (dissoc (assoc user :id (:generated_key (model/save user))) :pass))


(defn mandatory-attributes [body]
  (and contains? body :pass (contains? body :email)))

(defn save-event [body]
  (if (mandatory-attributes body) (save body) ({:message "Mandatory fields not present"})))



(defroutes routes
  (POST "/user" {body :body}
       {:status 200
        :headers {"Content-Type" "application/json"}
        :body (save-event body)
        })
  )
