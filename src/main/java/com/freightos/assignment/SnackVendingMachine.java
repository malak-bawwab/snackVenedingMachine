package com.freightos.assignment;

public class SnackVendingMachine extends VendingMachine<Snack> {
    private Inventory snackInventory;

    public SnackVendingMachine() {
        snackInventory = new Inventory();
    }

    @Override
    public void printItemList() {
        snackInventory.printSnackList();
    }

    @Override
    public Snack selectItemAtSlot(int index) {
        if (snackInventory.isSnackAvailable(index)) {
            return snackInventory.getItem(index);
        }
        return null;
    }

    @Override
    public boolean execute(int userChoice, double price) {
        if (doPayment(price)) {
            dispenseSnack(userChoice);
            dispenseChanges();
            return true;
        }
        return false;
    }

    private boolean doPayment(double price) {
        return getMoneyController().doPayment(getKeypad(), price);
    }

    private void dispenseSnack(int index) {
        snackInventory.dispenseSnack(index);
    }

    private void dispenseChanges() {
        getMoneyController().dispenseChange();
    }
}
