package com.annular.filmhook.webmodel;

import lombok.Data;
import javax.validation.constraints.NotNull;
@Data
public class ShootingPropertyMediaRequest {
	  @NotNull
	    private Integer mediaId;

	    @NotNull
	    private Boolean approved;

	    private String reason;    
}
