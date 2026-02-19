package com.annular.filmhook.service;

import java.math.BigDecimal;

import com.annular.filmhook.enums.PaymentMode;
import com.annular.filmhook.enums.TransactionType;
import com.annular.filmhook.model.Transaction;
import com.annular.filmhook.model.User;


public interface TransactionService {

    Transaction createTransaction(User user,
                                  String txnId,
                                  TransactionType type,
                                  PaymentMode paymentMode,
                                  Integer referenceId,
                                  BigDecimal amount);

    void markSuccess(String txnId, String gatewayResponse);

    void markFailed(String txnId, String gatewayResponse);

    Transaction getByTxnId(String txnId);
}
