module ExceptionHandler
  extend ActiveSupport::Concern

  class UnauthorizedRequest < StandardError; end
  class BadRequest < StandardError; end
  class UnprocessableEntity < StandardError; end

  included do

    rescue_from ExceptionHandler::BadRequest do |e|
      json_response({message: e.message}, :bad_request)
    end

    rescue_from ExceptionHandler::UnauthorizedRequest do |e|
      json_response({message: e.message}, :unauthorized)
    end

    rescue_from ActiveRecord::RecordNotFound do |e|
      json_response({message: e.message}, :not_found)
    end

    rescue_from ActiveRecord::RecordInvalid do |e|
      json_response({message: e.message}, :unprocessable_entity)
    end

    rescue_from ExceptionHandler::UnprocessableEntity do |e|
      json_response({message: e.message}, :unprocessable_entity)
    end
  end
end

