package com.example.test;

public class User {
    private String userName;
    private String email;
    private String userRole;

    public User(String name, String role, String email) {
        this.userName = name;
        this.email = email;
        this.userRole = role;
    }

    public User() {

    }

    public User(User u){
        this.userName = u.userName;
        this.email = u.email;
        this.userRole = u.userRole;

    }

    public String getUserName() {return userName;}
    public String getEmail() {
        return email;
    }
    public String getUserRole() {return userRole;}


}

