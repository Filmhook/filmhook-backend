package com.annular.filmhook.webmodel;

import com.annular.filmhook.model.AuditionCompanyDetails.VerificationStatus;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditionRowDTO {

    private Integer id;
    private String name;
    private String reviewedOn;
    private String status;

    private String companyName;
    private String companyType;
    private Boolean govtVerified;
    private VerificationStatus verificationStatus; 
}
