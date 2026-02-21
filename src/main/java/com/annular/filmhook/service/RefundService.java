package com.annular.filmhook.service;

public interface RefundService {

    void refundToWallet(Integer transactionId, String reason);

}
