class AuthenticationController < ApplicationController

  def new_passcode
    email = auth_params[:email]
    validate_email email

    user_passcode = AuthenticateUser.new_passcode(email)

    ApplicationMailer
      .passcode_email(email, user_passcode)
      .deliver_now

    puts "\n\nCaso vc nao queira ir ate seu email pra olhar o codigo..\n
    Codigo de verificacao: #{user_passcode}\n\n\n"

    json_response({message: "Código de verificação enviado para #{auth_params[:email]}"})
  end

  def authenticate
    token_and_user = AuthenticateUser
      .login(auth_params[:email], auth_params[:passcode])
    user_role = AuthenticateUser.user_role(token_and_user[:user])

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
      raise(ExceptionHandler::BadRequest, 'Email inválido')
    end
  end
end
