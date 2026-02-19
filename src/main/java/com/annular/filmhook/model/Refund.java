package com.annular.filmhook.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.*;

import com.annular.filmhook.enums.RefundStatus;

import lombok.*;

@Entity
@Table(name = "refunds")
@Getter @Setter
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    private Transaction originalTransaction;

    private String refundTxnId;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private RefundStatus status; // INITIATED, SUCCESS, FAILED

    private String reason;

    private LocalDateTime createdAt = LocalDateTime.now();
}
