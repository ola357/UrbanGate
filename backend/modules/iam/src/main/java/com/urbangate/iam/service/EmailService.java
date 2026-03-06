package com.urbangate.iam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

//TODO : (TESTING PURPOSE) - REPLACE THIS LATER WITH A MORE DYNAMIC EMAIL SERVICE IN THE NOTIFICATION MODULE


@Service
@RequiredArgsConstructor
public class EmailService {



  private final JavaMailSender mailSender;

  @Value("${spring.mail.from:no-reply@urbangate.com}")
  private String from;

  public void sendEmail(String to, String data) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(from);
    message.setTo(to);
    message.setSubject("Your Password Reset Code");
    message.setText("""
                Hi,

                This is your requested Code.

                Code: %s

                If you didn't request this, you can safely ignore this email.

                – The Urban Gate Team
                """.formatted(data));

    mailSender.send(message);
  }
}
