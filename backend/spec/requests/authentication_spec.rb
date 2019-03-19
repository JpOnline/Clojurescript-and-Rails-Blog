require 'rails_helper'

RSpec.describe 'Authentication', type: :request do
  describe 'POST /auth/request-passcode' do

    context 'When a valid email is provided' do
      it 'should create user with a passcode' do
        user_passcode = AuthenticateUser.new('jpsoares106@gmail.com').new_passcode

        expect(user_passcode).not_to be_nil
      end

      it 'should generate a new passcode every time is requested' do
        passcode1 = AuthenticateUser.new('jpsoares106@gmail.com').new_passcode
        passcode2 = AuthenticateUser.new('jpsoares106@gmail.com').new_passcode

        expect(passcode1).not_to eq(passcode2)
      end
    end
  end
end
