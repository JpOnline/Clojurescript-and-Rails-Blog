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

(defn create-post
  "It sends an http POST request to the server to create a post,
  it dispatches an event with the server response when it arrives."
  [post post-index callback-event]
  (go (let [response (<! (http/post "http://localhost:3000/posts"
                                   {:with-credentials? false
                                    :json-params post}))]
        ;; Delay the response to simulate a real server
        (js/setTimeout
          #(re-frame/dispatch
             [callback-event (:body response) post-index])
          5000))))

(defn update-post
  "It sends an http PUT request to the server to update a post."
  [post]
  (http/put (str "http://localhost:3000/posts/" (:id post))
            {:with-credentials? false
             :json-params post}))

(defn delete-post
  "It sends an http DELETE request to the server to delete a post."
  [post-id]
  (http/delete (str "http://localhost:3000/posts/" post-id)
            {:with-credentials? false}))
