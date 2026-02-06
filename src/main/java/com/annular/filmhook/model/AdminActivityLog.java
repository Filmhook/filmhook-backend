package com.annular.filmhook.model;

import java.time.LocalDateTime;
import javax.persistence.*;

import lombok.*;

@Entity
@Table(name = "admin_activity_log")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AdminActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Admin who performed the action
    @Column(name = "admin_id", nullable = false)
    private Integer adminId;

    // JPA Relationship to User table (Optional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", referencedColumnName = "id", 
                insertable = false, updatable = false)
    private User admin;

    // What action happened
    @Column(name = "action_type", nullable = false)
    private String actionType;

    // Module: INDUSTRY_USER, SHOOTING_LOCATION, REPORT, etc.
    @Column(name = "target_type", nullable = false)
    private String targetType;

    // UserId or LocationId or ReportId
    @Column(name = "target_id", nullable = false)
    private Integer targetId;

    // Timestamp
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;
}
