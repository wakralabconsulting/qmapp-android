package com.qatarmuseums.qatarmuseumsapp.culturepass;

public class LoginData {
    private String name, pass;

    public LoginData(String username, String password) {
        this.name = username;
        this.pass = password;
    }

    public String getUsername() {
        return name;
    }

    public void setUsername(String username) {
        this.name = username;
    }

    public String getPassword() {
        return pass;
    }

    public void setPassword(String password) {
        this.pass = password;
    }
}
