package com.annular.filmhook.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "HelpAndSupport")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class HelpAndSupport {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "help_support_id")
	    private Integer helpAndSupportId;
	    
	    @Column(name = "userId")
	    private Integer userId;
	    
	    @Column(name = "help_and_support_is_active")
	    private Boolean helpAndSupportIsActive;

	    @Column(name = "help_and_support_created_by")
	    private Integer helpAndSupportCreatedBy;

	    @Column(name = "help_and_support_updated_by")
	    private Integer helpAndSupportUpdatedBy;

	    @CreationTimestamp
	    @Column(name = "help_and_support_created_on")
	    private Date helpAndSupportCreatedOn;

	    @CreationTimestamp
	    @Column(name = "help_and_support_updated_on")
	    private Date helpAndSupportUpdatedOn;
	    
	    @Column(name = "message")
	    private String message;

	    @Column(name = "subject")
	    private String subject;
	    
        @Column(name = "receipentEmail")
        private String receipentEmail;
}
