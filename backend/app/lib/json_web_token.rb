class JsonWebToken
  # TODO: make credentials secret to work
  # HMAC_SECRET = Rails.application.credentials.secret_key_base
  HMAC_SECRET = '8654466f85f6137e3859454817b1bec5'

  def self.encode(payload, exp = 24.hours.from_now)
    puts 'JsonWebToken encode'
    puts payload.inspect.to_yaml

    payload[:exp] = exp.to_i

    JWT.encode(payload, HMAC_SECRET)
  end

  def self.decode(token)
    puts 'JsonWebToken decode'

    payload = JWT.decode(token, HMAC_SECRET)[0]
    HashWithIndifferentAccess.new payload

  rescue JWT::DecodeError => e
    raise ExceptionHandler::UnprocessableEntity,
      "Invalid Json Web Token. " + e.message
  end
end
