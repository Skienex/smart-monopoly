package com.github.skienex.monopoly.utils;

import java.util.Random;

public class Dice {
    private Random random = new Random();

    public Roll roll() {
        int a = random.nextInt(6) + 1;
        int b = random.nextInt(6) + 1;
        return new Roll(a, b);
    }

    public record Roll(int firstNumber, int secondNumber) {
        public int value() {
            return firstNumber + secondNumber;
        }

        public boolean pasch() {
            return firstNumber == secondNumber;
        }
    }
}
