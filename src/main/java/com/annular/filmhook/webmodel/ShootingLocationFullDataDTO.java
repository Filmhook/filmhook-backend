package com.annular.filmhook.webmodel;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShootingLocationFullDataDTO {
    private List<ShootingLocationTypeDTO> types;
    private List<ShootingLocationCategoryDTO> categories;
    private List<ShootingLocationSubcategoryDTO> subcategories;
    private List<ShootingLocationSubcategorySelectionDTO> selections;
    private List<ShootingLocationPropertyDetailsDTO> properties;
    
   
}

