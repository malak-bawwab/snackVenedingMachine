package com.freightos.assignment;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static org.junit.Assert.*;

public class SnackVendingMachineApplicationTest {

    SnackVendingMachine snackVendingMachine = new SnackVendingMachine();
    private ClassLoader loader = SnackVendingMachineApplicationTest.class.getClassLoader();

    @Test
    public void payWithCardTest() throws FileNotFoundException {
        snackVendingMachine.getKeypad().addScanner(new Scanner(new File(loader.getResource("card_payment.txt").getPath()
        )));
        //success payment with card,valid number,enough blance,
        Snack selectedSnack = snackVendingMachine.selectItemAtSlot(0);
        assertTrue(snackVendingMachine.execute(0, selectedSnack.getPrice()));
        Assert.assertEquals(snackVendingMachine.getMoneyController().getMoneyStore().toString(), "{1.0=15, 0.1=1, 0.2=1, 20.0=2, 50.0=1}");
        Assert.assertEquals(snackVendingMachine.getMoneyController().getTotalChangeMap().size(), 0);
        assertTrue(selectedSnack.getQuantity() == 4);

        //valid number,not enough payment then cancel,credit card
        selectedSnack = snackVendingMachine.selectItemAtSlot(1);
        assertFalse(snackVendingMachine.execute(1, selectedSnack.getPrice()));
        Assert.assertEquals(snackVendingMachine.getMoneyController().getMoneyStore().toString(), "{1.0=15, 0.1=1, 0.2=1, 20.0=2, 50.0=1}");
        Assert.assertEquals(snackVendingMachine.getMoneyController().getTotalChangeMap().size(), 0);
        assertTrue(selectedSnack.getQuantity() == 5);

        //wrong then valid number,enough payment ,credit card
        selectedSnack = snackVendingMachine.selectItemAtSlot(2);
        assertTrue(snackVendingMachine.execute(2, selectedSnack.getPrice()));
        Assert.assertEquals(snackVendingMachine.getMoneyController().getMoneyStore().toString(), "{1.0=15, 0.1=1, 0.2=1, 20.0=2, 50.0=1}");
        Assert.assertEquals(snackVendingMachine.getMoneyController().getTotalChangeMap().size(), 0);
        assertTrue(selectedSnack.getQuantity() == 4);

        //wrong  then cancel,credit card
        selectedSnack = snackVendingMachine.selectItemAtSlot(2);
        assertFalse(snackVendingMachine.execute(2, selectedSnack.getPrice()));
        Assert.assertEquals(snackVendingMachine.getMoneyController().getMoneyStore().toString(), "{1.0=15, 0.1=1, 0.2=1, 20.0=2, 50.0=1}");
        Assert.assertEquals(snackVendingMachine.getMoneyController().getTotalChangeMap().size(), 0);
        assertTrue(selectedSnack.getQuantity() == 4);

        //wrong  then cancel,choose coin
        selectedSnack = snackVendingMachine.selectItemAtSlot(8);
        assertTrue(snackVendingMachine.execute(8, selectedSnack.getPrice()));
        Assert.assertEquals(snackVendingMachine.getMoneyController().getMoneyStore().toString(), "{1.0=18, 20.0=2, 50.0=1}");
        Assert.assertEquals("{0.1=1, 0.2=1}", snackVendingMachine.getMoneyController().getTotalChangeMap().toString());
        assertTrue(selectedSnack.getQuantity() == 4);

        //valid number,not enough payment then coin,credit card
        selectedSnack = snackVendingMachine.selectItemAtSlot(15);
        assertTrue(snackVendingMachine.execute(15, selectedSnack.getPrice()));
        Assert.assertEquals(snackVendingMachine.getMoneyController().getMoneyStore().toString(), "{1.0=19, 20.0=2, 50.0=1}");
        Assert.assertEquals("{1.0=2}", snackVendingMachine.getMoneyController().getTotalChangeMap().toString());
        assertTrue(selectedSnack.getQuantity() == 6);

    }

