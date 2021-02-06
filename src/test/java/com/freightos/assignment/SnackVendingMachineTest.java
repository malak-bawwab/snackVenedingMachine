package com.freightos.assignment;

import org.junit.Assert;
import org.junit.Test;

public class SnackVendingMachineTest {
    SnackVendingMachine snackVendingMachine = new SnackVendingMachine();

    @Test
    public void selectItemAtSlot() {
        Assert.assertEquals("Twix",snackVendingMachine.selectItemAtSlot(0).getName());
        Assert.assertEquals("KitKat",snackVendingMachine.selectItemAtSlot(1).getName());
        Assert.assertNull(snackVendingMachine.selectItemAtSlot(9));
    }
}