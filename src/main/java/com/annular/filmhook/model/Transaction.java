package com.annular.filmhook.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.annular.filmhook.enums.PaymentMode;
import com.annular.filmhook.enums.TransactionStatus;
import com.annular.filmhook.enums.TransactionType;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "transactions")
@Getter @Setter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String txnId; // Gateway txn id

    @Enumerated(EnumType.STRING)
    private TransactionType type; 
    // AUDITION, PROMOTE, BOOKING, WALLET_TOPUP, REFUND

    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode; 
    // PAYU, WALLET

    @Enumerated(EnumType.STRING)
    private TransactionStatus status; 
    // PENDING, SUCCESS, FAILED, REFUNDED

    private Integer referenceId; 
    // promoteId / auditionId / bookingId

    private BigDecimal amount;

    private BigDecimal platformFee;
    private BigDecimal gstAmount;
    private BigDecimal netAmount;

    @Column(columnDefinition = "TEXT")
    private String gatewayResponse;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        status = TransactionStatus.PENDING;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
