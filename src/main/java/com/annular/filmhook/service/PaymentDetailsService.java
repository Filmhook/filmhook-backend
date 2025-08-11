package com.annular.filmhook.service;

import org.springframework.http.ResponseEntity;

import com.annular.filmhook.webmodel.PaymentDetailsWebModel;
import com.annular.filmhook.webmodel.PromoteWebModel;

public interface PaymentDetailsService {

	ResponseEntity<?> savePayment(PaymentDetailsWebModel paymentDetailsWebModel);

	ResponseEntity<?> emailSend(PaymentDetailsWebModel paymentDetailsWebModel);

	ResponseEntity<?> promotionPending(PaymentDetailsWebModel paymentDetailsWebModel);

	ResponseEntity<?> promotionFailed(PaymentDetailsWebModel paymentDetailsWebModel);

	ResponseEntity<?> promotionForCron(PaymentDetailsWebModel paymentDetailsWebModel);
	
	ResponseEntity<?> getByPromoteId(PromoteWebModel promoteWebModel);

}
