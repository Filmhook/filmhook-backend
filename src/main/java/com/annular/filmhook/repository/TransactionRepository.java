package com.annular.filmhook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.annular.filmhook.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Integer>{

	Optional<Transaction> findByTxnId(String txnId);

}
