package com.example.services.input;

import com.example.models.User;
import com.example.persistence.UserDao;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import java.util.Objects;

public class UserService {

    private static SecretKey secretKey = new SecretKeySpec(System.getenv("SECRET_KEY").getBytes(), "AES");

    @Inject
    private UserDao userDao;

    @Inject
    private EncryptionService encryptionService;

    public UserService() {
    }

    public UserService(UserDao userDao, EncryptionService encryptionService) {
        this.userDao = userDao;
        this.encryptionService = encryptionService;
    }

    public Boolean register(String username, String password) {
        String encryptedPassword = encryptionService.encrypt(password, secretKey);
        return true;
    }

    public User login(String username, String password) {
        String decryptedPassword = encryptionService.decrypt(password, secretKey);
        return User.builder().build();
    }

    public Boolean isPresent(String username) {
        return userDao.get(username);
    }

    public Boolean isValid(User user) {
        return Objects.nonNull(userDao.get(user));
    }
}
