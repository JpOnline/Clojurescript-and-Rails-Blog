(ns frontend.subs
  (:require
   [re-frame.core :as re-frame]))

;; Please, be aware that db in re-frame's context means the state of the app.

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
  ::posts
  (fn [db]
    (get-in db [:domain :posts])))

(re-frame/reg-sub
  ::loading?
  (fn [db]
    (get-in db [:ui :loading?])))
