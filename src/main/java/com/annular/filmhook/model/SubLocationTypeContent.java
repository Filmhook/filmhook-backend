package com.annular.filmhook.model;


import javax.persistence.*;

@Entity
public class SubLocationTypeContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // e.g. IndoorFilm, OutdoorStage

    @ManyToOne
    @JoinColumn(name = "subLocationTypeId")
    private SubLocationType subLocationType;

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

	public SubLocationType getSubLocationType() {
		return subLocationType;
	}

	public void setSubLocationType(SubLocationType subLocationType) {
		this.subLocationType = subLocationType;
	}

    // Getters and Setters
    
}
