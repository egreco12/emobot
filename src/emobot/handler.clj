(ns emobot.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [clj-http.client :as client]
            [clojure.data.json :as json]
            [clojure.walk :as walk]
            [clojure.pprint :refer [pprint]]
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

(defn random-link
  "Grab a random youtube link"
  []
  (rand-nth links/links))

(defroutes app-routes
  (GET "/slack" {:keys [params] :as request}
    (let [p (walk/keywordize-keys params)]
        (if (and (= "/emobot" (:command p))
                 (= auth-token (:token p)))
          {:status 200
           :content-type "text/plain"
           :body (random-link)}
          {:status 400
           :content-type "text/plain"
           :body "Invalid params"})))
  (route/not-found "Not Found"))

(def app
  (wrap-params (wrap-defaults app-routes {:security {:anti-forgery false}})))
