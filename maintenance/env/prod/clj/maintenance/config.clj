(ns maintenance.config
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[maintenance started successfully]=-"))
   :middleware identity})
