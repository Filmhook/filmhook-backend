package com.annular.filmhook.model;



import java.time.LocalDateTime;

import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "audition_company_roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditionUserCompanyRole {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false) // The company owner
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private AuditionCompanyDetails company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_user_id", nullable = false) // The user who will get access
    private User assignedUser;

    private String designation;

    @Column(unique = true, nullable = false)
    private String accessKey;

    private Boolean status; // ACTIVE, INACTIVE, REVOKED

    private Integer createdBy;
    private LocalDateTime createdDate;
    private Integer updatedBy;
    private LocalDateTime updatedDate;
    
    private String filmHookCode;

}

