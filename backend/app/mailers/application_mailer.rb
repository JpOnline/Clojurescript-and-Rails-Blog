class ApplicationMailer < ActionMailer::Base
  default from: 'from@example.com'
  layout 'mailer'

  def passcode_email(email, passcode)
    mail(to: email,
         subject: "Verirication code: #{passcode}",
         body: "<h1>Verirication code: #{passcode}</h1>",
         content_type: "text/html")
  end
end
