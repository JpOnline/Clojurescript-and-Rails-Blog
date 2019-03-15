(ns frontend.subs
  (:require
    [re-frame.core :as re-frame]
    [frontend.events :as events]))

;; Please, be aware that db in re-frame's context means the state of the app.

(re-frame/reg-sub
 ::name
 (fn [db] (:name db)))

(re-frame/reg-sub
  ::posts
  (fn [db] (get-in db [:domain :posts])))

(re-frame/reg-sub
  ::loading?
  (fn [db] (get-in db [:ui :loading?])))

(re-frame/reg-sub
  ::actions-open?
  (fn [db] (get-in db [:ui :actions-open?])))

(re-frame/reg-sub
  ::state
  (fn [db] (get-in db [:ui :state])))

(defn actions
  [state]
  (case state
    :initial [{:name "Novo Post" :event :post-created}]
    :editing_post [{:name "Ok" :event :ok}
                   {:name "Excluir Post" :event :clicked-delete-post}]
    :post_detail [{:name "Editar" :event :editing-post}
                  {:name "Excluir Post" :event :clicked-delete-post}
                  {:name "Voltar" :event :went-back}]
    []))
(re-frame/reg-sub
  ::actions
  :<- [::state]
  actions)

(defn return-arrow?
  [state]
  (case state
    :initial false
    :post_detail true
    :editing_post true
    :delete_post_confirmation true
    false))
(re-frame/reg-sub
  ::return-arrow?
  :<- [::state]
  return-arrow?)

(re-frame/reg-sub
  ::selected-post-index
  (fn [db] (get-in db [:ui :selected-post-index])))

(re-frame/reg-sub
  ::selected-post
  :<- [::selected-post-index]
  :<- [::posts]
  (fn [[index posts]]
    (nth posts (or index 0) {})))

(defn top-bar-title
  [[state selected-post]]
  (case state
    :initial "Blog da SmartFit"
    :post_detail (selected-post :title)
    :editing_post (selected-post :title)
    :delete_post_confirmation "Tem Certeza?"
    "??"))
(re-frame/reg-sub
  ::top-bar-title
  :<- [::state]
  :<- [::selected-post]
  top-bar-title)

