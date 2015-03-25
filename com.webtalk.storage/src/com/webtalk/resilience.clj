(ns com.webtalk.resilience
  (:import (com.netflix.hystrix.contrib.metrics.eventstream HystrixMetricsStreamServlet)
           (org.eclipse.jetty.server Server Request)
           (org.eclipse.jetty.server.handler AbstractHandler)
           (org.eclipse.jetty.server.nio SelectChannelConnector)
           (org.eclipse.jetty.server.ssl SslSelectChannelConnector)
           (org.eclipse.jetty.server.bio SocketConnector)
           (org.eclipse.jetty.server.ssl SslSocketConnector)
           (org.eclipse.jetty.servlet ServletContextHandler ServletHolder)
           (org.eclipse.jetty.util.thread QueuedThreadPool)
           (org.eclipse.jetty.util.ssl SslContextFactory)
           (javax.servlet.http HttpServletRequest HttpServletResponse))
  (:require
   [compojure.core     :refer :all]
   [ring.adapter.jetty :as jetty]
   [ring.util.servlet  :as servlet]))

(defn run-jetty-with-hystrix-stream [app options]
  (let [^Server server (#'jetty/create-server (dissoc options :configurator))
        ^QueuedThreadPool pool (QueuedThreadPool. ^Integer (options :max-threads 50))]
    (when (:daemon? options false) (.setDaemon pool true))
    (doto server (.setThreadPool pool))
    (when-let [configurator (:configurator options)]
      (configurator server))
    (let [hystrix-holder  (ServletHolder. HystrixMetricsStreamServlet)
          app-holder (ServletHolder. (servlet/servlet app))
          context (ServletContextHandler. server "/" ServletContextHandler/SESSIONS)]
      (.addServlet context hystrix-holder "/hystrix.stream")
      (.addServlet context app-holder "/"))
    (.start server)
    (when (:join? options true) (.join server))
    server))

(defroutes app (GET "/hello" [] {:status 200 :body "Hello"}))

  
(defn -main
  [port]
  (run-jetty-with-hystrix-stream app {:port (Integer. port) :join? false}))
