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
      (let [response (model/create-or-find (:email session-data))]
        (println "the answer about to be sent is" response)
        {:session response})
      
      nil)))

(defn retrieve-session [token]
  (let [maybe-session (model/retrieve-session token)]
    (if (nil? maybe-session)
      {:status 404}
      {:status 200
       :headers {"Content-Type" "application/json"}
       :body {:session maybe-session}})))

(defn delete-session [token]
  
  (let [delete-result (model/delete-session token)]
    (println "result of delete " delete-result)
    (if (> delete-result 0)
      {:status 200}
      {:status 404})))

(defn mandatory-attributes [body]
  (and (and contains? body :session (contains? (:session body) :email)) (contains? (:session body) :password)))


(defn save-event [body]
  (if (mandatory-attributes body) (save (:session body)) {:status 400}))

(defn find-event [params]
  (if (contains? params :token) (retrieve-session (:token params)) {:status 400}))

(defn delete-event [params]
  (if (contains? params :token) (delete-session (:token params)) {:status 400}))

(defroutes routes
  (POST "/session" {params :params}
        (save-event params))

  (GET "/session" {params :params}
       (find-event params))

  (DELETE "/session" {params :params}
          (delete-event params))
  )
