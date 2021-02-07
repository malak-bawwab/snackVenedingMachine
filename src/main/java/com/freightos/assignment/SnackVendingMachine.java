package com.freightos.assignment;

public class SnackVendingMachine extends VendingMachine<Snack> {
    //This is the place where snacks are stored.
    private Inventory snackInventory;

    public SnackVendingMachine() {
        snackInventory = new Inventory();
    }

    @Override
    public void printItemList() {
        snackInventory.printSnackList();
    }

    /**
     * This method will return the snack if there is enough from it
     * else it will return null(0 amount of this snack)
     *
     * @param index snack slot index
     * @return selected snack
     */
    @Override
    public Snack selectItemAtSlot(int index) {
        if (snackInventory.isSnackAvailable(index)) {
            return snackInventory.getItem(index);
        }
        return null;
    }

    /**
     * This method will do payment either byCard or byCoin/notes
     * and in case of successful payment,the machine will dispense snack and changes if any exists
     * to customers.
     *
     * @param userChoice selected snack slot index
     * @param price      price of the selected snack
     * @return true or false represent if payment is success or not.
     */
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
