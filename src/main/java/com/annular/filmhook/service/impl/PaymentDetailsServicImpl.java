package com.annular.filmhook.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.annular.filmhook.model.PaymentDetails;
import com.annular.filmhook.repository.PaymentDetailsRepository;
import com.annular.filmhook.service.PaymentDetailsService;
import com.annular.filmhook.util.HashGenerator;
import com.annular.filmhook.webmodel.PaymentDetailsWebModel;

@Service
public class PaymentDetailsServicImpl implements PaymentDetailsService{
	


	@Autowired
    private PaymentDetailsRepository paymentDetailsRepository;

    private final String key = "oXregF";
    private final String salt = "fGiczQ8QDLit7B5iEHGQ2glKXv4wKPqe";

    @Override
    public ResponseEntity<?> savePayment(PaymentDetailsWebModel webModel) {
        String hash = HashGenerator.generateHash(
                key,
                webModel.getTxnid(),
                webModel.getAmount(),
                webModel.getProductinfo(),
                webModel.getFirstname(),
                webModel.getEmail(),
                salt
        );

        PaymentDetails details = PaymentDetails.builder()
                .txnid(webModel.getTxnid())
                .amount(webModel.getAmount())
                .productinfo(webModel.getProductinfo())
                .firstname(webModel.getFirstname())
                .email(webModel.getEmail())
                .userId(webModel.getUserId())
                .postId(webModel.getPostId())
                .paymentHash(hash)
                .build();

        paymentDetailsRepository.save(details);

        return ResponseEntity.ok("Payment saved successfully with hash.");
    }

}
