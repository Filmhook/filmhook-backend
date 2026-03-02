package com.annular.filmhook.model;
import java.time.LocalDateTime;

import javax.persistence.*;

import lombok.*;



@Entity
@Table(name = "user_verification_attempt")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVerificationAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationType verificationType;
    @Builder.Default
    private Integer attemptCount = 0;
    @Builder.Default
    private Integer failedDays = 0;

    private LocalDateTime attemptDate;
    @Builder.Default
    private Boolean locked = false;

}