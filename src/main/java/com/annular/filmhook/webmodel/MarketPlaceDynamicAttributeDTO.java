package com.annular.filmhook.webmodel;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketPlaceDynamicAttributeDTO {
    private String fieldKey;
    private String value;
    private String section;
}