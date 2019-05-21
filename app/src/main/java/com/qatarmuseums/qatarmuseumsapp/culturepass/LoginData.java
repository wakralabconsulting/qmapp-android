package com.qatarmuseums.qatarmuseumsapp.culturepass;

public class LoginData {
    private String name, pass;

    public LoginData(String username, String password) {
        this.name = username;
        this.pass = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
