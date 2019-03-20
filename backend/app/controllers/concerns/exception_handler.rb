module ExceptionHandler
  extend ActiveSupport::Concern

  class AuthenticationError < StandardError; end

  included do

    rescue_from ExceptionHandler::AuthenticationError do |e|
      json_response({message: e.message}, :unauthorized)
    end

    rescue_from ActiveRecord::RecordNotFound do |e|
      json_response({message: e.message}, :not_found)
    end

    rescue_from ActiveRecord::RecordInvalid do |e|
      json_response({message: e.message}, :unprocessable_entity)
    end

    rescue_from ::StandardError do |e|
      json_response({message: e.message}, :unprocessable_entity)
    end
  end
end

