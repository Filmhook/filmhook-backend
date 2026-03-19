package com.annular.filmhook.webmodel;




import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShootingLocationRowDTO {

	   private Integer id;
	    private String owner;
	    private String property;
	    private String category;
	    private String propertyType;
	    private String userType;
	    private String location;
	    private String reviewedOn;
	    private String status; 
}