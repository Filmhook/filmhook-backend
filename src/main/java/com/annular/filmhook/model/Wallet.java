package com.annular.filmhook.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.*;


import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "wallets")
@Getter @Setter
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    private BigDecimal balance = BigDecimal.ZERO;

    private LocalDateTime updatedAt;

    @PreUpdate
    public void updateTime() {
        updatedAt = LocalDateTime.now();
    }
}
