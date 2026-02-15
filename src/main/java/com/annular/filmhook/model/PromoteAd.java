package com.annular.filmhook.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "promote_ads")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromoteAd {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promote_id")
    private Integer promoteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    private Posts post;

    @Column(name = "headline")
    private String headline;
    
    @Column(name = "ad_type", columnDefinition = "TEXT")
    private String adType;


    @Column(name = "promote_description", columnDefinition = "TEXT")
    private String promoteDescription;

    @Column(name = "business_location")
    private String businessLocation;

    @Column(name = "business_type")
    private String businessType;

    @Column(name = "adv_object")
    private String advObject;

    @Column(name = "adv_object_value")
    private String advObjectValue;

    @Column(name = "business_name")
    private String businessName;

    @Column(name = "company_logo")
    private String companyLogo; // S3 URL

    @Column(name = "business_address")
    private String businessAddress; // Document S3 URL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_type_id", referencedColumnName = "detail_id")
    private VisitPageDetails visitType;
    
    

    @Column(name = "budget")
    private Double budget;

    @Column(name = "days")
    private Integer days;

    @Column(name = "target_countries")
    private String targetCountries;

    @Column(name = "reach_min")
    private Long reachMin;

    @Column(name = "reach_max")
    private Long reachMax;

    @Column(name = "payment_status")
    private String paymentStatus; // PENDING, PAID

    @Column(name = "transaction_id")
    private String transactionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private PromoteStatus status = PromoteStatus.NotStarted;

    // NEW PAYMENT FIELDS
    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "total_cost", precision = 10, scale = 2)
    private BigDecimal totalCost;

    @Column(name = "tax_fee")
    private Integer taxFee;

    @Column(name = "cgst")
    private Integer cgst;

    @Column(name = "sgst")
    private Integer sgst;

    @Column(name = "price")
    private Integer price;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on")
    private Date createdOn;
    
    @Column(name = "start_date")
    @CreationTimestamp
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    
    public enum PromoteStatus {
        NotStarted,
        Running,
        Completed
    }
    
    
}
