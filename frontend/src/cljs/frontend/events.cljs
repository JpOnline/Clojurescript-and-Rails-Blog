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

(declare create-new-post-local)
(declare create-new-post-on-server)
(declare next-state)

(re-frame/reg-event-db
  :post-created
  (fn-traced
    [db ev]
    (-> db
        create-new-post-local
        create-new-post-on-server
        (next-state ev))))

(defn create-new-post-local [db]
  (update-in db [:domain :posts] conj {:title "Título" :content "## Conteúdo"}))

(defn create-new-post-on-server [db]
  (let [posts (get-in db [:domain :posts])]
    (server-talk/create-post
      (last posts)
      (dec (count posts))
      ::post-created-on-server)
    db))

(re-frame/reg-event-db
  ::post-created-on-server
  (fn-traced
    [db [_ post post-index]]
    (assoc-in db [:domain :posts post-index :id] (:id post))))

(defn get-post [db id]
  (first (keep #(when (= id (:id %)) %) (get-in db [:domain :posts]))) )

(defn-traced send-update-to-server-handler
    [db [_ id]]
    (when-not (get-in db [:server :should-wait-to-update?])
      (server-talk/update-post (get-post db id)))
    db)
(re-frame/reg-event-db
  ::send-update-to-server
  send-update-to-server-handler)

(defn set-timeout-for-next-update
  "It set the :should-wait-to-update? flag in app-state, so the update doesn't
  happen in every keystroke, only every 5 seconds."
  [db id]
  (if-not (get-in db [:server :should-wait-to-update?])
    (do
      (js/setTimeout
        (fn []
          (re-frame/dispatch [::clear-update-timeout])
          (re-frame/dispatch [::send-update-to-server id]))
        5000)
      (assoc-in db [:server :should-wait-to-update?] true))
    db))

(re-frame/reg-event-db
  ::clear-update-timeout
  (fn-traced
    [db]
    (assoc-in db [:server :should-wait-to-update?] false)))

(defn get-post-index [db id]
  (first (keep-indexed #(when (= id (:id %2)) %1) (get-in db [:domain :posts]))))

(defn-traced post-changed-handler
  [db [event id payload]]
  (let [post-index (get-post-index db id)
        title-or-content ({::post-title-changed :title
                           ::post-content-changed :content} event)]
    (-> db
        (assoc-in [:domain :posts post-index title-or-content] payload)
        (send-update-to-server-handler [event id])
        (set-timeout-for-next-update id))))

(re-frame/reg-event-db
  ::post-title-changed
  post-changed-handler)

(re-frame/reg-event-db
  ::post-content-changed
  post-changed-handler)

(defn deleted-post-handler [db id]
  (let [index (get-post-index db id)
        posts (get-in db [:domain :posts])
        updated-posts (vec (concat (subvec posts 0 index)
                                   (subvec posts (inc index))))]
    (assoc-in db [:domain :posts] updated-posts)))

(re-frame/reg-event-db
  :deleted-post
  (fn-traced
    [db [event id]]
    (server-talk/delete-post id)
    (-> db
        (deleted-post-handler id)
        (next-state [event]))))

(defn-traced clicked-post-handler
  [db [event post-id]]
  (-> db
      (assoc-in [:ui :selected-post-id] post-id)
      (next-state [event])))

(defn-traced toggled-actions-handler [db]
  (update-in db [:ui :actions-open?] not))
(re-frame/reg-event-db
  ::toggled-actions
  toggled-actions-handler)

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

(re-frame/reg-event-db :went-back next-state)
(re-frame/reg-event-db :clicked-post clicked-post-handler)
(re-frame/reg-event-db :editing-post next-state)
(re-frame/reg-event-db :clicked-delete-post next-state)
(re-frame/reg-event-db :cancel next-state)
