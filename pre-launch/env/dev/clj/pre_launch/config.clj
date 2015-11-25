(ns pre-launch.config
  (:require [selmer.parser :as parser]
            [taoensso.timbre :as timbre]
            [pre-launch.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (timbre/info "\n-=[pre-launch started successfully using the development profile]=-"))
   :middleware wrap-dev})
