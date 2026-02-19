package com.annular.filmhook.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.*;


import com.annular.filmhook.enums.WalletTxnType;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "wallet_transactions")
@Getter @Setter
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Wallet wallet;

    @ManyToOne
    private Transaction transaction;

    @Enumerated(EnumType.STRING)
    private WalletTxnType type; // CREDIT / DEBIT

    private BigDecimal amount;

    private BigDecimal balanceAfter;

    private String description;

    private LocalDateTime createdAt = LocalDateTime.now();
}
