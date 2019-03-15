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

(deftest toggle-actions-menu
  (testing "Opening actions menu"
          ;; Arranging
    (let [app-state (is {:ui {:actions-open? false}}
                        "(app-state) When actions menu is closed")
          ;; System Under Test
          result (is (events/toggled-actions-handler app-state)
                     "(result) and the toggled-actions event is dispatched")]
          ;; Asserting
      (is (= result {:ui {:actions-open? true}})
          "it should set its state to opened")))

  (testing "Closing actions menu"
    (let [app-state (is {:ui {:actions-open? true}}
                        "(app-state) When actions menu is opened")
          result (is (events/toggled-actions-handler app-state)
                     "(result) and the toggled-actions event is dispatched")]
      (is (= result {:ui {:actions-open? false}})
          "it should set its state to closed"))))

(deftest post-content-changed
  (testing "Updating post"
    ;; Context
    (let [app-state (is {:domain {:posts
                                  [{:id 2
                                    :title "Life answer"
                                    :content "41",
                                    :submited_by "jpsoares106@gmail.com",
                                    :updated_at "2019-03-14T18:50:45.033Z"
                                    :created_at "2019-03-06T20:06:21.353Z"}]}}
                        "(app-state) Given there's a submited post")]
      ;; System Under Test
      (let [result (is (events/post-changed-handler
                         app-state
                         [::events/post-title-changed 2 "The ultimate answer"])
                       "(result) When title is changed")]
        ;; Asserting
        (is (= (get-in result [:domain :posts 0])
               {:id 2
                :title "The ultimate answer"
                :content "41",
                :submited_by "jpsoares106@gmail.com",
                :updated_at "2019-03-14T18:50:45.033Z"
                :created_at "2019-03-06T20:06:21.353Z"})
            "it should update post title."))
      (let [result (is (events/post-changed-handler
                         app-state
                         [::events/post-content-changed 2 "42"])
                       "(result) When content is changed")]
        ;; Asserting
        (is (= (get-in result [:domain :posts 0])
               {:id 2
                :title "Life answer"
                :content "42",
                :submited_by "jpsoares106@gmail.com",
                :updated_at "2019-03-14T18:50:45.033Z"
                :created_at "2019-03-06T20:06:21.353Z"})
            "it should update post content")))))

(deftest deleted-post
  (testing "Deleting post"
    ;; Arranging
    (let [app-state (is {:domain {:posts
                                  [{:id 2
                                    :title "Título"
                                    :content "Conteúdo",
                                    :submited_by "jpsoares106@gmail.com",
                                    :updated_at "2019-03-14T18:50:45.033Z"
                                    :created_at "2019-03-06T20:06:21.353Z"}]}}
                        "(app-state) When there's a post")
          ;; System Under Test
          result (is (events/deleted-post-handler app-state 2)
                     "(result) and post of id 2 is deleted")]
      ;; Asserting
      (is (= (get-in result [:domain :posts]) [])
          "post should no longer exist"))))

(deftest change-state
  (testing "When state is initial and event is post-created"
    (let [db {:ui {:state :initial}}]
      (is (= :editing_post
             (get-in
               (events/next-state db [:post-created])
               [:ui :state]))
          "It should transit to the editing_post state.")
      (is (= [{:title "Título", :content "## Conteúdo"}]
             (get-in (events/create-new-post-local db) [:domain :posts]))
          "It should create post locally.")))
  (testing "When state is editing_post and event is went-back"
    (let [db {:ui {:state :editing_post}}]
      (is (= :initial
             (get-in
               (events/next-state db [:went-back])
               [:ui :state]))
          "It should transit to the initial state.")))
  (testing "Reading post"
    (let [app-state (is {:ui {:state :initial}}
                        "(app-state) When in initial state")
          event (is [:clicked-post 3] "(event) and the post with id 3 is clicked")
          result (is (events/clicked-post-handler app-state event) "(result)")]
      (is (= :post_detail
             (get-in result [:ui :state]))
          "It should change ui state to post detail.")
      (is (= 3
             (get-in result [:ui :selected-post-id]))
          "It should set the selected post id to 3."))))
