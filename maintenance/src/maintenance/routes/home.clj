(ns maintenance.routes.home
  (:require [maintenance.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [not-found]]))

(defn home-page []
  (layout/render "home.html"))

(defroutes home-routes
  (GET "/" [] (home-page))
  (not-found (home-page)))

