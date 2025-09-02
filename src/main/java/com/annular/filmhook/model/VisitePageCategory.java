package com.annular.filmhook.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@Entity
@Table(name = "visit_page_category") 
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class VisitePageCategory {
	   @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "category_id")
	    private Integer categoryId;

	    @Column(name = "category_name", nullable = false)
	    private String categoryName;
	    
	    @Column(name = "visitType")
	    private String visitType;

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

	    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	    @JsonManagedReference
	    private List<VisitPage> visitPages;
}
