package com.annular.filmhook.webmodel;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyAvailabilityDTO {
    private Integer propertyId;
    private LocalDate startDate;
    private LocalDate endDate;
}
