(ns clj-auth-sample.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.adapter.jetty :as ring]
            [ring.middleware.json :as middleware]
            [ring.middleware.session :as sessions]
            [ring.util.response :refer [resource-response response]]
            [compojure.handler :as handler]
            [cheshire.core :refer :all]
            [somnium.congomongo :as cm]
            [cemerick.friend :as friend]
            [friend-oauth2.workflow :as oauth2]
            [friend-oauth2.util :refer [format-config-uri]]
            (cemerick.friend [workflows :as workflows]
                             [credentials :as creds])
            [environ.core :refer [env]]
            [clj-http.client :as http-client]))

(def google-id
  (env :google-id))

(def google-secret
  (env :google-secret))

(def oauth-domain
  (env :oauth-domain))

(def oauth-callback
  (env :oauth-callback))

(def google-api-url "https://www.googleapis.com/plus/v1/people/me/openIdConnect")

(defn google-profile [access-token]
  (http-client/get google-api-url {:headers {"Authorization" (str "Bearer " access-token)}}))

(def config-auth {:roles #{::user}})

(defn credential-fn [token]
  {:identity token :roles #{::user}})

(def client-config
  {:client-id google-id
   :client-secret google-secret
   :callback {:domain oauth-domain :path oauth-callback}})

(defn user-access-token [request]
  (get-in (friend/current-authentication request) [:identity :access-token]))

(def uri-config
  {:authentication-uri {:url "https://accounts.google.com/o/oauth2/auth"
                       :query {:client_id (:client-id client-config)
                               :response_type "code"
                               :redirect_uri (format-config-uri client-config)
                               :scope "email"}}

   :access-token-uri {:url "https://accounts.google.com/o/oauth2/token"
                      :query {:client_id (:client-id client-config)
                              :client_secret (:client-secret client-config)
                              :grant_type "authorization_code"
                              :redirect_uri (format-config-uri client-config)}}})


(defn auth-middleware [handler]
  (fn [request]
    (println (str "\n\nRequest in auth middleware: " request))
    (handler request)))

(defroutes app-routes
  (GET "/" [] (resource-response "index.html" {:root "public"}))
  (GET "/secured" request
    (friend/authorize #{::user}
      (do
        (println (str "\n\nRequest inside secured route: " request))
        (resource-response "secured.html" {:root "public"}))))
  (route/resources "/"))

(def app
  (sessions/wrap-session
  (routes
  (-> app-routes
    auth-middleware
    (friend/authenticate
       {:allow-anon? true
          :workflows [(oauth2/workflow
                   {:client-config client-config
                    :uri-config uri-config
                    :credential-fn credential-fn})]})
      handler/site
       )
      )))

(defn -main []
  (ring/run-jetty #'app {:port 3000 :join? false}))
