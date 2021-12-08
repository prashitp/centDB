package com.example.services;

import com.example.models.User;
import com.example.services.accessor.UserAccessor;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Objects;
import java.util.Optional;

public class UserService {

    private static SecretKey secretKey = new SecretKeySpec(System.getenv("SECRET_KEY").getBytes(), "AES");

    private UserAccessor userAccessor;

    private EncryptionService encryptionService;

    public UserService() {
        this.encryptionService = new EncryptionService();
    }

    public UserService(UserAccessor userAccessor, EncryptionService encryptionService) {
        this.userAccessor = userAccessor;
        this.encryptionService = encryptionService;
    }

    public Boolean register(User user) {
        String encryptedPassword = encryptionService.encrypt(user.getPassword(), secretKey);
        user.setPassword(encryptedPassword);
        return userAccessor.save(user);
    }

    public Optional<User> login(User user) {
        String encryptedPassword = encryptionService.encrypt(user.getPassword(), secretKey);
        user.setPassword(encryptedPassword);
        return userAccessor.get(user);
    }

    public Boolean isPresent(String username) {
        return userAccessor.isPresent(username);
    }

    public Boolean isValid(User user) {
        return Objects.nonNull(userAccessor.get(user));
    }
}
