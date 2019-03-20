class ApplicationController < ActionController::API
  include Response
  include ExceptionHandler

  before_action :add_user_info
  attr_reader :user_role, :current_user


  private

  def add_user_info
    @current_user = AuthenticateUser.logged_user(request.headers)

    @user_role = @current_user && @current_user.email == 'jpsoares106@gmail.com' ?
      'author' : 'reader'
  end
end
