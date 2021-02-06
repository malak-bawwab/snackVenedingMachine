package com.freightos.assignment;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static org.junit.Assert.*;

/**
 * This test is used to test most of the Main application scenario.
 */
public class SnackVendingMachineApplicationTest {

    SnackVendingMachine snackVendingMachine = new SnackVendingMachine();
    private ClassLoader loader = SnackVendingMachineApplicationTest.class.getClassLoader();

    /**
     * This test will test most payWithCard scenarios,it will consider card_payment
     * file((in resources) as an input to scanner,so instead of require console input
     * from user,it will read them form the file.
     *
     * @throws FileNotFoundException
     */
    @Test
    public void payWithCardTest() throws FileNotFoundException {
        snackVendingMachine.getKeypad().addScanner(new Scanner(new File(loader.getResource("card_payment.txt").getPath()
        )));

        //case1 :success payment with card(valid number,enough balance).
        //each time the test validate moneyStore,change returned,snack updated quantity.
        Snack selectedSnack = snackVendingMachine.selectItemAtSlot(0);
        assertTrue(snackVendingMachine.execute(0, selectedSnack.getPrice()));
        Assert.assertEquals(snackVendingMachine.getMoneyController().getMoneyStore().toString(), "{1.0=15, 0.1=1, 0.2=1, 20.0=2, 50.0=1}");
        Assert.assertEquals(snackVendingMachine.getMoneyController().getTotalChangeMap().size(), 0);
        assertTrue(selectedSnack.getQuantity() == 4);

        /*case 2:valid card number,not enough payment-> then user press cancel to refer to payment methods
         and then press cancel.
        */
        selectedSnack = snackVendingMachine.selectItemAtSlot(1);
        assertFalse(snackVendingMachine.execute(1, selectedSnack.getPrice()));
        Assert.assertEquals(snackVendingMachine.getMoneyController().getMoneyStore().toString(), "{1.0=15, 0.1=1, 0.2=1, 20.0=2, 50.0=1}");
        Assert.assertEquals(snackVendingMachine.getMoneyController().getTotalChangeMap().size(), 0);
        assertTrue(selectedSnack.getQuantity() == 5);

        //case3:user enter wrong  card id multiple times then entered valid id(enough balance).
        selectedSnack = snackVendingMachine.selectItemAtSlot(2);
        assertTrue(snackVendingMachine.execute(2, selectedSnack.getPrice()));
        Assert.assertEquals(snackVendingMachine.getMoneyController().getMoneyStore().toString(), "{1.0=15, 0.1=1, 0.2=1, 20.0=2, 50.0=1}");
        Assert.assertEquals(snackVendingMachine.getMoneyController().getTotalChangeMap().size(), 0);
        assertTrue(selectedSnack.getQuantity() == 4);

        /*case4:user enter wrong card id multiple times then entered cancel
         to refer to payment method,then entered cancel.
        */
        selectedSnack = snackVendingMachine.selectItemAtSlot(2);
        assertFalse(snackVendingMachine.execute(2, selectedSnack.getPrice()));
        Assert.assertEquals(snackVendingMachine.getMoneyController().getMoneyStore().toString(), "{1.0=15, 0.1=1, 0.2=1, 20.0=2, 50.0=1}");
        Assert.assertEquals(snackVendingMachine.getMoneyController().getTotalChangeMap().size(), 0);
        assertTrue(selectedSnack.getQuantity() == 4);

        /*case5:user enter wrong card id multiple times then entered cancel
         to refer to payment method,then choose pay with coin/Note
        */
        selectedSnack = snackVendingMachine.selectItemAtSlot(8);
        assertTrue(snackVendingMachine.execute(8, selectedSnack.getPrice()));
        Assert.assertEquals(snackVendingMachine.getMoneyController().getMoneyStore().toString(), "{1.0=18, 20.0=2, 50.0=1}");
        Assert.assertEquals("{0.1=1, 0.2=1}", snackVendingMachine.getMoneyController().getTotalChangeMap().toString());
        assertTrue(selectedSnack.getQuantity() == 4);

        /*case 6:valid card number,not enough payment-> then user press cancel to
        refer to payment methods,and choose pay with coin/notes.
        */
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