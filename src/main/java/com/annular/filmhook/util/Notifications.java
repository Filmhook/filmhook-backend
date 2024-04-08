package com.annular.filmhook.util;

import jakarta.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class Notifications {
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	public static final Logger logger = LoggerFactory.getLogger(Notifications.class);

	 @Async
	    public void emailNotification(String mailId, String subject, String mailContent) {
	        try {
	            logger.info("emailNotification method start");
	            String senderName = "FilmHook";
	            MimeMessage message = javaMailSender.createMimeMessage();
	            MimeMessageHelper helper = new MimeMessageHelper(message);
	            helper.setFrom("yaswanthshankar2705@gmail.com", senderName);
	            helper.setTo(mailId);
	            helper.setSubject(subject);
	            helper.setText(mailContent, true);
	            javaMailSender.send(message);
	            logger.info("emailNotification method end");
	        } catch (Exception e) {
	            logger.error("Error sending email", e);
	        }
	    }

}
