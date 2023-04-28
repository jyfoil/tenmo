package com.techelevator.tenmo.model;

public class UserInfoDTO {
    private String username;

    public UserInfoDTO(){

    }

    public UserInfoDTO(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
