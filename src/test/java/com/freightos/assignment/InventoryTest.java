package com.freightos.assignment;

import org.junit.Test;

import static org.junit.Assert.*;

public class InventoryTest {
    Inventory snackInventory = new Inventory();

    @Test
    public void isSnackAvailableTest() {
        for (int i = 0; i < 24; i++) {
            if (i == 9) {
                assertFalse(snackInventory.isSnackAvailable(i));
            } else {
                assertTrue(snackInventory.isSnackAvailable(i));
            }
        }
    }

    @Test
    public void getItemTest() {
        assertTrue(snackInventory.getItem(0).getName().equals("Twix"));
        assertTrue(snackInventory.getItem(0).getQuantity() == 5);
        assertTrue(snackInventory.getItem(0).getPrice() == 2.20);

        assertTrue(snackInventory.getItem(1).getName().equals("KitKat"));
        assertTrue(snackInventory.getItem(1).getQuantity() == 5);
        assertTrue(snackInventory.getItem(1).getPrice() == 2.0);

        assertTrue(snackInventory.getItem(24).getName().equals("Lays"));
        assertTrue(snackInventory.getItem(24).getQuantity() == 5);
        assertTrue(snackInventory.getItem(24).getPrice() == 1.60);
    }

    @Test
    public void decrementQuantityTest() {
        snackInventory.dispenseSnack(0);
        assertTrue(snackInventory.getItem(0).getQuantity() == 4);
    }

}