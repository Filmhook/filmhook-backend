	package com.annular.filmhook.webmodel;
	
	import java.time.LocalDateTime;
	import java.util.List;
	
	import org.springframework.web.multipart.MultipartFile;
	
	import com.annular.filmhook.model.AuditionCompanyDetails.VerificationStatus;
	
	import lombok.*;
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public class AuditionCompanyDetailsDTO {
		private Integer id;
	    private String companyName;
	    private String location;
	    private String companyType;
	    private List<MultipartFile> logoFiles;
	    private boolean gstRegistered;
	    private boolean businessCertificate;
	    private String businessCertificateNumber;
	    private String gstNumber;
	    private String state;
	    private Integer userId;
	    private String houseNumber;
	    private String landMark;
	    private String pinCode;
	    private boolean govtVerified;
	    private String govtVerificationLink;
	    private String accessCode; 
	    private VerificationStatus verificationStatus;
	    private Boolean status;
	    private Integer createdBy;
	    private List<FileOutputWebModel> logoFilesOutput;
	    private Integer updatedBy;
	    private LocalDateTime createdDate;
	    private LocalDateTime updatedDate;
	}
	
