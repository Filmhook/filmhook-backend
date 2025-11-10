package com.annular.filmhook.webmodel;

import java.util.List;

import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditionUserCompanyAccessRequestDTO {
    private Integer ownerId;
    private Integer companyId;
    private List<AuditionUserAccessDTO> userAccessList;
}