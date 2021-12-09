package com.example.handler;

import com.example.models.context.LogContext;
import com.example.models.User;
import com.example.models.enums.Permission;
import com.example.services.UserService;
import com.example.services.logs.GeneralLogService;
import com.example.services.logs.LogService;
import com.example.util.StringUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class InputAuthentication {

    public static void auth(Scanner scanner) {
        LOGIN: do {
            try {
                System.out.print("USER> ");
                login(scanner);
                break;
            } catch (Exception e) {
                System.out.println("Invalid user");
                continue LOGIN;
            }
        } while (true);
    }

    public static void register(Scanner scanner) {
        UserService userService = new UserService();
        Map<String, String> questions = userService.getQuestions();
        Map<String, String> answers = new HashMap<>();

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

        for (Map.Entry<String, String> question : questions.entrySet()) {
            System.out.println(question.getValue());
            String answer = scanner.nextLine();
            answers.put(question.getKey(), answer);
        }

        User user = User.builder()
                .permission(Permission.ADMIN)
                .username(username)
                .password(password)
                .answers(answers)
                .build();

        if (userService.register(user)) {
            System.out.print("Registration Successful, Please login \n");
        }
    }

    public static void login(Scanner scanner) {
        LogService generalLogService;
        UserService userService = new UserService();
        Map<String, String> questions = userService.getQuestions();

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
            User databaseUser = optional.get();
            for (Map.Entry<String, String> question : databaseUser.getAnswers().entrySet()) {
                System.out.println(questions.get(question.getKey()));
                String answer = scanner.nextLine();
                if (answer.equals(question.getValue())) {
                    break;
                } else {
                    throw new IllegalArgumentException("Invalid Answer");
                }
            }
            LogContext.setUser(optional.get());
            System.out.print("Login Successful \n");
        }
    }
}
