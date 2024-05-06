package com.annular.filmhook.webmodel;

import lombok.Data;

@Data
public class UserIdAndNameWebModel {
    private Integer userId;
    private String userName;

    // Constructor
    public UserIdAndNameWebModel(Integer userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    // Getters and setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}


