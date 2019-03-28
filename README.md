[<img alt="Versão em Português" height="21" width="21" src="http://aux.iconspalace.com/uploads/1427634447.png">
](README.pt.md)

# Clojurescript and Rails Blog

This project concists of a mobile first design, a backend server developed in Ruby
on Rails exposing an API for a frontend client in a Single Page Application (SPA)
style using Clojurescript on [Re-frame](https://github.com/Day8/re-frame).

You can check it in https://jponline.github.io/Clojurescript-and-Rails-Blog/ and its devcards in https://jponline.github.io/Clojurescript-and-Rails-Blog/cards

## Running

To run the server you'll need to have [Rails installed](https://guides.rubyonrails.org/getting_started.html#installing-rails)
and enter the following commands

```
/backend$ bundle install
/backend$ rails s
```

in the `backend` directory. Consequently the server will start in
http://localhost:3000/

To run the frontend you'll need to have [Leiningen](https://leiningen.org/) and
enter the following command

`/frontend$ lein figwheel dev devcards`

in the `frontend` directory. So the app will be available in http://localhost:3449/

PS: You can use `ctrl-h` to hide the Re-frame debug panel.

## Authentication

To create, edit and delete posts the user needs to be **logged**. The app has a
single **author**, only him is capable of executing the author's actions, however
to improve the demo the author's role is enabled to any logged user.

The authentication is done by **email verification**. A verification code is sent
to the user's email when informed in the login screen. The code also is displayed
in the server console in the case you have access to it.

## Editing posts

Each change in the post is sent to the server automatically (a spinner shows when
the app is waiting for a response from the server). A Markdown to HTML is done in
real time.

## Tests

In the backend the [RSpec](http://rspec.info/) framework is used in place of the
standard Minitest. The tast cases are defined in 2 files:
`backend/spec/requests/authentication_spec.rb` and
`backend/spec/requests/posts_spec.rb`. To run them you can run the command

`/backend$ bundle exec rspec`

In the frontend the [Devcards](https://github.com/bhauman/devcards) is used, it
helps to isolate the **views** and **components** to be tested and prototyped out
of the app context. There's also tests using the **Humble View** strategy,
isolating the views models verifying that the **events** change the model as
expected. These tests can be verified in

- http://localhost:3449/cards.html#!/frontend.views_prototypes and;
- http://localhost:3449/cards.html#!/frontend.events_tests
