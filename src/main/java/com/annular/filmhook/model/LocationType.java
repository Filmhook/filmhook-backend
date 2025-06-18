package com.annular.filmhook.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class LocationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Indoor / Outdoor

    @OneToMany(mappedBy = "locationType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   
    private List<SubLocationType> subLocationTypes;
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<SubLocationType> getSubLocationTypes() {
		return subLocationTypes;
	}

	public void setSubLocationTypes(List<SubLocationType> subLocationTypes) {
		this.subLocationTypes = subLocationTypes;
	}

    // Getters and Setters
    
}
