(ns frontend.core
  (:require
    [reagent.core :as reagent]
    [re-frame.core :as re-frame]
    [frontend.events :as events]
    [frontend.views :as views]
    [frontend.config :as config]
    [frontend.server-talk.core :as server-talk]
    [frontend.events-tests]))

(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/app]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [::events/app-initialized])
  (dev-setup)
  (mount-root))
