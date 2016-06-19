(ns emobot.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [clj-http.client :as client]
            [clojure.data.json :as json]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(def hook-url (System/getenv "SLACK_HOOK_URL"))

(defn post-to-slack
	"Post given string to our hook"
	[url msg]
	(let [m (merge {:username "emobot"
				   :icon_emoji ":feelsgood:"} msg)]
	(client/post url {:body (json/write-str m)
					  :content-type :json})))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
