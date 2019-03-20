require 'rails_helper'

class AuthenticateUser

  def self.new_passcode(email)
    new_passcode = rand(100000).to_s

    # Check if user exists
    user = User.find_by(email: email)
    if !user
      # Create user if it does not exist
      user = User.create!({email: email, password: new_passcode})
    else
      # Update its codepass
      user.update!({password: new_passcode})
    end

    new_passcode
  end

  def self.login(email, passcode)
    user = User.find_by(email: email)

    if user && user.authenticate(passcode)
      JsonWebToken.encode(user_id: user.id)
    else
      raise(ExceptionHandler::AuthenticationError, 'Credenciais inválidas')
    end
  end
end