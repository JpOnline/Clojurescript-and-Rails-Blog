class AuthenticationController < ApplicationController

  def new_passcode
    email = auth_params[:email]
    validate_email email

    user_passcode = AuthenticateUser.new_passcode(email)

    ApplicationMailer
      .passcode_email(email, user_passcode)
      .deliver_now

    puts "\n\nFor the case you don't want to look in your email here's the code..\n
    Verification code:: #{user_passcode}\n\n\n"

    json_response({message: "Verification code sent to #{auth_params[:email]}"})
  end

  def authenticate
    puts 'AuthenticationController authenticate entering'

    token_and_user = AuthenticateUser
      .login(auth_params[:email], auth_params[:passcode])

    puts 'AuthenticationController authenticate after login'

    user_role = AuthenticateUser.user_role(token_and_user[:user])

    puts 'AuthenticationController authenticate after user_role'

    json_response({auth_token: token_and_user[:auth_token],
                   user: token_and_user[:user],
                   user_role: user_role})
  end


  private

  def auth_params
    params.permit(:email, :passcode)
  end

  def validate_email(email)
    valid_email_regex = /\A[\w+\-.]+@[a-z\d\-]+(\.[a-z\d\-]+)*\.[a-z]+\z/i

    if !valid_email_regex.match(email)
      raise(ExceptionHandler::BadRequest, 'Invalid email')
    end
  end
end