    //coin test
    @Test
    public void payWithCoinTest() throws FileNotFoundException {
        snackVendingMachine.getKeypad().addScanner(new Scanner(new File(loader.getResource("coin_paymet.txt").getPath()
        )));

        //success payment with coin,no change,all valid money
        Snack selectedSnack = snackVendingMachine.selectItemAtSlot(0);
        assertTrue(snackVendingMachine.execute(0, selectedSnack.getPrice()));
        Assert.assertEquals(snackVendingMachine.getMoneyController().getMoneyStore().toString(), "{1.0=17, 0.1=3, 0.2=1, 20.0=2, 50.0=1}");
        Assert.assertTrue(snackVendingMachine.getMoneyController().getTotalChangeMap().size() == 0);
        assertTrue(selectedSnack.getQuantity() == 4);

        //success payment,no suuficent change,un accepted money
        selectedSnack = snackVendingMachine.selectItemAtSlot(0);
        assertFalse(snackVendingMachine.execute(0, selectedSnack.getPrice()));
        Assert.assertEquals(snackVendingMachine.getMoneyController().getMoneyStore().toString(), "{1.0=17, 0.1=3, 0.2=1, 20.0=2, 50.0=1}");
        Assert.assertTrue(snackVendingMachine.getMoneyController().getTotalChangeMap().size() == 0);
        assertTrue(selectedSnack.getQuantity() == 4);

        //success payment,suficent change,all ccepted money
        selectedSnack = snackVendingMachine.selectItemAtSlot(14);
        assertTrue(snackVendingMachine.execute(14, selectedSnack.getPrice()));

        Assert.assertEquals(snackVendingMachine.getMoneyController().getMoneyStore().toString(), "{1.0=9, 0.1=1, 50.0=2}");
        Assert.assertEquals(snackVendingMachine.getMoneyController().getTotalChangeMap().toString(), "{1.0=8, 0.1=2, 0.2=1, 20.0=2}");
        assertTrue(selectedSnack.getQuantity() == 4);

        //not enough payment then cancel
        selectedSnack = snackVendingMachine.selectItemAtSlot(0);
        assertFalse(snackVendingMachine.execute(0, selectedSnack.getPrice()));
        Assert.assertEquals(snackVendingMachine.getMoneyController().getMoneyStore().toString(), "{1.0=9, 0.1=1, 50.0=2}");
        Assert.assertTrue(snackVendingMachine.getMoneyController().getTotalChangeMap().size() == 0);
        assertTrue(selectedSnack.getQuantity() == 4);

        //not enough payment then correct
        selectedSnack = snackVendingMachine.selectItemAtSlot(0);
        assertTrue(snackVendingMachine.execute(0, selectedSnack.getPrice()));
        Assert.assertEquals(snackVendingMachine.getMoneyController().getMoneyStore().toString(), "{1.0=11, 0.1=3, 50.0=2}");
        Assert.assertTrue(snackVendingMachine.getMoneyController().getTotalChangeMap().size() == 0);
        assertTrue(selectedSnack.getQuantity() == 3);

        //not enough payment then credit
        selectedSnack = snackVendingMachine.selectItemAtSlot(0);
        assertTrue(snackVendingMachine.execute(0, selectedSnack.getPrice()));
        Assert.assertEquals(snackVendingMachine.getMoneyController().getMoneyStore().toString(), "{1.0=11, 0.1=3, 50.0=2}");
        Assert.assertTrue(snackVendingMachine.getMoneyController().getTotalChangeMap().size() == 0);
        assertTrue(selectedSnack.getQuantity() == 2);

    }

    @Test
    public void soldOutTest() throws FileNotFoundException {
        snackVendingMachine.getKeypad().addScanner(new Scanner(new File(loader.getResource("sold_out.txt").getPath()
        )));
        Snack selectedSnack = snackVendingMachine.selectItemAtSlot(6);
        assertTrue(snackVendingMachine.execute(6, selectedSnack.getPrice()));
        assertTrue(selectedSnack.getQuantity() == 1);

        //not  sufficent change
        selectedSnack = snackVendingMachine.selectItemAtSlot(6);
        assertFalse(snackVendingMachine.execute(6, selectedSnack.getPrice()));
        assertTrue(selectedSnack.getQuantity() == 1);

        //not enough payment then credit
        selectedSnack = snackVendingMachine.selectItemAtSlot(6);
        assertTrue(snackVendingMachine.execute(6, selectedSnack.getPrice()));
        assertTrue(selectedSnack.getQuantity() == 0);

        assertNull(snackVendingMachine.selectItemAtSlot(6));
    }
}