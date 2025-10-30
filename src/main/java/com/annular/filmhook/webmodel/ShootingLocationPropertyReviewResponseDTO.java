package com.annular.filmhook.webmodel;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShootingLocationPropertyReviewResponseDTO {
      private double averageRating;
    private long totalReviews;
    private double fiveStarPercentage;
    private double fourStarPercentage;
    private double threeStarPercentage;
    private double twoStarPercentage;
    private double oneStarPercentage;
    private List<ShootingLocationPropertyReviewDTO> reviews;

}
