package com.annular.filmhook.service.impl;



import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.annular.filmhook.enums.WalletTxnType;
import com.annular.filmhook.model.Transaction;
import com.annular.filmhook.model.User;
import com.annular.filmhook.model.Wallet;
import com.annular.filmhook.model.WalletTransaction;
import com.annular.filmhook.repository.WalletRepository;
import com.annular.filmhook.repository.WalletTransactionRepository;
import com.annular.filmhook.service.WalletService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTxnRepository;

    @Override
    public Wallet getOrCreateWallet(User user) {
        return walletRepository.findByUser(user)
                .orElseGet(() -> {
                    Wallet wallet = new Wallet();
                    wallet.setUser(user);
                    wallet.setBalance(BigDecimal.ZERO);
                    return walletRepository.save(wallet);
                });
    }

    @Override
    @Transactional
    public void credit(User user, BigDecimal amount, Transaction txn, String description) {

        Wallet wallet = getOrCreateWallet(user);

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        WalletTransaction wTxn = new WalletTransaction();
        wTxn.setWallet(wallet);
        wTxn.setTransaction(txn);
        wTxn.setType(WalletTxnType.CREDIT);
        wTxn.setAmount(amount);
        wTxn.setBalanceAfter(wallet.getBalance());
        wTxn.setDescription(description);

        walletTxnRepository.save(wTxn);
    }

    @Override
    @Transactional
    public void debit(User user, BigDecimal amount, Transaction txn, String description) {

        Wallet wallet = getOrCreateWallet(user);

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient Wallet Balance");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        WalletTransaction wTxn = new WalletTransaction();
        wTxn.setWallet(wallet);
        wTxn.setTransaction(txn);
        wTxn.setType(WalletTxnType.DEBIT);
        wTxn.setAmount(amount);
        wTxn.setBalanceAfter(wallet.getBalance());
        wTxn.setDescription(description);

        walletTxnRepository.save(wTxn);
    }

    @Override
    public BigDecimal getBalance(User user) {
        return getOrCreateWallet(user).getBalance();
    }
}
