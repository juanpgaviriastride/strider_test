(ns com.webtalk.mailer
  (:gen-class)
  (:import (com.sendgrid SendGrid
                         SendGrid$Email
                         SendGrid$Response)))

(defn send-email
  [{username :api_user password :api_key} {to :to from :from subject :subject html :html}]
  (let [sendgrid (SendGrid. username password)
        email (-> (SendGrid$Email.)
                  (.addTo to)
                  (.setFrom from)
                  (.setSubject subject)
                  (.setHtml html))
        response (.send sendgrid email)]
    (.getMessage response)))
