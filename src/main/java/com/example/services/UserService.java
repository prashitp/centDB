package com.example.services;

import com.example.models.User;
import com.example.services.accessor.UserAccessor;
import com.example.services.accessor.UserAccessorImpl;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Map;
import java.util.Optional;

public class UserService {

    private static SecretKey secretKey = new SecretKeySpec(System.getenv("SECRET_KEY").getBytes(), "AES");

    private UserAccessor userAccessor;

    private EncryptionService encryptionService;

    public UserService() {
        this.encryptionService = new EncryptionService();
        this.userAccessor = new UserAccessorImpl();
    }

    public Boolean register(User user) {
        String encryptedPassword = encryptionService.encrypt(user.getPassword(), secretKey);
        user.setPassword(encryptedPassword);
        return userAccessor.save(user);
    }

    public Optional<User> login(User user) {
        Optional<User> optional = userAccessor.get(user);
        if (optional.isPresent()) {
            User databaseUser = optional.get();
            String decryptedPassword = encryptionService.decrypt(databaseUser.getPassword(), secretKey);
            if (decryptedPassword.equals(user.getPassword())) {
                return Optional.of(databaseUser);
            }
        }
        return Optional.empty();
    }

    public Boolean isPresent(String username) {
        return userAccessor.get(User.builder()
                .username(username)
                .build()).isPresent();
    }

    public Map<String, String> getQuestions() {
        return userAccessor.getQuestions();
    }
}
