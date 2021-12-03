package com.example;

import com.example.models.User;
import com.example.services.input.UserService;
import com.example.util.Validator;
import java.util.Objects;
import java.util.Scanner;

public class Application {

    public static void main(String[] args) {

        System.out.print("\n" +
                "░█▀▀█ ░█▀▀▀ ░█▄─░█ ▀▀█▀▀ 　 ░█▀▀▄ ─█▀▀█ ▀▀█▀▀ ─█▀▀█ ░█▀▀█ ─█▀▀█ ░█▀▀▀█ ░█▀▀▀ \n" +
                "░█─── ░█▀▀▀ ░█░█░█ ─░█── 　 ░█─░█ ░█▄▄█ ─░█── ░█▄▄█ ░█▀▀▄ ░█▄▄█ ─▀▀▀▄▄ ░█▀▀▀ \n" +
                "░█▄▄█ ░█▄▄▄ ░█──▀█ ─░█── 　 ░█▄▄▀ ░█─░█ ─░█── ░█─░█ ░█▄▄█ ░█─░█ ░█▄▄▄█ ░█▄▄▄");

        final Scanner scanner = new Scanner(System.in);

        while (true) {
            select(scanner);
        }

    }

    public static void register(Scanner scanner) {
        UserService userService = new UserService();

        System.out.print("Enter Username: \n");
        final String username = scanner.nextLine();

        System.out.print("Enter Password: \n");
        final String password = scanner.nextLine();
        if (!Validator.isAlphaNumeric(password)) {
            System.out.print("Invalid password \n");
            return;
        }

        System.out.print("Re-Enter Password: \n");
        final String repeatPassword = scanner.nextLine();
        if (!password.equals(repeatPassword)) {
            System.out.print("Invalid password \n");
            return;
        }

        if (userService.register(username, password)) {
            System.out.print("Registration Successful \n");
        }
    }

    public static void login(Scanner scanner) {
        UserService userService = new UserService();

        System.out.print("Enter Username: \n");
        final String username = scanner.nextLine();

        System.out.print("Enter Password: \n");
        final String password = scanner.nextLine();

        User user = userService.login(username, password);
        if (Objects.nonNull(user)) {
            System.out.print("Login Successful \n");
        }
    }

    public static void query(Scanner scanner) {

    }

    public static void select(Scanner scanner) {
        System.out.print("Select an option: \n");
        System.out.print("1. Register \n");
        System.out.print("2. Login \n");
        System.out.print("3. Quit \n");

        final String userInput = scanner.nextLine();

        switch (userInput) {
            case "1":
                System.out.print("Registration selected \n");
                register(scanner);
                break;
            case "2":
                System.out.print("Login selected \n");
                break;
            case "3":
                System.exit(0);
            default:
                break;
        }
    }
}
