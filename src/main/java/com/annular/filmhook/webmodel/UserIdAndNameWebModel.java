package com.annular.filmhook.webmodel;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserIdAndNameWebModel {

    private Integer userId;
    private String userName;
    private String profilePicUrl;

}
