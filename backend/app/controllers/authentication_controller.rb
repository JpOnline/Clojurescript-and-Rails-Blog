class AuthenticationController < ApplicationController
  def authenticate
    auth_token = AuthenticateUser
      .login(auth_params[:email], auth_params[:passcode])

    json_response({auth_token: auth_token})
  end

  def auth_params
    params.permit(:email, :passcode)
  end
end
