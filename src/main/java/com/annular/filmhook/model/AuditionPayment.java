package com.annular.filmhook.model;


import java.time.LocalDateTime;
import javax.persistence.*;

import lombok.*;

@Entity
@Table(name = "audition_payment")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditionPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer auditionPaymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private AuditionNewProject project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String txnid;
    private String paymentHash;
    
     // Payment Details
    private Double totalAmount;
    private Integer totalTeamNeeds;
    private Integer selectedDays;

    // Payment Status
    private String paymentStatus;
    private String reason;

    private LocalDateTime successDateTime;
    private LocalDateTime expiryDateTime; 

    // Audit Fields
    private Integer createdBy;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
}

