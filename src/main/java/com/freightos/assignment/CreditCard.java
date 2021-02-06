package com.freightos.assignment;

import java.util.Objects;

public class CreditCard {
    private String cardId;
    private double balance;

    public CreditCard(String cardId, double balance) {
        this.cardId = cardId;
        this.balance = balance;
    }

    public String getCardId() {
        return cardId;
    }

    public double getBalance() {
        return balance;
    }

    public double decrementBalance(double value) {
        if (value <= this.balance) {
            this.balance = this.balance - value;
        }
        return this.balance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardId);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CreditCard))
            return false;
        CreditCard card = (CreditCard) obj;
        return this.cardId.equals(card.getCardId());
    }
}
