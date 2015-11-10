(ns wt.controllers.user
  (:require [wt.persistence.user :as model]
            [compojure.core :refer [defroutes GET POST DELETE]]
            [clojure.string :as str]
            [ring.util.response :as ring]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [environ.core :refer [env]]
            [clojure.data.json :as json]
            ))

;;TODO validation of mandatory fields
(defn save [user]
  {:user  (dissoc (assoc user :id (:generated_key (model/save user))) :password)})

(defn get [email]
  (println "the user requested is" email)
  (let [users (model/get email)]
    (println "the users fetched in controller are" {:user (first users)})
    {:user  users}))

(defn delete [id]
  (let [result (model/delete id)]
    (if (> result 0) "200"  "404")))

(defn update [user-map id]
  (let [user-result (model/update-user user-map id)]
    (println "the result of updating is " user-result)
    (if (= user-result 1)
      {:user (assoc user-map :id id)}
      {:user nil})))

  


(defn mandatory-attributes [body]
  (and contains? body :user (contains? (:user body) :email )))

(defn save-event [body]
  (if (mandatory-attributes body) (save (:user body)) {:message "Mandatory fields not present"}))

(defn get-event [params]
  (if (contains? params :id) (get (:id params)) (println "not enough params")))

(defn delete-event [params]
  (if (contains? params :id) (delete (:id params)) (println "not enough params")))

(defroutes routes
  (POST "/user" {params :params}
       {:status 200
        :headers {"Content-Type" "application/json"}
        :body (save-event params)
        })
  (GET "/user/:id" {params :params}
       {:status 200
        :headers {"Content-Type" "application/json"}
        :body (get-event params)
        })
  (DELETE "/user/:id" {params :params}
       {:status (delete-event params)})
  )
