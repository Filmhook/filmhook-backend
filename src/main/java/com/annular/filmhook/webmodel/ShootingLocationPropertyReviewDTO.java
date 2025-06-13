package com.annular.filmhook.webmodel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShootingLocationPropertyReviewDTO {
	private Integer propertyId;
    private Integer userId;
    private int rating;
    private String reviewText;
    private String userName;
}
