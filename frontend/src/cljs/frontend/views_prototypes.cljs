(ns frontend.views-prototypes
  (:require
    [reagent.core :as reagent]
    [devcards.core :as devcards :refer-macros [defcard deftest defcard-rg]]
    [material-ui :as material]
    [material-ui-icons :as material-icons]
    [frontend.views.app-views :as app-views]
    [frontend.views.content-views :as content-views]
    [fsmviz.core :as viz-state-machine]
    [frontend.subs :as subs]
    [frontend.db :as db]))

(defonce devcards-hidden (reagent/atom []))

;; Hidding Cards
(defcard-rg hidding-cards
  (let [card-container-style #js {:display "flex"
                                  :justifyContent "space-evenly"
                                  :padding "20px 0"}]
    (fn [devcard-data _]
      [:div.card-container
       {:style card-container-style}
       [:> material/Button
        {:variant "outlined"
         :size "small"
         :color "secondary"
         :onClick #(doseq [devcard-hidden? @devcards-hidden]
                     (reset! devcard-hidden? true))}
        "hide all"]
       [:> material/Button
        {:variant "outlined"
         :size "small"
         :color "secondary"
         :onClick #(doseq [devcard-hidden? @devcards-hidden]
                     (reset! devcard-hidden? false))}
        "show all"]]))
  {}
  {:frame false})

(declare card-container)

(defcard-rg initial-state
  (fn [devcard-data _]
    [card-container
     @devcard-data
     [app-views/app-view
      [app-views/top-bar
       @devcard-data]
      [app-views/main-view
       [content-views/posts-view
        @devcard-data]]]])
  {:hidden? (reagent/atom true)
   :title "Blog"
   :posts [{:id 1 :title "A test post 1" :content "# Content"}]
   :loading? false})

(defcard-rg loading
  (fn [hidden? _]
    [card-container
     {:hidden? hidden?}
     [app-views/app-view
      [app-views/top-bar
       {:title "Blog"}]
      [app-views/main-view
       [content-views/posts-view
        {:posts [{:id 1 :title "A test post 2" :content "# Content"}]
         :loading? true}]]
      [app-views/actions-menu
       {:actions [{:name "Novo Post"}]
       :open? true}]]])
  (reagent/atom true))

(defcard-rg editing-post*
  (fn [devcard-data _]
    [card-container
     @devcard-data
     [app-views/app-view
      [app-views/top-bar
       {:title "Blog"
        :return-arrow? true}]
      [app-views/main-view
       [content-views/editing-post-view
        {:post {:id 1 :title @(:title @devcard-data) :content @(:content @devcard-data) :updated_at "2019-06-01T20:06:21.353Z"}
         :opt-on-title-change-fn #(reset! (:title @devcard-data) (-> % .-target .-value))
         :opt-on-content-change-fn #(reset! (:content @devcard-data) (-> % .-target .-value))}]]
      [app-views/actions-menu
       {:actions [{:name "Novo Post"}]
       :open? true}]]])
  {:hidden? (reagent/atom true)
   :content (reagent/atom "# Content")
   :title (reagent/atom "A test post")})

(defcard-rg reading-post
  (fn [devcard-data _]
    (let [ui-state :post_detail]
      [card-container
       @devcard-data
       [app-views/app-view
        [app-views/top-bar
         {:title "Blog"
          :return-arrow? (subs/return-arrow? ui-state)}]
        [app-views/main-view
         [content-views/post-view-mode
          {:post {:id 1
                  :title "A test post 4"
                  :content @(:content @devcard-data)}}]]
        [app-views/actions-menu
         {:actions (subs/actions ui-state)
          :open? true}]]]))
  {:hidden? (reagent/atom true) :content (reagent/atom "# Content")})

(defcard initial-state-machine-doc
  (str "## Máquinas de Estado Finito

       Definir máquinas de estado ajuda a manter a consistência do estado de diferentes
       componentes quando mais de um componente é atualizado num evento. A ideia veio
       do Jeb Beich e é descrita em 2 posts dele.

       - [Restate your ui using state machines](http://blog.cognitect.com/blog/2017/5/22/restate-your-ui-using-state-machines-to-simplify-user-interface-development)
       - [Restate your ui using state machines and re-frame](http://blog.cognitect.com/blog/2017/8/14/restate-your-ui-creating-a-user-interface-with-re-frame-and-state-machines)")
  {}
  {:frame false
   :heading false})

(defonce initial-state-machine
  (viz-state-machine/generate-image
    db/initial-state-machine
    "fsm"))

(declare card-expander)

(defcard-rg initial-state-machine
  (fn [hidden? _]
    (swap! devcards-hidden conj hidden?)
    [:<>
     [card-expander
      {:hidden? hidden?}]
     [:div
      {:style #js {:overflow "auto"}
       :hidden @hidden?
       :dangerouslySetInnerHTML
       #js {:__html initial-state-machine}}]])
  (reagent/atom true))

(declare app-container)

(defn card-container [{:keys [hidden?]} & children]
  (swap! devcards-hidden conj hidden?)
    [:<>
     [card-expander {:hidden? hidden?}]
     [app-container
      {:hidden? hidden?}
      (map-indexed #(with-meta %2 {:key %1}) children)]])

(defn card-expander [{:keys [hidden?]}]
  [:div.card-expander
   {:onClick #(swap! hidden? not)
    :style #js {:textAlign "center"}}
   (if @hidden?
     [:> material-icons/ExpandMore]
     [:> material-icons/ExpandLess])])

(defn app-container [{:keys [hidden?]} & children]
  [:div.app-container
   {:style #js {:display "flex"
                :justifyContent "space-evenly"
                :paddingBottom 20}}
   [:div.component-container
    {:hidden @hidden?
     :style #js {:width 360 :height 640
                 :border "1px solid #00000038"}}
    (map-indexed #(with-meta %2 {:key %1}) children)]])
