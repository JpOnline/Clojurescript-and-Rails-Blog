(ns frontend.views.app-views
  (:require
    [reagent.core :as reagent]
    [re-frame.core :as re-frame]
    [material-ui :as material]
    ["material-ui/styles" :as material-styles]
    [material-ui-icons :as material-icons]
    [frontend.subs :as subs]
    [frontend.events :as events]
    [frontend.views.content-views :as content-views]
    ))

(declare app-view)
(declare top-bar)
(declare main-view)
(declare actions-menu)

(defn app []
  (let [top-bar-title (re-frame/subscribe [::subs/name])
        actions (re-frame/subscribe [::subs/actions])
        actions-open? (re-frame/subscribe [::subs/actions-open?])]
    [app-view
     [top-bar
      {:title @top-bar-title}]
     [main-view
      [content-views/content-view]]
     [actions-menu
      {:actions @actions
       :open? @actions-open?}]]))

(defn top-bar [{:keys [title]} & children]
  [:> material/AppBar
   {:position "static"
    :color "primary"}
   [:> material/Toolbar
    [:> material/Typography
     {:variant "h6"
      :color "inherit"}
     title]]
   (map-indexed #(with-meta %2 {:key %1}) children)])

(def custom-theme
  (material-styles/createMuiTheme
    #js {:palette
         #js {:primary
              #js {:light "#46484b",
                   :main "#1f2123",
                   :dark "#000000",
                   :contrastText "#fff"}
              :secondary
              #js {
                   :light "#ffea5b",
                   :main "#fcb823",
                   :dark "#c48800",
                   :contrastText "#000"}}}))

(defn app-view [& children]
  [:> material/MuiThemeProvider
   {:theme custom-theme}
   [:div.app-view
    {:style #js {:display "flex"
                 :justifyContent "space-between"
                 :flexDirection "column"
                 :height "inherit"
                 :width "inherit"}}
    (map-indexed #(with-meta %2 {:key %1}) children)]])

(defn main-view [& children]
  [:main
   {:style #js {:flexGrow 1
                :padding 12
                :overflow "auto"}}
   (map-indexed #(with-meta %2 {:key %1}) children)])

(defn actions-menu [{:keys [open? actions]}]
  (let [this (reagent/current-component)]
    [:> material/Fab
     {:color "secondary"
      :onClick #(re-frame/dispatch [::events/toggled-actions])
      :style #js {:alignSelf "flex-end" :margin "15px"}}
     [:> material-icons/MoreVert]
     [:> material/Popper
      {:open open?
       :style #js {:zIndex 1100}
       :anchorEl #(reagent/dom-node this)
       :placement "top-start"
       :modifiers #js {:offset #js {:offset "-80%p"}
                       :flip #js {:enabled false}
                       :preventOverflow #js {:enabled false}
                       :hide #js {:enabled false}}}
      [:> material/Paper
       {:elevation 8
        :style #js {:backgroundColor "#ffffffba"}}
       [:> material/MenuList
        (map (fn [action]
               (with-meta
                   [:> material/MenuItem
                    {:onClick #(re-frame/dispatch [(action :event)])}
                    (action :name)]
                 {:key (:name action)}))
             actions)]]]]))

