(ns frontend.views-prototypes
  (:require
    [reagent.core :as reagent]
    [devcards.core :as devcards :refer-macros [defcard deftest defcard-rg]]
    [material-ui :as material]
    [material-ui-icons :as material-icons]
    [frontend.views :as v]
    [fsmviz.core :as viz-state-machine]
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

(defcard-rg f*
  (fn [devcard-data _]
    [card-container
     @devcard-data
     [v/app-view
      [v/top-bar
       @devcard-data]
      [v/main-view
       [v/posts-view
        @devcard-data]]]])
  {:hidden? (reagent/atom true)
   :title "Blog"
   :posts [{:id 1 :title "A test post" :content "# Content"}]
   :loading? false})

(defcard-rg loading
  (fn [hidden? _]
    [card-container
     {:hidden? hidden?}
     [v/app-view
      [v/top-bar
       {:title "Blog"}]
      [v/main-view
       [v/posts-view
        {:posts [{:id 1 :title "A test post" :content "# Content"}]
         :loading? true}]]
      [v/actions-menu
       {:actions [{:name "Novo Post"}]
       :open? true}]]])
  (reagent/atom true))

(defcard initial-state-machine-doc
  (str "## Máquinas de Estado Finito

  Definir máquinas de estado ajuda a manter a consistência do estado de diferentes
  componentes quando mais de um componente é atualizado num evento. A ideia veio
  do Jeb Beich e é descrita em 2 posts dele.

  - [Restate your ui using state machines](http://blog.cognitect.com/blog/2017/5/22/restate-your-ui-using-state-machines-to-simplify-user-interface-development)
  - [Restate your ui using state machines and re-frame](http://blog.cognitect.com/blog/2017/8/14/restate-your-ui-creating-a-user-interface-with-re-frame-and-state-machines)")
  {}
  {:frame false
   :heading false })

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
