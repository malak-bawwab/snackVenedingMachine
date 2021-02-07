package com.freightos.assignment;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static org.junit.Assert.*;

/**
 * This class is used to test most of the Main application scenarios.
 */
public class SnackVendingMachineApplicationTest {

    SnackVendingMachine snackVendingMachine = new SnackVendingMachine();

    /**
     * This test will test most payWithCard scenarios,it will consider card_payment
     * file((in resources) as an input to scanner,so instead of require console input
     * from user,it will read them form the file.
     *
     * @throws FileNotFoundException
     */
    @Test
    public void payWithCardTest() throws FileNotFoundException {
        snackVendingMachine.getKeypad().addScanner(new Scanner(new File("card_payment_test.txt")));

        //case1 :success payment with card(valid number,enough balance).
        //each time the test validate moneyStore,change returned,snack updated quantity.
        Snack selectedSnack = snackVendingMachine.selectItemAtSlot(0);
        assertTrue(snackVendingMachine.execute(0, selectedSnack.getPrice()));
        Assert.assertEquals("{1.0=15, 0.1=1, 0.2=1, 20.0=2, 50.0=1}", snackVendingMachine.getMoneyController().getMoneyStore().toString());
        Assert.assertTrue(snackVendingMachine.getMoneyController().getTotalChangeMap().size() == 0);
        assertTrue(selectedSnack.getQuantity() == 4);

        //case 2:valid card number,not enough payment-> then user press cancel.
        selectedSnack = snackVendingMachine.selectItemAtSlot(1);
        assertFalse(snackVendingMachine.execute(1, selectedSnack.getPrice()));
        Assert.assertEquals("{1.0=15, 0.1=1, 0.2=1, 20.0=2, 50.0=1}", snackVendingMachine.getMoneyController().getMoneyStore().toString());
        Assert.assertTrue(snackVendingMachine.getMoneyController().getTotalChangeMap().size() == 0);
        assertTrue(selectedSnack.getQuantity() == 5);

        //case3:user enter wrong  card id multiple times then entered valid id(enough balance).
        selectedSnack = snackVendingMachine.selectItemAtSlot(2);
        assertTrue(snackVendingMachine.execute(2, selectedSnack.getPrice()));
        Assert.assertEquals("{1.0=15, 0.1=1, 0.2=1, 20.0=2, 50.0=1}", snackVendingMachine.getMoneyController().getMoneyStore().toString());
        Assert.assertTrue(snackVendingMachine.getMoneyController().getTotalChangeMap().size() == 0);
        assertTrue(selectedSnack.getQuantity() == 4);

        /*case4:user enter wrong card id multiple times then entered cancel
         to refer to payment method,then entered cancel.
        */
        selectedSnack = snackVendingMachine.selectItemAtSlot(2);
        assertFalse(snackVendingMachine.execute(2, selectedSnack.getPrice()));
        Assert.assertEquals("{1.0=15, 0.1=1, 0.2=1, 20.0=2, 50.0=1}", snackVendingMachine.getMoneyController().getMoneyStore().toString());
        Assert.assertTrue(snackVendingMachine.getMoneyController().getTotalChangeMap().size() == 0);
        assertTrue(selectedSnack.getQuantity() == 4);

        /*case5:user enter wrong card id multiple times then entered cancel
         to refer to payment method,then choose pay with coin/Note
        */
        selectedSnack = snackVendingMachine.selectItemAtSlot(8);
        assertTrue(snackVendingMachine.execute(8, selectedSnack.getPrice()));
        Assert.assertEquals("{1.0=18, 20.0=2, 50.0=1}", snackVendingMachine.getMoneyController().getMoneyStore().toString());
        Assert.assertEquals("{0.1=1, 0.2=1}", snackVendingMachine.getMoneyController().getTotalChangeMap().toString());
        assertTrue(selectedSnack.getQuantity() == 4);

        /*case 6:valid card number,not enough payment-> then user press cancel to
        refer to payment methods,and choose pay with coin/notes.
        */
        selectedSnack = snackVendingMachine.selectItemAtSlot(15);
        assertTrue(snackVendingMachine.execute(15, selectedSnack.getPrice()));
        Assert.assertEquals("{1.0=19, 20.0=2, 50.0=1}", snackVendingMachine.getMoneyController().getMoneyStore().toString());
        Assert.assertEquals("{1.0=2}", snackVendingMachine.getMoneyController().getTotalChangeMap().toString());
        assertTrue(selectedSnack.getQuantity() == 6);

    }

