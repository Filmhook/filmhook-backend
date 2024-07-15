package com.annular.filmhook.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentConfig {

    private static final Logger logger = LoggerFactory.getLogger(PaymentConfig.class);

    @Value("${annular.app.payment.cashfree.clientId}")
    private String clientId;

    @Value("${annular.app.payment.cashfree.clientSecret}")
    private String clientSecret;

    @Value("${annular.app.payment.cashfree.customerId}")
    private String customerId;

    @Value("${annular.app.payment.cashfree.customerPhone}")
    private String customerPhone;

}
