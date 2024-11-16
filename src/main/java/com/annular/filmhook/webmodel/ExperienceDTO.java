package com.annular.filmhook.webmodel;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExperienceDTO {

	    private Integer userId;
	    private Integer leastStartingYear;
	    private Integer maximumEndingYear;
	    private Integer totalExperienceYears;
	
}
