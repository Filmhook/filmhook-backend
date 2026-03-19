package com.annular.filmhook.service;

import java.math.BigDecimal;

import com.annular.filmhook.enums.TransactionType;
import com.annular.filmhook.model.PaymentWebModel;

public interface PaymentService {

	PaymentWebModel generatePaymentToken(PaymentWebModel paymentWebModel) throws Exception;


	void handleGatewaySuccess(String txnId,
			BigDecimal amount,
			TransactionType type,
			Integer referenceId,
			String gatewayResponse);


	void handleGatewayFailure(String txnId, BigDecimal amount, TransactionType type, Integer referenceId, String gatewayResponse);


}
