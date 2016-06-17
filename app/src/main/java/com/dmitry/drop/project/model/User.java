package com.dmitry.drop.project.model;

import com.orm.SugarRecord;

import java.util.List;

/**
 * Created by Laptop on 22/05/2016.
 */
public class User extends SugarRecord {
    String username;
    String password;

    List<Post> posts;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
