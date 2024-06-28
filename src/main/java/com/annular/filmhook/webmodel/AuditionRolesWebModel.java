package com.annular.filmhook.webmodel;

import java.sql.Date;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditionRolesWebModel {

    private Integer auditionRoleId;

    private String auditionRoleDesc;

    private Integer auditionReferenceId;

    private boolean auditionRoleIsactive;

    private Integer auditionRoleCreatedBy;

    private Date auditionRoleCreatedOn;

    private Integer auditionRoleUpdatedBy;

    private Date auditionRoleUpdatedOn;

}
