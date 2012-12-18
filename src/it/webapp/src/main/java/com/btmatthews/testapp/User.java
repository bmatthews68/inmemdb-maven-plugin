package com.btmatthews.testapp;

public class User {

    private String username;

    private String password;

    private String name;

    public User(final String username,
                final String password,
                final String name) {
        this.username = username;
        this.password = password;
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }
}
