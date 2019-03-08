(ns frontend.views
  (:require
   [re-frame.core :as re-frame]
   [frontend.subs :as subs]))

(declare posts-list)

(defn main-panel []
  (let [loading (re-frame/subscribe [::subs/loading])]
    (if @loading
      [:div [:h1 "Carregando posts..."]]
      [posts-list])))

(defn posts-list []
  (let [name (re-frame/subscribe [::subs/name])
        posts (re-frame/subscribe [::subs/posts])]
    [:div
     [:h1 "Hello from " @name]
     [:ul
      (map #(with-meta
              [:li (:title %)]
              {:key (:id %)})
           @posts)]]))
