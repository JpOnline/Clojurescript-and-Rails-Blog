(ns frontend.server-talk.core
  "Here is where the talk with the server happens."
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
    [cljs-http.client :as http]
    [cljs.core.async :refer [<!]]
    [re-frame.core :as re-frame]))

(declare request-to-server)

(defn request-all-posts
  "It sends an http GET request to the server asking for all posts,
  it dispatches an event with the server answer when it arrives."
  [callback-event]
  (request-to-server {:http-verb http/get
                      :path "/posts"
                      :success-callback
                      (fn [response]
                        ;; Delay the response to simulate a real server
                        (js/setTimeout
                          #(re-frame/dispatch
                             [callback-event (:body response)])
                          5000))}))

(defn create-post
  "It sends an http POST request to the server to create a post,
  it dispatches an event with the server response when it arrives."
  [post post-index auth-token callback-event]
  (request-to-server {:http-verb http/post
                      :path "/posts"
                      :json-params post
                      :auth-token auth-token
                      :success-callback
                      (fn [response]
                        ;; Delay the response to simulate a real server
                        (js/setTimeout
                          #(re-frame/dispatch
                             [callback-event (:body response) post-index])
                          5000))}))

(defn update-post
  "It sends an http PUT request to the server to update a post."
  [post auth-token callback-event]
  (request-to-server {:http-verb http/put
                      :path (str "/posts/" (:id post))
                      :json-params post
                      :auth-token auth-token
                      :success-callback #(re-frame/dispatch [callback-event])}))

(defn delete-post
  "It sends an http DELETE request to the server to delete a post."
  [post-id auth-token callback-event]
  (request-to-server {:http-verb http/delete
                      :path (str "/posts/" post-id)
                      :auth-token auth-token
                      :success-callback #(re-frame/dispatch [callback-event])}))

(defn new-passcode
  "It sends a request to the server to generate a new passcode for the given email
  and send this new passcode by email to the user."
  [email callback-event]
  (request-to-server {:http-verb http/post
                      :path "/auth/new_passcode"
                      :json-params {:email email}
                      :success-callback
                      #(re-frame/dispatch [callback-event email])}))

(defn authenticate
  "It sends a request to the server to get an authentication token, with it the
  user can be authenticated for other requests without providing a new passcode."
  [email passcode callback-event]
  (request-to-server {:http-verb http/post
                      :path "/auth/login"
                      :json-params {:email email :passcode passcode}
                      :success-callback
                      (fn [response]
                        (re-frame/dispatch [callback-event (:body response)]))}))

(defn request-to-server
  [{:keys [http-verb path json-params auth-token success-callback]}]
  (go (let [error-callback #(re-frame/dispatch
                              [:frontend.events/received-error-from-server
                               (-> % :body :message)])
            response (<! (http-verb
                           (str "http://localhost:3000" path)
                           (merge {:with-credentials? false}
                                  (when auth-token {:headers {"Authorization" auth-token}})
                                  (when json-params {:json-params json-params}))))]
        (if (:success response)
          (success-callback response)
          (error-callback response)))))
