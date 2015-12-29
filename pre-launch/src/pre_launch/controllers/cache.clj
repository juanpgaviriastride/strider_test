(ns pre-launch.controllers.cache
  (:require [taoensso.carmine :as car :refer (wcar)]
            [environ.core :refer [env]]))

(def server1-conn {:pool {}
                   :spec {:uri (or (env :redis-uri)
                                  "redis://localhost:6379")}})

(defmacro wcar* [& body]
  `(car/wcar server1-conn ~@body))

(defn cache*
  ([key value ttl]
   (wcar* (car/set key value)
          (car/expire key ttl)))
  ([key value]
   (cache* key value 1800)))

(defmacro defcached [key value]
  `(if-let [cached-value# (wcar* (car/get ~key))]
     cached-value#
     (let [value# ~value]
       (cache* ~key value#)
       value#)))

(defn invalid [keys]
  (wcar* (apply car/del keys)))
