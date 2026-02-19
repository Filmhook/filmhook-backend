package com.annular.filmhook.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.annular.filmhook.enums.PaymentMode;
import com.annular.filmhook.enums.RefundStatus;
import com.annular.filmhook.enums.TransactionStatus;
import com.annular.filmhook.enums.TransactionType;
import com.annular.filmhook.model.Refund;
import com.annular.filmhook.model.Transaction;
import com.annular.filmhook.repository.RefundRepository;
import com.annular.filmhook.repository.TransactionRepository;
import com.annular.filmhook.service.RefundService;
import com.annular.filmhook.service.WalletService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefundServiceImpl implements RefundService {

    private final TransactionRepository transactionRepository;
    private final WalletService walletService;
    private final RefundRepository refundRepository;

    @Override
    @Transactional
    public void refundToWallet(Integer transactionId, String reason) {

        Transaction original = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (original.getStatus() != TransactionStatus.SUCCESS) {
            throw new RuntimeException("Only SUCCESS transactions can be refunded");
        }

        Transaction refundTxn = new Transaction();
        refundTxn.setUser(original.getUser());
        refundTxn.setType(TransactionType.REFUND);
        refundTxn.setPaymentMode(PaymentMode.WALLET);
        refundTxn.setAmount(original.getAmount());
        refundTxn.setStatus(TransactionStatus.SUCCESS);

        transactionRepository.save(refundTxn);

        walletService.credit(original.getUser(),
                original.getAmount(),
                refundTxn,
                "Refund");

        original.setStatus(TransactionStatus.REFUNDED);
        transactionRepository.save(original);

        Refund refund = new Refund();
        refund.setOriginalTransaction(original);
        refund.setAmount(original.getAmount());
        refund.setReason(reason);
        refund.setStatus(RefundStatus.SUCCESS);

        refundRepository.save(refund);
    }
}
