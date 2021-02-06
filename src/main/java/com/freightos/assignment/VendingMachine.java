package com.freightos.assignment;

public abstract class VendingMachine<T> {
    private MoneyController moneyController = new MoneyController();
    private Keypad keypad = new Keypad();

    public Keypad getKeypad() {
        return keypad;
    }

    public MoneyController getMoneyController() {
        return moneyController;
    }

    public abstract void printItemList();

    public abstract T selectItemAtSlot(int index);

    public abstract boolean execute(int userChoice, double price);
}
