package com.annular.filmhook.webmodel;


import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class BankDetailsDTO {
	private Long id;
	    private String beneficiaryName;
	    private String mobileNumber;
	    private String accountNumber;
	    private String confirmAccountNumber;
	    private String ifscCode;
   
}