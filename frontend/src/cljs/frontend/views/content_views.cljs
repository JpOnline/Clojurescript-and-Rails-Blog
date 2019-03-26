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
(declare email-input-view)
(declare passcode-input-view)

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
      :email_input [email-input-view]
      :passcode_input [passcode-input-view]
      [:h2 "No content for this state" @state])))

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
  [:h2 {:style #js {:textAlign "center" :color "gray"}} "Loading posts..."])

(defn no-post []
  [:h3 {:style #js {:textAlign "center" :color "gray"}} "No post :("])

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
                   :height "40px"
                   :padding "0 8px"
                   :fontSize "2.125rem"
                   :margin "10px 0"
                   :color "inherit"}
       :value (post :title)
       :onChange on-title-change-fn
       :multiline true}]
     [:> material/Input
      {:style #js {:width "95%"
                   :padding "0 8px"
                   :border "1px solid #00000038"
                   :color "inherit"}
       :value (post :content)
       :onChange on-content-change-fn
       :multiline true
       :rowsMax "10"
       }]
     [:br] [:br] [:br]
     [post-view-mode {:post post}]]))

(declare confirmation-buttons)

(defn confirm-delete-post [{:keys [post]}]
  [:<>
   [:p
    "Are you sure you want to delete the post "
    [:span {:style #js {:fontWeight "bold"}}
     (post :title)]
    "?"]
   [confirmation-buttons
    {:yes-action {:name "Delete" :event [:deleted-post (post :id)]}
     :no-action {:name "I changed my mind" :event [:cancel]}}]])

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

(defn email-input-view []
  (let [email (or @(re-frame/subscribe [::subs/email-input]) "")]
    [:div.email-input-view
     {:style #js {:display "flex"
                  :flexDirection "column"
                  :maxWidth "315px"
                  :margin "auto"}}
     [:h1 {:style #js {:margin "15px 0 -5px 0"}}
      "Enter your email"]
     [:p {:style #js {:color "gray"}}
      "You'll receive a verification code."]
     [:> material/Input
      {:value email
       :onChange #(re-frame/dispatch
                    [::events/email-input-changed (-> % .-target .-value)])
       :placeholder "example@dominio.com"
       :style #js {:margin "15px 0"}}]
     [:> material/Fab
      {:color "secondary"
       :onClick #(re-frame/dispatch [::events/submit-email-clicked email])
       :style #js {:margin "12px 0"
                   :alignSelf "flex-end"}}
      [:> material-icons/ArrowForward]]]))

(defn passcode-input-view []
  (let [passcode (or @(re-frame/subscribe [::subs/passcode-input]) "")]
    [:div.passcode-input-view
     {:style #js {:display "flex"
                  :flexDirection "column"
                  :maxWidth "315px"
                  :margin "auto"}}
     [:h1 {:style #js {:margin "15px 0 -5px 0"}}
      "Verirication Code"]
     [:p {:style #js {:color "gray"}}
      "Enter the code sent by email."]
     [:> material/Input
      {:value passcode
       :onChange #(re-frame/dispatch
                    [::events/passcode-input-changed (-> % .-target .-value)])
       :placeholder "12345"
       :style #js {:margin "15px 0"}}]
     [:> material/Fab
      {:color "secondary"
       :onClick #(re-frame/dispatch [::events/submit-passcode-clicked passcode])
       :style #js {:margin "12px 0"
                   :alignSelf "flex-end"}}
      [:> material-icons/ArrowForward]]]))
