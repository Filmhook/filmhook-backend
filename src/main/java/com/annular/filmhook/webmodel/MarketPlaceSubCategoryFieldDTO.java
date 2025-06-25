package com.annular.filmhook.webmodel;


import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketPlaceSubCategoryFieldDTO {
    private Integer id;
    private String fieldKey;
//    private String label;
    private String type;
    private boolean required;
    private String section;
    private List<String> options;
    private Integer subCategoryId; 
}

