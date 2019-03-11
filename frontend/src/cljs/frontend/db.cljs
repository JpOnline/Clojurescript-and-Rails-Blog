(ns frontend.db)

(def default-db
  {:name "Blog"
   :ui {:state :initial
        :actions-open? false}})

;; Defining state machines will help to keep track of component states when
;; multiple components are updated in an event. The idea come from Jeb Beich and
;; is described in the posts
;; http://blog.cognitect.com/blog/2017/5/22/restate-your-ui-using-state-machines-to-simplify-user-interface-development
;; http://blog.cognitect.com/blog/2017/8/14/restate-your-ui-creating-a-user-interface-with-re-frame-and-state-machines

(def initial-state-machine
  {nil {:app-initialized :initial}
   :initial {:post-created :editing_post
             :clicked-post :editing_post}
   :editing_post {:went-back :initial
                  :deleted-post :initial}})