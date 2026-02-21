package com.annular.filmhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.annular.filmhook.model.WalletTransaction;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Integer> {

}
