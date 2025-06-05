package com.annular.filmhook.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="shooting_location_subcategory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ShootingLocationSubcategory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name="subcategory_name")
	private String name;

	@Column(name="subcategory_description")
	private String description;

	@ManyToOne
	@JoinColumn(name = "category_id")
	@JsonBackReference
	private ShootingLocationCategory category;

	@OneToMany(mappedBy = "subcategory", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ShootingLocationSubcategorySelection> subcategorySelections;

	//	    private Boolean status;
	//	    private Integer createdBy;
	//	    @CreationTimestamp
	//	    private Date createdOn;
	//	    private Integer updatedBy;
	//	    @UpdateTimestamp
	//	    private Date updatedOn;


}
