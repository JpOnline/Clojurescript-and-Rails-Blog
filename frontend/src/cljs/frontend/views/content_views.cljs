(ns frontend.views.content-views
  (:require
    [re-frame.core :as re-frame]
    [material-ui :as material]
    [material-ui-icons :as material-icons]
    [frontend.subs :as subs]
    [markdown.core :refer [md->html]]
    [frontend.events :as events]
    ))

(declare posts-view)
(declare editing-post-view)

(defn content-view []
  (let [state (re-frame/subscribe [::subs/state])
        loading? (re-frame/subscribe [::subs/loading?])
        posts (re-frame/subscribe [::subs/posts])]
    (case @state
      :initial [posts-view
                {:posts @posts
                 :loading? @loading?}]
      :editing_post [editing-post-view {:post (first @posts)}]
      [:h2 "no matching"])))

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
    :style #js {:width "100%" :maxWidth 360}}
   (map #(with-meta
           [:> material/ListItem
            {:button true}
            [:> material/ListItemText
             {:primary (:title %)}]]
           {:key (:id %)})
        posts)])

(defn loading-posts []
  [:h2 {:style #js {:textAlign "center" :color "gray"}} "Carregando posts..."])

(defn no-post []
  [:h3 {:style #js {:textAlign "center" :color "gray"}} "Nenhum post :("])

(defn editing-post-view [{:keys [post onchange-fn]}]
  (let [default-onchange-fn #(re-frame/dispatch [::events/post-content-changed
                                                 1
                                                 (-> % .-target .-value)])]
    [:<>
     [:> material/Input
      {:style #js {:width "95%"
                   :padding "0 8px"
                   :border "1px solid #00000038"
                   :color "inherit"}
       :value (post :content)
       :onChange (or onchange-fn default-onchange-fn)
       :multiline true}]
     [:div
      {:style #js {:overflow "auto"}
       :dangerouslySetInnerHTML
       #js {:__html (md->html (post :content))}}]]))
