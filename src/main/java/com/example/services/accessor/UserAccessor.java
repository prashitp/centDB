package com.example.services.accessor;

import com.example.models.User;

import java.util.Map;
import java.util.Optional;

public interface UserAccessor {

    Boolean save(User user);

    Optional<User> get(User user);

    Map<String, String> getQuestions();
}
