package com.annular.filmhook.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MultiMailConfig {

    // 🔹 Default sender (used by Spring Boot automatically)
    // You don’t need to redefine this if you use application.properties

    // 🔹 Secondary sender (for reports, etc.)
	@Bean(name = "OtpMailSender")
	public JavaMailSender OtpMailSender() {
	    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
	    mailSender.setHost("smtp.gmail.com");
	    mailSender.setPort(587);
	    mailSender.setUsername("security@filmhookapp.com");
	    mailSender.setPassword("ygnw tbfw vezf oowk");

	    Properties props = mailSender.getJavaMailProperties();
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.starttls.enable", "true");

	    return mailSender;
	}
}
