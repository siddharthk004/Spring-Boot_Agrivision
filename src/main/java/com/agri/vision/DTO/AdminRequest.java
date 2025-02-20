package com.agri.vision.DTO;

public class AdminRequest {

    public String username;
    public String password;

    public AdminRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AdminRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
