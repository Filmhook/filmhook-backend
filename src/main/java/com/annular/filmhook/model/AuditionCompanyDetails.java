	package com.annular.filmhook.model;
	
	import java.time.LocalDateTime;
	
	import javax.persistence.*;
	import lombok.*;
	
	@Entity
	@Table(name = "auditions_companies")
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public class AuditionCompanyDetails {
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;
	    
	    @Column(name = "company_name")
	    private String companyName;
	    
	    @Column(name = "location")
	    private String location;
	    
	    @Column(name = "company_type")
	    private String companyType;
	    
	    @Column(name = "logo_url")
	    private String logoUrl;
	    
	    @Column(name = "gst_registered")
	    private boolean gstRegistered;
	    
	    @Column(name = "business_certificate")
	    private boolean businessCertificate;
	    
	    @Column(name = "business_certificate_number")
	    private String businessCertificateNumber;
	    
	    @Column(name = "gst_number")
	    private String gstNumber;
	
	    
	    @Column(name = "state")
	    private String state;
	    
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "user_id", nullable = false)
	    private User user;
	    
	    @Column(name = "house_number")
	    private String houseNumber;
	    
	    @Column(name = "land_mark")
	    private String landMark;
	    
	    @Column(name = "pin_code")
	    private String pinCode;
	    
	    @Column(name = "govt_verified")
	    private boolean govtVerified;
	    
	    @Column(name = "govt_verification_link")
	    private String govtVerificationLink;
	
	    @Column(name = "access_code", unique = true)
	    private String accessCode; 
	
	    @Enumerated(EnumType.STRING)
	    @Column(name = "verification_status")
	    private VerificationStatus verificationStatus;
	    
	    @Builder.Default
	    private Boolean deleted = false;
	    
	    private Boolean status;
	    private Integer createdBy;
	    private Integer updatedBy;
	    private LocalDateTime createdDate;
	    private LocalDateTime updatedDate;
	    
	    public enum VerificationStatus {
	        PENDING, SUCCESS, FAILED
	    }
	}
	
