(ns emobot.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [clj-http.client :as client]
            [clojure.data.json :as json]
            [emobot.links :as links]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-body]]
            [ring.middleware.params :refer [wrap-params]]))

(def hook-url (System/getenv "SLACK_HOOK_URL"))

(def auth-token (System/getenv "SLACK_AUTH_TOKEN"))

(defn post-to-slack
  "Post given string to our hook"
  [url msg]
  (let [m (merge {:username "emobot"
           :icon_emoji ":feelsgood:"} msg)]
  (client/post url {:body (json/write-str m)
            :content-type :json})))

(defn link-to-slack
  "Post random link to Slack hook url"
  []
  (let [e (rand-nth links/links)]
    (post-to-slack hook-url {:text e})))

(defroutes app-routes
  (GET "/slack" {:keys [params] :as request}
        ;;(if (and (= "/slack" (:command params))
        ;;         (= auth-token (:token params)))
          {:status 200
           :content-type "text/plain"
           :body "hello, Morrissey!"})
          ;;{:status 400
           ;;:content-type "text/plain"
           ;;:body "Invalid params"})
  (route/not-found "Not Found"))

(def app
  (wrap-params (wrap-defaults app-routes {:security {:anti-forgery false}})))
