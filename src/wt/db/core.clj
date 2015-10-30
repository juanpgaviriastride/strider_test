(ns wt.db.core
  (:require
    [clojure.java.jdbc :as jdbc]
    [conman.core :as conman]
    [taoensso.timbre :as timbre]
    [to-jdbc-uri.core :refer [to-jdbc-uri]]
    [environ.core :refer [env]])
  (:import [java.sql
            BatchUpdateException
            PreparedStatement]
           [java.text.SimpleDateFormat]))

(defonce ^:dynamic *conn* (atom nil))

(conman/bind-connection *conn* "sql/queries.sql")

(def pool-spec
  {:adapter    :mysql
   :init-size  1
   :min-idle   1
   :max-idle   4
   :max-active 32})

(defn connect! []
  (timbre/info "creating connection in core")
  (conman/connect!
    *conn*
   (assoc
     pool-spec
     :jdbc-url (to-jdbc-uri (env :database-url))))

  (defn disconnect! []
    (conman/disconnect! *conn*)))

(defn to-date [sql-date]
  (-> sql-date (.getTime) (java.util.Date.)))

(defn sql-date [date-text]
  (let [formatter (java.text.SimpleDateFormat. "MM/dd/YYYY")] (java.sql.Date. (.getTime (.parse formatter date-text)))))

(extend-protocol jdbc/IResultSetReadColumn
  java.sql.Date
  (result-set-read-column [v _ _] (to-date v))

  java.sql.Timestamp
  (result-set-read-column [v _ _] (to-date v)))

(extend-type java.util.Date
  jdbc/ISQLParameter
  (set-parameter [v ^PreparedStatement stmt idx]
    (.setTimestamp stmt idx (java.sql.Timestamp. (.getTime v)))))

