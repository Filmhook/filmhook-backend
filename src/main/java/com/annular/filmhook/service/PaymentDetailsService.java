package com.annular.filmhook.service;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.webmodel.PaymentDetailsWebModel;

public interface PaymentDetailsService {

	ResponseEntity<?> savePayment(PaymentDetailsWebModel paymentDetailsWebModel);

	ResponseEntity<?> emailSend(PaymentDetailsWebModel paymentDetailsWebModel);

}
