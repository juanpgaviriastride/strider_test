(ns maintenance.config
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [maintenance.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[maintenance started successfully using the development profile]=-"))
   :middleware wrap-dev})
