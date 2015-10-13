(ns com.webtalk.storage.persistence.user
  (:gen-class)
  (:require [clojurewerkz.cassaforte.cql :as cql]))

(def table-name "users")

(defn create-user
  [connection id payload]
  (cql/insert connection table-name {:user_id id
                                     :email (payload "email")
                                     :full_name (payload "full_name")
                                     :username (payload "username")
                                     :team_trainer false
                                     :phone (payload "phone")
                                     :position (payload "position")
                                     :avatar_url (payload "avatarUrl")
                                     :enable (payload "enable")
                                     :type (payload "type")
                                     :city (payload "cityName")
                                     :job_title (payload "jobTitle")
                                     :former_job_title (payload "formerJobTitle")
                                     :former_place_of_employment (payload "formerPlaceOfEmployment")
                                     :place_of_employment (payload "placeOfEmployment")
                                     :start_date (payload "startDate")
                                     :start_date_year (payload "startDateYear")
                                     :end_date (payload "endDate")
                                     :end_date_year (payload "endDateYear")
                                     :location (payload "location")
                                     :occupation (payload "occupation")
                                     :industry (payload "industry")
                                     :seeking_job (payload "seekingJob")
                                     :degree (payload "degree")
                                     :field_of_study (payload "fieldOfStudy")
                                     :school_name (payload "schoolName")
                                     }))
