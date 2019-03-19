class AuthenticationController < ApplicationController
  def authenticate
    auth_token = AuthenticateUser
      .new(auth_params[:email])
      .login(auth_params[:passcode])

    json_response({auth_token: auth_token})
  end

  def auth_params
    params.permit(:email, :passcode)
  end
end
