package com.annular.filmhook.webmodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilmSubProfessionResponseDTO {
    private Integer id;
    private String subProfessionName;
    private String professionName;
    private Integer filmProfessionId;
    private String iconFilePath;
    private String shortCharacters;
    private Integer count; 
    
}

