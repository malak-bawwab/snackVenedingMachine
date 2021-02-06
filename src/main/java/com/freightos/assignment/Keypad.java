package com.freightos.assignment;

import java.util.Scanner;

public class Keypad {
    private Scanner scanner;

    public String read() {
        return scanner.nextLine();
    }

    public void addScanner(Scanner scanner) {
        this.scanner = scanner;
    }
}
