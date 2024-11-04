package com.annular.filmhook.webmodel;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDetailsDTO {
	
    private Integer userId;
    private String userName;
    private String profilePic;

}
