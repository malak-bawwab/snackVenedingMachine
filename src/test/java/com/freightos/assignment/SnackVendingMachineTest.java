package com.freightos.assignment;

import org.junit.Assert;
import org.junit.Test;

public class SnackVendingMachineTest {
    SnackVendingMachine snackVendingMachine = new SnackVendingMachine();

    @Test
    public void selectItemAtSlot() {
        Assert.assertEquals(snackVendingMachine.selectItemAtSlot(0).getName(), "Twix");
        Assert.assertEquals(snackVendingMachine.selectItemAtSlot(1).getName(), "KitKat");
        Assert.assertNull(snackVendingMachine.selectItemAtSlot(9));
    }
}