Rails.application.routes.draw do
  # For details on the DSL available within this file, see http://guides.rubyonrails.org/routing.html

  resources :posts
  post 'auth/new_passcode', to: 'authentication#new_passcode'
  post 'auth/login', to: 'authentication#authenticate'
end
