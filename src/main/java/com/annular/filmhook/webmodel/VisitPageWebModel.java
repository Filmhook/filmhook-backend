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

    private Integer pageId;
    private String pageData;
    private String officialSubCategory;

    private Integer detailId;
    private String detailTitle;
    private String detailDescription;
    private String detailMediaUrl;
}
