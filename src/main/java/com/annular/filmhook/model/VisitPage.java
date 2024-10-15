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
@Table(name = "visitPage")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class VisitPage {
	
	   @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "visitPageId")
	    private Integer visitPageId;

	    @Column(name = "status")
	    private boolean status;

	    @Column(name = "created_by")
	    private Integer createdBy;

	    @CreationTimestamp
	    @Column(name = "created_on")
	    private Date createdOn;

	    @Column(name = "updated_by")
	    private Integer updatedBy;

	    @CreationTimestamp
	    @Column(name = "updated_on")
	    private Date updatedOn;
	    
	    @Column(name ="data")
	    private String data;
	    
	    @Column(name = "visitType")
	    private String visitType;


}
