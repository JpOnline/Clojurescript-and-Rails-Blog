require 'rails_helper'

RSpec.describe 'Authentication', type: :request do
  describe 'POST /auth/new_passcode' do
    context 'When a valid email is provided' do
      it 'should create user with a passcode' do
        user_passcode = AuthenticateUser.new_passcode('jpsoares106@gmail.com')

        expect(user_passcode).not_to be_nil
      end

      it 'should generate a new passcode every time is requested' do
        passcode1 = AuthenticateUser.new_passcode('jpsoares106@gmail.com')
        passcode2 = AuthenticateUser.new_passcode('jpsoares106@gmail.com')

        expect(passcode1).not_to eq(passcode2)
      end
    end

    context 'When an invalid email is provided' do
      before {post '/auth/new_passcode', params: {email: 'not an email'}}

      it 'should send error message not a valid email.' do
        expect(response.body).to match(/Email inválido/)
      end
    end
  end

  describe 'POST /auth/login' do
    context 'When user provide the right passcode sent by email' do
      it 'should return an authentication token' do
        user_passcode = AuthenticateUser.new_passcode('jpsoares106@gmail.com')
        post '/auth/login', params: {email: 'jpsoares106@gmail.com',
                                     passcode: user_passcode}

        expect(json['auth_token']).not_to be_nil
      end
    end

    context 'When user provide the wrong passcode' do
      it 'should not authenticate' do
        post '/auth/login', params: {email: 'jpsoares106@gmail.com',
                                     passcode: '1234'}

        expect(response.body).to match(/Credenciais inválidas/)
      end
    end
  end


  private

  def json
    JSON.parse(response.body)
  end
end
