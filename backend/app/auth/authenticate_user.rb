require 'rails_helper'

class AuthenticateUser
  def initialize(email)
    @email = email
  end

  def new_passcode
    new_passcode = rand(100000).to_s

    # Check if user exists
    user = User.find_by(email: @email)
    if !user
      # Create user if it does not exist
      user = User.create!({email: @email, password: new_passcode})
    else
      # Update its codepass
      user.update!({password: new_passcode})
    end

    new_passcode
  end
end
