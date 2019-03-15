(ns frontend.views.content-views
  (:require
    [re-frame.core :as re-frame]
    [material-ui :as material]
    [material-ui-icons :as material-icons]
    [frontend.subs :as subs]
    [markdown.core :refer [md->html]]
    [frontend.events :as events]))

(declare posts-view)
(declare post-view-mode)
(declare editing-post-view)
(declare confirm-delete-post)

(defn content-view []
  (let [state (re-frame/subscribe [::subs/state])
        loading? (re-frame/subscribe [::subs/loading?])
        selected-post (re-frame/subscribe [::subs/selected-post])
        posts (re-frame/subscribe [::subs/posts])]
    (case @state
      :initial [posts-view
                {:posts @posts
                 :loading? @loading?}]
      :post_detail [post-view-mode {:post @selected-post}]
      :editing_post [editing-post-view {:post @selected-post}]
      :delete_post_confirmation [confirm-delete-post {:post @selected-post}]
      [:h2 "Sem conteúdo pro estado " @state])))

(declare posts-list)
(declare no-post)
(declare loading-posts)

(defn posts-view [{:keys [posts loading?]}]
  (cond
    loading? [loading-posts]
    (empty? posts) [no-post]
    :default [posts-list {:posts posts}]))

(defn posts-list [{:keys [posts]}]
  [:> material/List
   {:component "nav"
    :style #js {:width "100%" :maxWidth "100%"}}
   (map #(with-meta
           [:> material/ListItem
            {:button true
             :onClick (fn [e] (re-frame/dispatch [:clicked-post (:id %)]))}
            [:> material/ListItemText
             {:primary (:title %)}]]
           {:key (:id %)})
        posts)])

(defn loading-posts []
  [:h2 {:style #js {:textAlign "center" :color "gray"}} "Carregando posts..."])

(defn no-post []
  [:h3 {:style #js {:textAlign "center" :color "gray"}} "Nenhum post :("])

(defn post-view-mode [{:keys [post]}]
  [:<>
   [:p {:style #js {:color "gray"}}
    (str "Autor: "(post :submited_by))
    [:br]
    (let [date (re-find #"(\d+)-(\d+)-(\d+)T(\d+):(\d+)" (post :updated_at))
          [_ year month day hour minute] date]
      (str day "/" month "/" year " " hour ":" minute))]
   [:div
   {:style #js {:overflow "auto"}
    :dangerouslySetInnerHTML
    #js {:__html (md->html (post :content))}}]])

(defn editing-post-view
  [{:keys [post selected-post-id opt-on-title-change-fn opt-on-content-change-fn]}]
  (let [on-content-change-fn (or opt-on-content-change-fn
                                 #(re-frame/dispatch
                                    [::events/post-content-changed
                                     (post :id)
                                     (-> % .-target .-value)]))
        on-title-change-fn (or opt-on-title-change-fn
                                 #(re-frame/dispatch
                                    [::events/post-title-changed
                                     (post :id)
                                     (-> % .-target .-value)]))]
    [:<>
     [:> material/Input
      {:style #js {:width "95%"
                   :padding "0 8px"
                   :fontSize "2.125rem"
                   :margin "10px 0"
                   :color "inherit"}
       :value (post :title)
       :onChange on-title-change-fn
       :multiline true}][:> material/Input
      {:style #js {:width "95%"
                   :padding "0 8px"
                   :border "1px solid #00000038"
                   :color "inherit"}
       :value (post :content)
       :onChange on-content-change-fn
       :multiline true}]
     [:br] [:br] [:br]
     [post-view-mode {:post post}]]))

(declare confirmation-buttons)

(defn confirm-delete-post [{:keys [post]}]
  [:<>
   [:p
    "Tem certeza que quer excluir o post "
    [:span {:style #js {:fontWeight "bold"}}
     (post :title)]
    "?"]
   [confirmation-buttons
    {:yes-action {:name "Excluir" :event [:deleted-post (post :id)]}
     :no-action {:name "Mudei de ideia" :event [:cancel]}}]])

(defn confirmation-buttons [{:keys [yes-action no-action]}]
  [:div.confirmation-buttons-container
   {:style #js {:display "flex"
                :flexFlow "column"
                :alignItems "stretch"
                :padding "12px"}}
   [:div.buttons-countainer
    {:style #js {:alignSelf "flex-end"
                 :padding "20px 0"}}
    [:> material/Button
     {:variant "contained"
      :size "small"
      :color "secondary"
      :onClick #(re-frame/dispatch (yes-action :event))
      :style #js {:marginRight "4px"}}
     (yes-action :name)]
    [:> material/Button
     {:variant "outlined"
      :size "small"
      :color "secondary"
      :onClick #(re-frame/dispatch (no-action :event))
      :style #js {:marginLeft "4px"
                  :marginRight "8px"}}
     (no-action :name)]]])
