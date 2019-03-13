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

(defn send-update-to-server [db [_ id]]
  (when-not (get-in db [:server :should-wait-to-update?])
    (.log js/console "Agora enviando pro server")
    (js/setTimeout
      (fn []
        (.log js/console "Tipo enviando pro server")
        (re-frame/dispatch [::clear-update-timeout]))
      5000))
  db)

(re-frame/reg-event-db
  ::clear-update-timeout
  (fn-traced
    [db]
    (assoc-in db [:server :should-wait-to-update?] false)))

(defn set-timeout-for-next-change [db]
  (assoc-in db [:server :should-wait-to-update?] true))

(defn get-post-index [db id]
  (first (keep-indexed #(when (= id (:id %2)) %1) (get-in db [:domain :posts]))))

(defn-traced post-title-changed-handler [db [_ id new-title]]
  (assoc-in db [:domain :posts (get-post-index db id) :title] new-title))
(re-frame/reg-event-db
  ::post-title-changed
  (fn-traced
    [db ev]
    (-> db
        (post-title-changed-handler ev)
        (send-update-to-server ev)
        (set-timeout-for-next-change))))

(defn-traced post-content-changed-handler [db [_ id new-content]]
  (assoc-in db [:domain :posts (get-post-index db id) :content] new-content))
(re-frame/reg-event-db
  ::post-content-changed
  post-content-changed-handler)

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

(re-frame/reg-event-db
  :post-created
  (fn-traced
    [db ev]
    (-> db
        create-new-post-local
        create-new-post-on-server
        (next-state ev))))

(defn-traced clicked-post-handler
  [db [event post-id]]
  (-> db
      (assoc-in [:ui :selected-post-id] post-id)
      (next-state [event])))

(re-frame/reg-event-db :went-back next-state)
(re-frame/reg-event-db :clicked-post clicked-post-handler)
(re-frame/reg-event-db :editing-post next-state)
