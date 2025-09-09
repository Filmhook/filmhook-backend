package com.annular.filmhook.webmodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilmProfessionResponseDTO {
    private Integer id;
    private String professionName;
    private String iconFilePath;
    private Long count;  // total counts across all subProfessions
}
