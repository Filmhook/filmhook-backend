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
            String logoSection = "<tr><td align='center' style='padding-bottom:20px;'>"
                    + "<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/filmHookLogo.png' alt='FilmHook Logo' style='width:180px;height:auto;'>"
                    + "</td></tr>";
            String appSection = "<tr><td style='padding:5px;'>"
                    + "<p><strong>📲 Get the App:</strong></p>"
                    + "<p>"
                    + "<a href='https://play.google.com/store/apps/details?id=com.projectfh&hl=en'>"
                    + "<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/PlayStore.jpeg' alt='Android' width='25' style='margin-right:10px;'></a>"
                    + "<a href='#'>"
                    + "<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Apple.jpeg' alt='iOS' width='25'></a>"
                    + "</p></td></tr>";

            String socialSection = "<tr><td style='padding:5px;'>"
                    + "<p><strong>📢 Follow Us:</strong></p>"
                    + "<p>"
                    + "<a href='https://www.facebook.com/share/1BaDaYr3X6/?mibextid=qi2Omg'>"
                    + "<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/faceBook.jpeg' width='25' style='margin-right:5px;'></a>"
                    + "<a href='https://x.com/Filmhook_Apps?t=KQJkjwuvBzTPOaL4FzDtIA&s=08/'>"
                    + "<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Twitter.jpeg' width='25' style='margin-right:5px;'></a>"
                    + "<a href='https://www.threads.net/@filmhookapps/'>"
                    + "<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Threads.jpeg' width='25' style='margin-right:5px;'></a>"
                    + "<a href='https://www.instagram.com/filmhookapps?igsh=dXdvNnB0ZGg5b2tx'>"
                    + "<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Instagram.jpeg' width='25' style='margin-right:5px;'></a>"
                    + "<a href='https://youtube.com/@film-hookapps'>"
                    + "<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/Youtube.jpeg' width='25' style='margin-right:5px;'></a>"
                    + "<a href='https://www.linkedin.com/in/film-hook-68666a353'>"
                    + "<img src='https://filmhook-dev-bucket.s3.ap-southeast-2.amazonaws.com/MailLogo/linked.png' width='25'></a>"
                    + "</p></td></tr>";
            
            String bodyStart = "<html><body style='font-family:Verdana;font-size:12px;'><table width='100%' cellpadding='0' cellspacing='0'>";
            String greeting = "<tr><td><p>Dear <b>" + userName + "</b>,</p></td></tr>";
            String mainContent = "<tr><td>" + mailContent + "</td></tr>";
            String closing = "<tr><td><p>Best Regards,<br>The Film-hook Team.</p></td></tr>";
            String bodyEnd = "</table></body></html>";
            
            String finalMailContent = bodyStart + logoSection + greeting + mainContent + closing + appSection + socialSection + bodyEnd;


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
