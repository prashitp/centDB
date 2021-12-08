package com.example.handler;

import com.example.models.context.LogContext;
import com.example.models.User;
import com.example.services.UserService;
import com.example.util.StringUtil;

import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

public class InputAuthentication {

    public static void register(Scanner scanner) {
        UserService userService = new UserService();

        System.out.print("Enter Username: \n");
        final String username = scanner.nextLine();

        System.out.print("Enter Password: \n");
        final String password = scanner.nextLine();
        if (!StringUtil.isAlphaNumeric(password)) {
            System.out.print("Invalid password \n");
            return;
        }

        System.out.print("Re-Enter Password: \n");
        final String repeatPassword = scanner.nextLine();
        if (!password.equals(repeatPassword)) {
            System.out.print("Invalid password \n");
            return;
        }

        User user = User.builder()
                .username(username)
                .password(password)
                .build();

        if (userService.register(user)) {
            System.out.print("Registration Successful \n");
        }
    }

    public static void login(Scanner scanner) {
        UserService userService = new UserService();

        System.out.print("Enter Username: \n");
        final String username = scanner.nextLine();

        System.out.print("Enter Password: \n");
        final String password = scanner.nextLine();

        User user = User.builder()
                .username(username)
                .password(password)
                .build();

        Optional<User> optional = userService.login(user);

        if (optional.isPresent()) {
            LogContext.setUser(optional.get());
            System.out.print("Login Successful \n");
        }
    }
}
