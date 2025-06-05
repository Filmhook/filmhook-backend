package com.annular.filmhook.model;

import javax.persistence.*;


import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class SubLocationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // e.g. IndoorLights, OutdoorTiles

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locationTypeId")
    @JsonBackReference
    private LocationType locationType;

   

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

	public LocationType getLocationType() {
		return locationType;
	}

	public void setLocationType(LocationType locationType) {
		this.locationType = locationType;
	}

	

    // Getters and Setters
    
}