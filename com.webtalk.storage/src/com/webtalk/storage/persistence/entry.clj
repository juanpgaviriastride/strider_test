(ns com.webtalk.storage.persistence.entry
  (:gen-class)
  (:require [clojurewerkz.cassaforte.cql :as cql]))

(def table-name "entries")

(defn create-entry
  [connection timeuuid owner-id payload]
  (cql/insert connection table-name {:owner_id owner-id
                                     :entry_id timeuuid
                                     :type (payload "type")
                                     :title (payload "title")
                                     :text_content (payload "text_content")
                                     :url_content (payload "url_content")
                                     :file_content (payload "file_content")}))