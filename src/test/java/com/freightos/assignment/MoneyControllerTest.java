package com.freightos.assignment;

import org.junit.Test;

import static org.junit.Assert.*;

public class MoneyControllerTest {
    MoneyController moneyController = new MoneyController();

    @Test
    public void payWithCardTest() {
        assertTrue(moneyController.payWithCard("500as", 1.5));
        assertFalse(moneyController.payWithCard("500ax", 1.5));
        assertFalse(moneyController.payWithCard("500hdhd", 2.5));
    }

    @Test
    public void isCardAvailableTest() {
        assertTrue(moneyController.isCardAvailable("500as"));
        assertFalse(moneyController.payWithCard("500hdhd", 2.5));
    }
}