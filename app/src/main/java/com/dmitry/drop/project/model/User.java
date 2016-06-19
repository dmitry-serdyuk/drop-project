package com.dmitry.drop.project.model;


import java.util.List;

/**
 * Created by Laptop on 22/05/2016.
 */
public class User {
    String username;
    String password;

    List<Post> posts;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
