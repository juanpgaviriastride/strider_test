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
  (model/save user))


(defn mandatory-attributes [body]
  (-> body
     (contains? :name))
  ;;(and contains? body :pass (contains? body :email))
  )

(defn event-happened [body]
  (if (mandatory-attributes body) (save body) (println "doesn't have minimim!"))
  ;;(save/body)
)



(defroutes routes
  (POST "/user" {body :body}
       (event-happened body)
       {:status 200})
  )
