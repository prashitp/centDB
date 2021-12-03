package com.example.persistence;

import com.example.models.User;

public class UserDao {

    public Boolean createOrUpdate(User user) {
        return true;
    }

    public Boolean get(String username) {
        return true;
    }

    public User get(User user) {
        return User.builder().build();
    }
}
