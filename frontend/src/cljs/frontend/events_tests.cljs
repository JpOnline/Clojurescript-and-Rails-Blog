(ns frontend.events-tests
  (:require
    [devcards.core :as devcards :refer-macros [defcard deftest defcard-rg]]
    [cljs.test :refer-macros [is testing]]
    [frontend.events :as events]
    [frontend.views-prototypes]))

(defcard initial-state-machine-doc
  (str "# Tests

       We are using a **humble view** strategy to test the user interface. The
  components do not process data, do not change the state of the app. Everything
  that happens in the app, triggers an **event** that produces a new app-state.")
  {}
  {:frame false
   :heading false})

(deftest initialized-event
  (testing "When app is initialized,"
    (is (= (events/app-initialized-handler)
           {:name "Blog"
            :ui {:state :initial :loading? true :actions-open? false}})
        "it should set the loading flag."))
  (testing "When server answered all posts"
    (let [db {:ui {:loading? true}}
          posts [{:id 1,
                  :title "A super post",
                  :submited_by "jpsoares106@gmail.com",
                  :content "# All the knowledge you can imagine",
                  :created_at "2019-03-06T20:06:21.353Z",
                  :updated_at "2019-03-06T20:06:21.353Z"}]]
      (is (= {:posts [{:id 1,
                       :title "A super post",
                       :submited_by "jpsoares106@gmail.com",
                       :content "# All the knowledge you can imagine",
                       :created_at "2019-03-06T20:06:21.353Z",
                       :updated_at "2019-03-06T20:06:21.353Z"}]}
             (:domain (events/server-answered-all-posts-handler db posts)))
          "it should set the posts data to domain/posts")
      (is (= {:loading? false}
             (:ui (events/server-answered-all-posts-handler db posts)))
          "it should unset the loading flag."))))

(deftest toogle-actions-menu
  (testing "When actions menu is closed and it's pressed"
    (is (= {:ui {:actions-open? true}}
           (events/toggled-actions-handler {:ui {:actions-open? false}}))
        "It should set its state to open")))

(deftest post-content-changed
  (testing "When post content is changed"
    (let [db {:domain {:posts [{:id 1 :title "Life answer" :content "41"}]}}]
      (is (= {:id 1 :title "Life answer" :content "42"}
             (get-in (events/post-content-changed-handler
                       db
                       [:post-content-changed 1 "42"])
                     [:domain :posts 0]))))))

(deftest change-state
  (testing "When an state change event is triggered"
    (let [db {:ui {:state :initial}}]
      (is (= :editing_post
             (get-in
               (events/next-state db [:post-created])
               [:ui :state]))
          "It should transit to the next state"))))
