
package com.annular.filmhook.model;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.*;

@Entity
@Table(name = "shooting_payment_details")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShootingLocationPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Integer paymentId;

    @Column(name = "txnid", nullable = false, unique = true)
    private String txnid;

    @Column(name = "amount", nullable = false)
    private String amount;

    @Column(name = "productinfo")
    private String productinfo;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "email")
    private String email;

    @Column(name = "status")
    private String status; 

    @Column(name = "reason")
    private String reason;

    @Column(name = "payment_hash")
    private String paymentHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private ShootingLocationBooking booking;
    
    private String phone;

    @CreationTimestamp
    @Column(name = "created_on", updatable = false)
    private LocalDateTime createdOn;

    @UpdateTimestamp
    @Column(name = "updated_on")
    private Date updatedOn;
}
