(ns frontend.events-tests
  (:require
    [devcards.core :as devcards :refer-macros [defcard deftest defcard-rg]]
    [cljs.test :refer-macros [is testing]]
    [frontend.events :as events]
    [frontend.views-prototypes]))

#_(deftest events-tests
  "We are using a **humble view** strategy to test the user interface. The
  components do not process data, do not change the state of the app. Everything
  that happens in the app, triggers an **event** that produces a new app-state."
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
