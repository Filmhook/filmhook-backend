package com.annular.filmhook.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Component
public class TwilioConfig {

    @Value("${annular.app.twilio.accountSid}")
    private String accountSid;

    @Value("${annular.app.twilio.authToken}")
    private String authToken;

    public static final Logger logger = LoggerFactory.getLogger(TwilioConfig.class);

    @Async
    public void smsNotification(String number, String message) {
        try {
            logger.info("smsNotification method start");
            Twilio.init(accountSid, authToken);
            Message.creator(new PhoneNumber("+91" + number), new PhoneNumber("+17602974960"), message).create();
            logger.info("smsNotification method end");
        } catch (Exception e) {
            logger.error("smsNotification method error -> {}", e.getMessage());
            e.printStackTrace();
        }
    }

}