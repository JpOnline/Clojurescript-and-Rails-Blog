(ns frontend.events-tests
  (:require
    [devcards.core :as devcards :refer-macros [defcard deftest defcard-rg]]
    [cljs.test :refer-macros [is testing]]
    [frontend.events :as events]
    [frontend.views-prototypes]))

(defcard tests-doc
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
           {:ui {:state :initial :loading? true :actions-open? false}})
        "it should set the loading flag."))

  (testing "Server answered all posts"
    (let [app-state (is {:ui {:loading? true}} "Given the following app-state")
          posts (is [{:id 1,
                      :title "A super post",
                      :submited_by "jpsoares106@gmail.com",
                      :content "# All the knowledge you can imagine",
                      :created_at "2019-03-06T20:06:21.353Z",
                      :updated_at "2019-03-06T20:06:21.353Z"}]
                    "when the server answered with the following posts")
          result (events/server-answered-all-posts-handler app-state posts)]
      (is (= (:domain result) {:posts posts})
          "it should set the posts data to domain/posts")
      (is (= (:ui result) {:loading? false})
          "and it should unset the loading flag."))))

(deftest toggle-actions-menu
  (testing "Opening actions menu"
          ;; Arranging
    (let [app-state (is {:ui {:actions-open? false}}
                        "(app-state) Given actions menu is closed")
          ;; System Under Test
          result (is (events/toggled-actions-handler app-state)
                     "(result) when the toggled-actions event is dispatched")]
          ;; Asserting
      (is (= result {:ui {:actions-open? true}})
          "it should set its state to opened")))

  (testing "Closing actions menu"
    (let [app-state (is {:ui {:actions-open? true}}
                        "(app-state) Given actions menu is opened")
          result (is (events/toggled-actions-handler app-state)
                     "(result) when the toggled-actions event is dispatched")]
      (is (= result {:ui {:actions-open? false}})
          "it should set its state to closed"))))

(deftest post-content-changed
  (testing "Updating post"
    ;; Context
    (let [app-state
          (is {:domain
               {:posts
                [{:id 2
                  :title "Life answer"
                  :content "41",
                  :submited_by "jpsoares106@gmail.com",
                  :updated_at "2019-03-14T18:50:45.033Z"
                  :created_at "2019-03-06T20:06:21.353Z"}]}}
              "(app-state) Given there's a submited post")]
      ;; System Under Test
      (let [result
            (is (events/update-post-locally
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
      ;; System Under Test
      (let [result
            (is (events/update-post-locally
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
    (let [app-state
          (is {:domain
               {:posts
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
      (is (= [{:title "Título",
               :content "## Conteúdo"
               :created_at "0000-00-00",
               :updated_at "0000-00-00T00:00:00.000Z"}]
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
    (let [app-state
          (is {:ui {:state :initial}
               :domain {:posts [{:id 3 :title "Título"}]}}
              "(app-state) Given in initial state")
          event (is [:clicked-post 3] "(event) when the post with id 3 is clicked")
          result (is (events/clicked-post-handler app-state event) "(result)")]
      (is (= :post_detail
             (get-in result [:ui :state]))
          "It should change ui state to post detail.")
      (is (= 0 (get-in result [:ui :selected-post-index]))
          "It should set the selected post id to 3."))))

(deftest error-handling
  (let [result (is (events/received-error-from-server-handler
                     {}
                     [:e "server error message"])
                   "When received error from server")]
    (is (= {:ui {:error-message "server error message"}}
           result)
        "it should set an error message in the UI.")))

(deftest login-events
  (let [app-state (is {:ui {:state :email_input}}
                      "Given user is in the email_input screen (app-state)")
        event (is [::events/email-input-changed "email@dominio.com"]
                  "When email input changed (event)")
        result (is (events/email-input-changed-handler app-state event)
                   "(result)")]
    (is (= (get-in result [:ui :email-input])
           "email@dominio.com")
        "it should set the email input."))

  (let [app-state (is {:ui {:state :email_input
                            :email-input "email@dominio.com"}}
                      "Given the email input is set with a valid email")
        event (is [:server-sent-passcode]
                  "When server sent verification code by email (event)")
        result (is (events/server-sent-passcode-handler app-state event)
                   "(result)")]
    (is (= (get-in result [:ui :state])
           :passcode_input)
        "it should change screen to the passcode input view,")
    (is (= (get-in result [:ui :email-input])
           nil)
        "it should clean the email input"))

  (let [app-state (is {:ui {:state :passcode_input}}
                      "Given user is in the passcode input screen (app-state)")
        event (is [::events/passcode-input-changed "23456"]
                  "When passcode input changed (event)")
        result (is (events/passcode-input-changed-handler app-state event)
                   "(result)")]
    (is (= (get-in result [:ui :passcode-input])
           "23456")
        "it should set the passcode input."))

  (let [app-state (is {:ui {:state :passcode_input
                            :passcode-input "23456"}}
                      "Given the passcode input is set with a valid passcode")
        event (is [:server-authenticated-user {:user_role "author"}]
                  "When server authenticated the user (event)")
        result (is (events/server-authenticated-user-handler app-state event)
                   "(result)")]
    (is (= (get-in result [:ui :state])
           :initial)
        "it should change to the initial screen,")
    (is (= (get-in result [:ui :passcode-input])
           nil)
        "it should clean the passcode input")
    (is (#{"reader" "author"} (get-in result [:server :user :role]))
        "it should set the user role to reader or author."))

  (let [app-state (is {:ui {:state :initial}
                       :server {:user {:email "a@b.com"
                                       :role "reader"}
                                :auth-token "x"}}
                      "Given a logged user (app-state)")
        event (is [::events/logout]
                  "When user logout (event)")
        result (is (events/clicked-logout-handler app-state event)
                   "(result)")]
    (is (nil? (get-in result [:server :user]))
        "it should unset user information")
    (is (nil? (get-in result [:server :auth-token]))
        "it should unset auth-token")))
