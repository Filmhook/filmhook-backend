package com.annular.filmhook.util;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.annular.filmhook.model.User;

@Component
public class MailNotification {

    public static final Logger logger = LoggerFactory.getLogger(MailNotification.class);

    @Autowired
    private JavaMailSender javaMailSender;

    public boolean sendEmail(String userName, String mailId, String subject, String mailContent) {
        try {
            String senderName = "Film-hook IT-Support";
            String finalMailContent = "<div style='font-family:Verdana;font-size:12px;'>";
            finalMailContent += "<p>Hello <b>" + userName + "</b>,</p>";
            finalMailContent += mailContent;
            finalMailContent += "<p>Thank You,<br>Film-hook IT-Support.</p></div>";

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom("${spring.mail.username}", senderName);
            helper.setTo(mailId);
            helper.setSubject(subject);
            helper.setText(finalMailContent, true);
            javaMailSender.send(message);
            return true;
        } catch (Exception e) {
            logger.error("Error sending email -> {}", e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Async
    public void sendEmailAsync(String userName, String mailId, String subject, String mailContent) {
        this.sendEmail(userName, mailId, subject, mailContent);
    }

    public boolean sendEmailSync(String userName, String mailId, String subject, String mailContent) {
        return this.sendEmail(userName, mailId, subject, mailContent);
    }

    public boolean sendVerificationEmail(User user) {
        try {
            if (Utility.isNullOrBlankWithTrim(user.getEmail()) || Utility.isNullOrZero(user.getFilmHookOtp())) throw new IllegalArgumentException("Email or OTP is null");
            String subject = "EmailId Verification";
            String mailContent = "<p>Please use the following OTP to verify your fimHookCode on FilmHook. OTP -> <b>" + user.getFilmHookOtp() + "</b></p>";
            return this.sendEmailSync(user.getName(), user.getEmail(), subject, mailContent);
        } catch (IllegalArgumentException e) {
            logger.error("Email or OTP is null for the user -> {}", user.getUserId());
            e.printStackTrace();
        } catch (Exception e) {
            logger.error("Failed to send verification email for user: {}", user.getUserId());
            e.printStackTrace();
        }
        return false;
    }

}
