(ns com.webtalk.mailer
  (:gen-class)
  (:import (com.sendgrid SendGrid
                         SendGrid$Email
                         SendGrid$Response)))

(defn send-email [auth params]
  (let [{username :api_user password :api_key} auth
        to (params :to)
        from (params :from)
        subject (params :subject)
        html (params :html)
        sendgrid (SendGrid. username password)
        email (-> (SendGrid$Email.)
                  (.addTo to)
                  (.setFrom from)
                  (.setSubject subject)
                  (.setHtml html))]
    (println (.getMessage (.send sendgrid email)))))
