package com.github.skienex.monopoly.utils.scanner;

import java.util.Scanner;

public class TextInput {
    public static String input(String prefix) {
        Scanner scanner = new Scanner(System.in); // Create a Scanner object
        System.out.print(prefix);
        String input = scanner.nextLine();

        return input;
    }

    public static String inputWithoutText() {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        return input;
    }
}
