// Copyright (c) UrbanGate
package com.urbangate.iam.notification;

import java.util.logging.Logger;
import org.springframework.stereotype.Component;

@Component
public class LoggingEmailSender implements EmailSender {
  private static final Logger LOGGER = Logger.getLogger(LoggingEmailSender.class.getName());

  @Override
  public void send(EmailMessage message) {
    LOGGER.info(
        () ->
            "Email stub -> to=%s subject=%s body=%s"
                .formatted(message.to(), message.subject(), message.body()));
  }
}