    /**
     * This test will test most payWithCoin/notes scenarios,it will consider coin_payment
     * file((in resources) as an input to scanner,so instead of require console input
     * from user,it will read them form the file.
     *
     * @throws FileNotFoundException
     */
    @Test
    public void payWithCoinTest() throws FileNotFoundException {
        snackVendingMachine.getKeypad().addScanner(new Scanner(new File("coin_paymet_test.txt")));

        //case1 :successful payment with money no change returned,all entered money are accepted.
        //each time the test validate moneyStore,change returned,snack updated quantity.
        Snack selectedSnack = snackVendingMachine.selectItemAtSlot(0);
        assertTrue(snackVendingMachine.execute(0, selectedSnack.getPrice()));
        Assert.assertEquals("{1.0=17, 0.1=3, 0.2=1, 20.0=2, 50.0=1}", snackVendingMachine.getMoneyController().getMoneyStore().toString());
        Assert.assertTrue(snackVendingMachine.getMoneyController().getTotalChangeMap().size() == 0);
        assertTrue(selectedSnack.getQuantity() == 4);

        //case2 :unsuccessful payment with no sufficient change ,not all entered money are accepted.
        selectedSnack = snackVendingMachine.selectItemAtSlot(0);
        assertFalse(snackVendingMachine.execute(0, selectedSnack.getPrice()));
        Assert.assertEquals("{1.0=17, 0.1=3, 0.2=1, 20.0=2, 50.0=1}", snackVendingMachine.getMoneyController().getMoneyStore().toString());
        Assert.assertTrue(snackVendingMachine.getMoneyController().getTotalChangeMap().size() == 0);
        assertTrue(selectedSnack.getQuantity() == 4);

        //case3:successful payment with sufficient change all entered money are accepted.
        selectedSnack = snackVendingMachine.selectItemAtSlot(14);
        assertTrue(snackVendingMachine.execute(14, selectedSnack.getPrice()));
        Assert.assertEquals("{1.0=9, 0.1=1, 50.0=2}", snackVendingMachine.getMoneyController().getMoneyStore().toString());
        Assert.assertEquals("{1.0=8, 0.1=2, 0.2=1, 20.0=2}", snackVendingMachine.getMoneyController().getTotalChangeMap().toString());
        assertTrue(selectedSnack.getQuantity() == 4);

        //case4:unsuccessful payment,Not enough entered money,then user cancel the operation.
        selectedSnack = snackVendingMachine.selectItemAtSlot(0);
        assertFalse(snackVendingMachine.execute(0, selectedSnack.getPrice()));
        Assert.assertEquals("{1.0=9, 0.1=1, 50.0=2}", snackVendingMachine.getMoneyController().getMoneyStore().toString());
        Assert.assertTrue(snackVendingMachine.getMoneyController().getTotalChangeMap().size() == 0);
        assertTrue(selectedSnack.getQuantity() == 4);

        //case5:successful payment,Not enough entered money,then user insert coin again.
        selectedSnack = snackVendingMachine.selectItemAtSlot(0);
        assertTrue(snackVendingMachine.execute(0, selectedSnack.getPrice()));
        Assert.assertEquals("{1.0=11, 0.1=3, 50.0=2}", snackVendingMachine.getMoneyController().getMoneyStore().toString());
        Assert.assertTrue(snackVendingMachine.getMoneyController().getTotalChangeMap().size() == 0);
        assertTrue(selectedSnack.getQuantity() == 3);

        //case6:successful payment,Not enough entered money,then user choose another payment method(card).
        selectedSnack = snackVendingMachine.selectItemAtSlot(0);
        assertTrue(snackVendingMachine.execute(0, selectedSnack.getPrice()));
        Assert.assertEquals("{1.0=11, 0.1=3, 50.0=2}", snackVendingMachine.getMoneyController().getMoneyStore().toString());
        Assert.assertTrue(snackVendingMachine.getMoneyController().getTotalChangeMap().size() == 0);
        assertTrue(selectedSnack.getQuantity() == 2);

    }

    /**
     * This test will test most snack soldOut scenarios,it will consider sold_out
     * file((in resources) as an input to scanner,so instead of require console input
     * from user,it will read them form the file.
     *
     * @throws FileNotFoundException
     */
    @Test
    public void soldOutTest() throws FileNotFoundException {
        snackVendingMachine.getKeypad().addScanner(new Scanner(new File("sold_out_test.txt")));
        Snack selectedSnack = snackVendingMachine.selectItemAtSlot(6);
        assertTrue(snackVendingMachine.execute(6, selectedSnack.getPrice()));
        assertTrue(selectedSnack.getQuantity() == 1);

        selectedSnack = snackVendingMachine.selectItemAtSlot(6);
        assertFalse(snackVendingMachine.execute(6, selectedSnack.getPrice()));
        assertTrue(selectedSnack.getQuantity() == 1);

        selectedSnack = snackVendingMachine.selectItemAtSlot(6);
        assertTrue(snackVendingMachine.execute(6, selectedSnack.getPrice()));
        assertTrue(selectedSnack.getQuantity() == 0);

        assertNull(snackVendingMachine.selectItemAtSlot(6));
    }
}