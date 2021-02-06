package com.freightos.assignment;

import java.util.Scanner;

/**
 * This is a main class of SnackVendingMachine
 *
 * @author Malak
 */
public class SnackVendingMachineApplication {
    private static final String INCORRECT_CHOICE_NUMBER_MESSAGE = "Please enter a valid snack index";
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        SnackVendingMachine snackVendingMachine = new SnackVendingMachine();
        Snack selectedSnack;
        snackVendingMachine.getKeypad().addScanner(scanner);
        while (true) {
            System.out.println("\nWelcome to our SnackVendingMachine \uD83D\uDE04\uD83D\uDE04");
            snackVendingMachine.printItemList();
            int userChoice = 0;
            int exceptionFlag = 0;
            try {
                userChoice = Integer.parseInt(snackVendingMachine.getKeypad().read());
            } catch (NumberFormatException e) {
                exceptionFlag = 1;
            }
            if (exceptionFlag == 1 || userChoice < 0 || userChoice > 24) {
                System.err.println(INCORRECT_CHOICE_NUMBER_MESSAGE);
                continue;
            }
            selectedSnack = snackVendingMachine.selectItemAtSlot(userChoice);
            if (selectedSnack == null) {
                System.err.println("Sorry! sold out,please choose another one");
            } else {
                System.out.println("Snack is avaliable : " + selectedSnack.getName() + " with price " + selectedSnack.getPrice());
                snackVendingMachine.execute(userChoice, selectedSnack.getPrice());
            }
        }
    }
}
