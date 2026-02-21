package com.annular.filmhook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.annular.filmhook.model.User;
import com.annular.filmhook.model.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Integer>{

	 Optional<Wallet> findByUser(User user);
	
	

}
