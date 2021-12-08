package com.example;

import com.example.handler.InputSelection;

import java.io.IOException;
import java.util.*;

public class Application {

    public static void main(String[] args) throws IOException {

        System.out.print("\n" +
                "░█▀▀█ ░█▀▀▀ ░█▄─░█ ▀▀█▀▀ 　 ░█▀▀▄ ─█▀▀█ ▀▀█▀▀ ─█▀▀█ ░█▀▀█ ─█▀▀█ ░█▀▀▀█ ░█▀▀▀ \n" +
                "░█─── ░█▀▀▀ ░█░█░█ ─░█── 　 ░█─░█ ░█▄▄█ ─░█── ░█▄▄█ ░█▀▀▄ ░█▄▄█ ─▀▀▀▄▄ ░█▀▀▀ \n" +
                "░█▄▄█ ░█▄▄▄ ░█──▀█ ─░█── 　 ░█▄▄▀ ░█─░█ ─░█── ░█─░█ ░█▄▄█ ░█─░█ ░█▄▄▄█ ░█▄▄▄");

        final Scanner scanner = new Scanner(System.in);

        while (true) {
            InputSelection.authentication(scanner);
        }

    }
}
