package com.annular.filmhook.service.impl;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.annular.filmhook.enums.PaymentMode;
import com.annular.filmhook.enums.TransactionStatus;
import com.annular.filmhook.enums.TransactionType;
import com.annular.filmhook.model.Transaction;
import com.annular.filmhook.model.User;
import com.annular.filmhook.repository.TransactionRepository;
import com.annular.filmhook.service.TransactionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public Transaction createTransaction(User user,
                                         String txnId,
                                         TransactionType type,
                                         PaymentMode paymentMode,
                                         Integer referenceId,
                                         BigDecimal amount) {

        Transaction txn = new Transaction();
        txn.setUser(user);
        txn.setTxnId(txnId);
        txn.setType(type);
        txn.setPaymentMode(paymentMode);
        txn.setReferenceId(referenceId);
        txn.setAmount(amount);
        txn.setStatus(TransactionStatus.PENDING);

        return transactionRepository.save(txn);
    }

    @Override
    @Transactional
    public void markSuccess(String txnId, String gatewayResponse) {
    	System.out.println("Check to Transactiion Table data ");
        Transaction txn = getByTxnId(txnId);
        txn.setStatus(TransactionStatus.SUCCESS);
        txn.setGatewayResponse(gatewayResponse);

        transactionRepository.save(txn);
    }

    @Override
    @Transactional
    public void markFailed(String txnId, String gatewayResponse) {

        Transaction txn = getByTxnId(txnId);
        txn.setStatus(TransactionStatus.FAILED);
        txn.setGatewayResponse(gatewayResponse);

        transactionRepository.save(txn);
    }

    @Override
    public Transaction getByTxnId(String txnId) {
        return transactionRepository.findByTxnId(txnId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }
}
