(ns wt.controllers.session
  (:require [wt.persistence.session :as model]
            [compojure.core :refer [defroutes GET POST DELETE]]
            [clojure.string :as str]
            [ring.util.response :as ring]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [environ.core :refer [env]]
            [clojure.data.json :as json]
            ))

;;TODO validation of mandatory fields
(defn save [session-data]
  (let [valid-password (model/validate-password (:email session-data) (:password session-data))]
    (if (boolean valid-password)
      {:status 200
       :headers {"Content-Type" "application/json"}
       :body {:session (model/create-or-find (:email session-data))}}
      {:status 401})))

(defn mandatory-attributes [body]
  (and  (and contains? body :session (contains? (:session body) :email)) (contains? (:session body) :password)))


(defn save-event [body]
  (if (mandatory-attributes body) (save (:session body)) {:status 400}))


(defroutes routes
  (POST "/session" {body :body}
        (save-event body)))
