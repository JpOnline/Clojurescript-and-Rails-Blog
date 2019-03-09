(ns frontend.views
  (:require
    [re-frame.core :as re-frame]
    [material-ui :as material]
    [material-ui-icons :as material-icons]
    [frontend.subs :as subs]))

(declare app-view)
(declare top-bar)
(declare main-view)
(declare posts-view)

(defn app []
  (let [top-bar-title (re-frame/subscribe [::subs/name])
        loading? (re-frame/subscribe [::subs/loading?])
        posts (re-frame/subscribe [::subs/posts])]
    [app-view
     [top-bar
      {:title @top-bar-title}]
     [main-view
      [posts-view
       {:posts @posts
        :loading? @loading?}]]]))

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

(defn top-bar [{:keys [title]} & children]
  [:> material/AppBar
   {:position "static"
    :color "secondary"}
   [:> material/Toolbar
    [:> material/Typography
     {:variant "h6"
      :color "inherit"}
     title]]
   (map-indexed #(with-meta %2 {:key %1}) children)])

(defn app-view [& children]
  [:div.app-view
   {:style #js {:display "flex"
                :justifyContent "space-between"
                :flexDirection "column"
                :height "inherit"
                :width "inherit"}}
   (map-indexed #(with-meta %2 {:key %1}) children)])

(defn main-view [& children]
  [:main
   {:style #js {:flexGrow 1
                :padding 12
                :overflow "auto"}}
   (map-indexed #(with-meta %2 {:key %1}) children)])

(defn loading-posts []
  [:h2 {:style #js {:textAlign "center" :color "gray"}} "Carregando posts..."])

(defn no-post []
  [:h3 {:style #js {:textAlign "center" :color "gray"}} "Nenhum post :("])
