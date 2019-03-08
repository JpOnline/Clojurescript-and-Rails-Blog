(ns frontend.server-talk.core
  "Here is where the talk with the server happens."
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
    [cljs-http.client :as http]
    [cljs.core.async :refer [<!]]
    [re-frame.core :as re-frame]))

(defn request-all-posts
  "It sends an http GET request to the server asking for all posts,
  it dispatches an event with the server answer when it arrives."
  [callback-event]
  (go (let [response (<! (http/get "http://localhost:3000/posts"
                                   {:with-credentials? false}))]
        ;; Delay the response to simulate a real server
        (js/setTimeout
          #(re-frame/dispatch
             [callback-event (:body response)])
          5000))))
