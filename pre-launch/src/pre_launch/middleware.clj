(ns pre-launch.middleware
  (:require [pre-launch.layout :refer [*app-context* error-page]]
            [taoensso.timbre :as timbre]
            [environ.core :refer [env]]
            [ring.util.response :as response]
            [ring.middleware.flash :refer [wrap-flash]]
            [immutant.web.middleware :refer [wrap-session]]
            [ring.middleware.webjars :refer [wrap-webjars]]
            [ring.middleware.ssl :refer [wrap-hsts wrap-ssl-redirect wrap-forwarded-scheme]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [buddy.auth.middleware :refer [wrap-authentication]]
            [buddy.auth.backends.session :refer [session-backend]]
            [buddy.auth.accessrules :refer [restrict]]
            [buddy.auth :refer [authenticated?]]
            [pre-launch.layout :refer [*identity*]]
            [pre-launch.config :refer [defaults]])
  (:import [javax.servlet ServletContext]))

(defn wrap-context [handler]
  (fn [request]
    (binding [*app-context*
              (if-let [context (:servlet-context request)]
                ;; If we're not inside a servlet environment
                ;; (for example when using mock requests), then
                ;; .getContextPath might not exist
                (try (.getContextPath ^ServletContext context)
                     (catch IllegalArgumentException _ context))
                ;; if the context is not specified in the request
                ;; we check if one has been specified in the environment
                ;; instead
                (:app-context env))]
      (handler request))))

(defn wrap-internal-error [handler]
  (fn [req]
    (try
      (handler req)
      (catch Throwable t
        (timbre/error t)
        (error-page {:status 500
                     :title "Something very bad has happened!"
                     :message "We've dispatched a team of highly trained gnomes to take care of the problem."})))))

(defn wrap-csrf [handler]
  (wrap-anti-forgery
    handler
    {:error-response
     (error-page
       {:status 403
        :title "Invalid anti-forgery token"})}))

(defn wrap-formats [handler]
  (wrap-restful-format handler {:formats [:json-kw :transit-json :transit-msgpack]}))

(defn on-error [request response]
  (error-page
   {:status 403
    :title "You don’t have an active session. Please log in again to continue."
    :message (str "Access to " (:uri request) " is not authorized")}))

(defn wrap-restricted [handler]
  (restrict handler {:handler authenticated?
                     :on-error on-error}))

(defn wrap-identity [handler]
  (fn [request]
    (binding [*identity* (get-in request [:session :identity])]
      (handler request))))

(defn wrap-auth [handler]
  (-> handler
      wrap-identity
      (wrap-authentication (session-backend))))



(defn- https-url [request-url]
  (str "https://" (:server-name request-url) ":443" (:uri request-url)))

(defn require-https
  [handler]
  (fn [request]
    (if (and (= (:scheme request) :http) (re-find #"webtalk" (:server-name request)))
      (-> (response/redirect (https-url request))
         (response/status 301))
      (handler request))))

(defn enforce-ssl [handler]
  (if (or (env :dev) (env :test))
    handler
    (-> handler
        wrap-hsts
        ;;wrap-ssl-redirect
        require-https
        wrap-forwarded-scheme)))

(defn wrap-base [handler]
  (-> ((:middleware defaults) handler)
      wrap-auth
      enforce-ssl
      wrap-formats
      wrap-params
      wrap-webjars
      wrap-flash
      (wrap-session {:cookie-attrs {:http-only true
                                    :domain "webtalk.co"}})
      (wrap-defaults
        (-> site-defaults
            (assoc-in [:security :anti-forgery] false)
            (dissoc :session)))
      wrap-context
      wrap-internal-error))
