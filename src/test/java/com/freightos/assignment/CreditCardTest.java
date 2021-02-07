package com.freightos.assignment;

import org.junit.Test;

public class CreditCardTest {
    CreditCard card = new CreditCard("500az", 600);

    @Test
    public void decrementBalanceTest() {
        assert (card.decrementBalance(100.50) == 499.50);
        assert (card.decrementBalance(800.00) == 499.50);
        assert (card.decrementBalance(199.50) == 300.00);
        assert (card.decrementBalance(500) == 300.00);
    }
}