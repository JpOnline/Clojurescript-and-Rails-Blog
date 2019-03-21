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
      {auth_token: JsonWebToken.encode(user_id: user.id),
       user: user}
    else
      raise(ExceptionHandler::UnauthorizedRequest, 'Credenciais inválidas')
    end
  end

  def self.logged_user(headers)
    decoded_auth_token = decode_auth_token(headers)

    User.find(decoded_auth_token[:user_id]) if decoded_auth_token
  end

  def self.user_role(user)
    # There's only one author for the blog, for demonstration purposes any logged
    # user is an author.

    # return user && user.email == 'jpsoares106@gmail.com' ? 'author' : 'reader'

    return user ? 'author' : 'reader'
  end


  private

  def self.decode_auth_token(headers)
    if !headers['Authorization'].present?
      return nil
    end

    http_auth_header = headers['Authorization'].split(' ').last

    JsonWebToken.decode(http_auth_header)
  end
end
