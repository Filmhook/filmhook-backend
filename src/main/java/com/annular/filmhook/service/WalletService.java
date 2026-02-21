package com.annular.filmhook.service;

import java.math.BigDecimal;

import com.annular.filmhook.model.Transaction;
import com.annular.filmhook.model.User;
import com.annular.filmhook.model.Wallet;


public interface WalletService {

    Wallet getOrCreateWallet(User user);

    void credit(User user, BigDecimal amount, Transaction txn, String description);

    void debit(User user, BigDecimal amount, Transaction txn, String description);

    BigDecimal getBalance(User user);
}
