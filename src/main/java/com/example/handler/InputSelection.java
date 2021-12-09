package com.example.handler;

import java.util.Scanner;

public class InputSelection {
    public static void authentication(Scanner scanner) {
        System.out.print("\n\nSelect an option: \n");
        System.out.print("1. Register \n");
        System.out.print("2. Login \n");
        System.out.print("3. Quit \n");

        final String userInput = scanner.nextLine();

        switch (userInput) {
            case "1":
                System.out.print("Registration selected \n");
                InputAuthentication.register(scanner);
                break;
            case "2":
                System.out.print("Login selected \n");
                InputAuthentication.auth(scanner);
                operation(scanner);
                break;
            case "3":
                System.exit(0);
            default:
                break;
        }
    }

    public static void operation(Scanner scanner) {
        System.out.print("\n\nSelect an option: \n");
        System.out.print("1. Query \n");
        System.out.print("2. Export \n");
        System.out.print("3. Reverse Engineering \n");
        System.out.print("4. Analytics \n");

        final String userInput = scanner.nextLine();

        switch (userInput) {
            case "1":
                System.out.print("Query selected \n");
                InputOperation.query(scanner);
                break;
            case "2":
                System.out.print("Export selected \n");
                InputOperation.exportData(scanner);
                break;
            case "3":
                System.out.print("Reverse Engineering selected \n");
                InputOperation.generateERD(scanner);
            case "4":
                System.exit(0);
            default:
                break;
        }
    }
}
