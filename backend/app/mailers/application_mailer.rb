class ApplicationMailer < ActionMailer::Base
  default from: 'from@example.com'
  layout 'mailer'

  def passcode_email(email, passcode)
    mail(to: email,
         subject: "Código de verificação: #{passcode}",
         body: "<h1>Código de verificação: #{passcode}</h1>",
         content_type: "text/html")
  end
end
