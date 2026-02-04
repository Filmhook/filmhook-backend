package com.annular.filmhook.webmodel;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitPageWebModel {

    private Integer categoryId;
    private String categoryName;
    private String description;

    private Integer pageId;
    private String pageData;
    private String officialSubCategory;

    private Integer detailId;
    private String detailTitle;

}
