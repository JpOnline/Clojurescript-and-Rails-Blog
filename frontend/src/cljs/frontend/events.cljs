(ns frontend.events
  (:require
    [re-frame.core :as re-frame]
    [frontend.db :as db]
    [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
    [frontend.server-talk.core :as server-talk]))

(declare app-initialized-handler)

;; Request posts from the server and sets a default app state.
(re-frame/reg-event-db
  ::app-initialized
  (fn-traced
    [_ _]
    (server-talk/request-all-posts ::server-answered-all-posts)
    (app-initialized-handler)))

(defn app-initialized-handler []
  (-> db/default-db
      (assoc-in [:ui :loading?] true)))

(declare server-answered-all-posts-handler)

;; Updates app state with posts and turn-off loading status.
(re-frame/reg-event-db
  ::server-answered-all-posts
  (fn-traced
    [db [_ posts]]
    (server-answered-all-posts-handler db posts)))

(defn server-answered-all-posts-handler [db posts]
  (-> db
      (assoc-in [:domain :posts] posts)
      (assoc-in [:ui :loading?] false)))

(defn-traced toggled-actions-handler [db]
  (update-in db [:ui :actions-open?] not))
(re-frame/reg-event-db
  ::toggled-actions
  toggled-actions-handler)

(defn get-post-index [db id]
  (first (keep-indexed #(when (= id (:id %2)) %1) (get-in db [:domain :posts]))))

(defn-traced post-content-changed-handler [db [_ id new-content]]
  (assoc-in db [:domain :posts (get-post-index db id) :content] new-content))
(re-frame/reg-event-db
  ::post-content-changed
  post-content-changed-handler)

(defn current->next-state
  [state-machine current-state transition]
  (get-in state-machine [current-state transition]))

(defn-traced next-state
  [db [event]]
  (if-let [new-state (current->next-state db/initial-state-machine
                                          (get-in db [:ui :state])
                                          event)]
    (assoc-in db [:ui :state] new-state)
    db))

(re-frame/reg-event-db :post-created next-state)
