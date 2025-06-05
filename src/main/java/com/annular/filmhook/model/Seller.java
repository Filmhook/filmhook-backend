package com.annular.filmhook.model;
import javax.persistence.*;

@Entity
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String middleName;
    private String lastName;
    private String citizenship;
    private String placeOfBirth;
    private String phone;
    private String email;

    @ManyToOne
    @JoinColumn(name = "location_type_id")
    private LocationType locationType;

    @ManyToOne
    @JoinColumn(name = "sub_location_type_id")
    private SubLocationType subLocationType;

    public ShootingLocationImages getShootingLocationImage() {
		return shootingLocationImage;
	}

	public void setShootingLocationImage(ShootingLocationImages shootingLocationImage) {
		this.shootingLocationImage = shootingLocationImage;
	}

	@ManyToOne
    @JoinColumn(name = "sub_location_type_content_id")
    private SubLocationTypeContent subLocationTypeContent;
    @ManyToOne
    @JoinColumn(name = "shooting_location_id")
    private ShootingLocationImages shootingLocationImage;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getCitizenship() {
		return citizenship;
	}

	public void setCitizenship(String citizenship) {
		this.citizenship = citizenship;
	}

	public String getPlaceOfBirth() {
		return placeOfBirth;
	}

	public void setPlaceOfBirth(String placeOfBirth) {
		this.placeOfBirth = placeOfBirth;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public LocationType getLocationType() {
		return locationType;
	}

	public void setLocationType(LocationType locationType) {
		this.locationType = locationType;
	}

	public SubLocationType getSubLocationType() {
		return subLocationType;
	}

	public void setSubLocationType(SubLocationType subLocationType) {
		this.subLocationType = subLocationType;
	}

	public SubLocationTypeContent getSubLocationTypeContent() {
		return subLocationTypeContent;
	}

	public void setSubLocationTypeContent(SubLocationTypeContent subLocationTypeContent) {
		this.subLocationTypeContent = subLocationTypeContent;
	}

    // Getters and Setters
    
}