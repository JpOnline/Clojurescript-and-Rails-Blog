(ns frontend.views.app-views
  (:require
    [reagent.core :as reagent]
    [re-frame.core :as re-frame]
    [material-ui :as material]
    ["material-ui/styles" :as material-styles]
    [material-ui-icons :as material-icons]
    [frontend.subs :as subs]
    [frontend.events :as events]
    [frontend.views.content-views :as content-views]))

(declare app-view)
(declare top-bar)
(declare main-view)
(declare actions-menu)
(declare error-message)

(defn app []
  (let [top-bar-title (re-frame/subscribe [::subs/top-bar-title])
        actions (re-frame/subscribe [::subs/actions])
        actions-open? (re-frame/subscribe [::subs/actions-open?])
        loading? (re-frame/subscribe [::subs/loading?])
        error-message-str (re-frame/subscribe [::subs/error-message])
        return-arrow? (re-frame/subscribe [::subs/return-arrow?])]
    [app-view
     [top-bar
      {:title @top-bar-title
       :return-arrow? @return-arrow?
       :loading? @loading?}]
     [main-view
      (when @error-message-str [error-message @error-message-str])
      [content-views/content-view]]
     [actions-menu
      {:actions @actions
       :open? @actions-open?}]]))

(defn top-bar [{:keys [title return-arrow? loading?]} & children]
  [:> material/AppBar
   {:position "static"
    :color "primary"}
   [:> material/Toolbar
    {:style #js {:alignItems "center"
                 :justifyContent "space-between"}}
    [:div.arrow-and-title
     {:style #js {:display "flex"
                  :alignItems "center"}}
     (if return-arrow?
       [:> material/IconButton
        {:color "inherit"
         :onClick #(re-frame/dispatch [:went-back])
         :style #js {:marginLeft -12 :marginRight 20}}
        [:> material-icons/ArrowBack]]
       [:div.arrow-back-placeholder
        {:style #js {:width "56px"}}])
     [:> material/Typography
      {:variant "h6"
       :color "inherit"}
      title]]
    (when loading?
      [:> material/CircularProgress
       {:color "secondary"}])]
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
              #js {:light "#ffea5b",
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
    (when-not (empty? actions)
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
               actions)]]]])))

(defn error-message [& children]
  [:> material/Paper
   {:style #js {:display "flex"
                :flexDirection "row"
                :justifyContent "space-between"}}
   [:div.error-message {:style #js {:color "red"
                                    :padding "5px 15px 15px 15px"}}
    [:h3 "Problems talking to the cloud"]
    (map-indexed (fn [i c] ^{:key i} c) children)]
   [:> material/IconButton
    {:style #js {:alignSelf "flex-start"
                 :marginTop "25px"}
     :onClick #(re-frame/dispatch [::events/clean-error-message])}
    [:> material-icons/Close]]])
