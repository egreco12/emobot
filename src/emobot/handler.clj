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
            [ring.middleware.params :refer [wrap-params]]
            [clojure.core.async :refer [thread]]))

(def hook-url (System/getenv "SLACK_HOOK_URL"))

(def auth-token (System/getenv "SLACK_AUTH_TOKEN"))

(defn post-to-slack
  "Post given string to our hook"
  [url msg]
  (let [m (merge {:username "emobot"
           :icon_emoji ":feelsgood:"} msg)]
  (println "m: " m)
  (client/post url {:body (json/write-str m)
            :content-type :json})))

(defn vid-to-slack
  "Post a random video to slack"
  [url]
  (post-to-slack url {:text (random-link)}))

(defn random-link
  "Grab a random youtube link"
  []
  (rand-nth links/links))

(def good-response
  {:status 200
   :content-type "text/plain"
   :body "You made me sad..."})

(def bad-response
  {:status 400
   :content-type "text/plain"
   :body "Invalid params"})

(defn build-response
  "Given text, build a slash command response object"
  [text status]
  (json/write-str 
    {:text text
     :response_type "in_channel"
     :status 200
     :content-type "application/json"}))

(defroutes app-routes
  (GET "/slack" {:keys [params] :as request}
    (let [p (walk/keywordize-keys params)]
        (if (and (= "/emobot" (:command p))
                 (= auth-token (:token p)))
        (do 
          (thread (vid-to-slack (:response_url p)))
          good-response)
        bad-response)))
  (route/not-found "Not Found"))

(def app
  (wrap-params (wrap-defaults app-routes {:security {:anti-forgery false}})))
