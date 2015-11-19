(ns pre-launch.config
  (:require [taoensso.timbre :as timbre]))

(def defaults
  {:init
   (fn []
     (timbre/info "\n-=[pre-launch started successfully]=-"))
   :middleware identity})
