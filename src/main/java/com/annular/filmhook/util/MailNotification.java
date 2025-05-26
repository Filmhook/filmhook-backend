package com.annular.filmhook.util;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.annular.filmhook.model.HelpAndSupport;
import com.annular.filmhook.model.User;

@Component
public class MailNotification {

    public static final Logger logger = LoggerFactory.getLogger(MailNotification.class);

    @Autowired
    private JavaMailSender javaMailSender;
    
    @Value("${spring.mail.username}")
    private String senderEmail;
    
    public boolean sendEmail(String userName, String mailId, String subject, String mailContent) {
        try {
            String senderName = "Film-hook IT-Support";
            String finalMailContent = "<div style='font-family:Verdana;font-size:12px;'>";
            finalMailContent += "<p>Dear <b>" + userName + "</b>,</p>";
            finalMailContent += mailContent;
            finalMailContent += "<p>Best Regards,<br>The Film-hook Team.</p></div>";

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

//    public boolean sendVerificationEmail(User user) {
//        try {
//            if (Utility.isNullOrBlankWithTrim(user.getEmail()) || Utility.isNullOrZero(user.getEmailOtp())) throw new IllegalArgumentException("Email or OTP is null");
//            String subject = "EmailId Verification";
//            String mailContent = "<p>Please use the following OTP to verify your fimHook on FilmHook. OTP -> <b>" + user.getEmailOtp() + "</b></p>";
//            return this.sendEmailSync(user.getName(), user.getEmail(), subject, mailContent);
//        } catch (IllegalArgumentException e) {
//            logger.error("Email or OTP is null for the user -> {}", user.getUserId());
//            e.printStackTrace();
//        } catch (Exception e) {
//            logger.error("Failed to send verification email for user: {}", user.getUserId());
//            e.printStackTrace();
//        }
//        return false;
//    }
    public boolean sendVerificationEmail(User user) {
        try {
            if (Utility.isNullOrBlankWithTrim(user.getEmail()) || Utility.isNullOrZero(user.getEmailOtp())) {
                throw new IllegalArgumentException("Email or OTP is null");
            }
            
            String subject = "FilmHook Account Verification";
            String mailContent = "<p>Your One-Time Password (OTP) to verify your FilmHook account is: <b>" + user.getEmailOtp() + "</b>.</p>"
                               + "<p>This code is valid for a short time. Please do not share it with anyone.</p>";
            
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


    public boolean sendFilmHookQueries(HelpAndSupport dbData) {
    	try {
            String senderName = "Film-hook IT-Support";
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Use the recipient email from dbData as the sender email
            helper.setFrom(dbData.getReceipentEmail());

            // Use the injected sender email from configuration as the recipient email
            helper.setTo(senderEmail);

            helper.setSubject(dbData.getSubject());
            helper.setText(dbData.getMessage());

            javaMailSender.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
