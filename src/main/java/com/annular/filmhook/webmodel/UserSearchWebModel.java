package com.annular.filmhook.webmodel;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchWebModel {

    // Input variables
    private List<Integer> countryIds;
    private List<Integer> industryIds;
    private Integer platformId;
    private List<Integer> professionIds;
    private List<Integer> subProfessionIds;

    // Output variables
    private List<IndustryWebModel> industryList;
    private List<ProfessionWebModel> professionList;
    private List<SubProfessionWebModel> subProfessionList;

    private List<UserWebModel> userList; // Final Users list
    Map<String, List<Map<String, Object>>> professionUsersMap;

}
