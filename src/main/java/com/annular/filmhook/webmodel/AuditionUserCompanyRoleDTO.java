package com.annular.filmhook.webmodel;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditionUserCompanyRoleDTO {
   
    private Integer id;
    private Integer ownerId;
    private Integer companyId;
    private Integer assignedUserId;
    private String designation;
    private String accessKey;
    private Boolean status;
    private LocalDateTime createdDate;
    private String filmHookCode;
    private Boolean isOwner;
    private String assignedUserName;
    private String assignedUserEmail;
    private String ownerName;
    private String ownerEmail;
    private String assignedUserProfilePicture;
}
