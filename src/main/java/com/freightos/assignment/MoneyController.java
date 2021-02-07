package com.freightos.assignment;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * This class is used to handle payment process(payment method,changes,moneyStore,etc.)
 *
 * @author Malak
 */
public class MoneyController {
    //moneyStore will only be updated with success coin/Notes payment with no/sufficient change.
    private final Map<Double, Integer> moneyStore = new HashMap<>();
    private final List<CreditCard> cardList = new ArrayList<>();

    //It will be updated whether payment success/not with no/sufficient/not sufficient change.
    private Map<Double, Integer> localMoneyStore;

    //contain all the change to return,Double is the money itself,Integer is the amount of this money.
    private Map<Double, Integer> totalChangeMap = new HashMap<>();

    public MoneyController() {
        fillCardList();
        fillMoneyStore();
    }

    public Map<Double, Integer> getMoneyStore() {
        return moneyStore;
    }

    public Map<Double, Integer> getTotalChangeMap() {
        return totalChangeMap;
    }

    /**
     * This method will iterate over all cards in the cards file located in resource folder
     * and store them into the cardList.
     */
    private void fillCardList() {
        ClassLoader classLoader = getClass().getClassLoader();
        File cardFile = new File(classLoader.getResource("cards.txt").getPath());
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(cardFile))) {
            String line;
            String[] splitLine;
            while ((line = bufferedReader.readLine()) != null) {
                splitLine = line.split(" ");
                cardList.add(new CreditCard(splitLine[0], Double.parseDouble(splitLine[1])));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * This method will initially fill moneyStore .
     */
    private void fillMoneyStore() {
        moneyStore.put(.10, 1);
        moneyStore.put(1.0, 15);
        moneyStore.put(.20, 1);
        moneyStore.put(50.0, 1);
        moneyStore.put(20.0, 2);

        //initially localMoneyStore is a copy from moneyStore
        localMoneyStore = new HashMap<>(moneyStore);
    }

    /**
     * This method will determine if the credit card has enough balance to pay the selected snack.
     *
     * @param cardId customer card id.
     * @param price  price of the selected snack.
     * @return true if credit card has enough balance,otherwise false.
     */
    public boolean payWithCard(String cardId, double price) {
        for (CreditCard card : cardList) {
            if (card.getCardId().equals(cardId)) {
                if (price <= card.getBalance()) {
                    card.decrementBalance(price);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    /**
     * This method will check if the cardList contain customer creditCard.
     *
     * @param cardId customer creditCard.
     * @return true if card exists,false otherwise.
     */
    public boolean isCardAvailable(String cardId) {
        for (CreditCard card : cardList) {
            if (card.getCardId().equals(cardId))
                return true;
        }
        return false;
    }

    /**
     * This method will display payment methods to customer,and will allow
     * him to enter his preferred payment method ,and will execute the payment
     * based on the chosen payment method.
     *
     * @param keypad keypad of the snack vending machine.
     * @param price  price of selected snack.
     * @return true if payment is successful,false otherwise.
     */
    public boolean doPayment(Keypad keypad, double price) {
        printPaymentMethods();
        int paymentChoice = 0;
        paymentChoice = Integer.parseInt(keypad.read());
        return executePaymentMethod(keypad, paymentChoice, price);
    }

    /**
     * This method will format the returned money to customer to be
     * displayed in a readable format.
     *
     * @param returnedMoney moneyToReturn to customer,key is the money itself,value
     *                      is the count of the money.
     * @return formatted returnedMoney.
     */
    private String formatReturnedMoney(Map<Double, Integer> returnedMoney) {
        StringBuilder formattedResult = new StringBuilder();
        for (Map.Entry<Double, Integer> entry : returnedMoney.entrySet()) {
            for (int index = 0; index < entry.getValue(); index++) {
                formattedResult.append(entry.getKey());
                formattedResult.append("$ , ");
            }
        }
        return formattedResult.toString();
    }

    /**
     * This method will add all the money entered by the customer and return one accumulated value.
     *
     * @param enteredMoneyMap the money entered by the customer,where key is the money itself
     *                        value is the count of this money.
     * @return accumulatedMoneyEntered.
     */
    private BigDecimal getAccumulatedMoney(Map<Double, Integer> enteredMoneyMap) {
        BigDecimal accumulatedMoney = new BigDecimal(0);
        for (Map.Entry<Double, Integer> entry : enteredMoneyMap.entrySet()) {
            accumulatedMoney = accumulatedMoney.add(BigDecimal.valueOf(entry.getKey()).multiply(BigDecimal.valueOf(entry.getValue())));
        }
        return accumulatedMoney;
    }

    /**
     * This method will execute payment based on the customer preferred method,
     * 1 for card, 2 for coin/notes.
     *
     * @param keypad        keypad of the SnackVendingMachine.
     * @param paymentChoice customer preferred payment method.
     * @param price         price of selected snack.
     * @return true if payment process is successful,false otherwise.
     */
    private boolean executePaymentMethod(Keypad keypad, int paymentChoice, double price) {
        switch (paymentChoice) {
            /*
                -if customer entered wrong card id,he can continue trying until he entered the correct id or
                he can press enter to refer to payment methods .
                -if card don't have enough balance,vending machine will forward customer to payment methods.
            */
            case 1:
                totalChangeMap.clear();
                System.out.println("You chose the creditCard payment method");
                while (true) {
                    System.out.println("Please Enter your cardId or press ENTER to cancel");
                    String cardId = keypad.read();
                    //check if the user press enter or not
                    if (!cardId.equals("")) {
                        if (!isCardAvailable(cardId)) {
                            System.out.println("Sorry!Your credit card id is not valid, please try again or press ENTER to cancel");
                            continue;
                        }
                        if (payWithCard(cardId, price)) {
                            System.out.println("Your payment has been performed successfully\uD83C\uDF89\uD83C\uDF89");
                            return true;
                        }
                        System.out.println("Sorry!Your balance is not enough to buy this item." +
                                "Please use another method to pay or cancel the request");
                    }
                    return doPayment(keypad, price);
                }
                /*
                -if customer entered unaccepted money,machine will not accept them and
                will return the unaccepted money to customer and wil ask the customer to enter valid money.
                -if payment is not sufficient,machine will forward customer to payment
                methods and return the money he entered.
                -if there is no sufficient change,machine will ask customer to choose another snack.
                 */
            case 2:
                /*to store customer entered money,key is the money itself,value
                is the amount of the money.
                 */
                Map<Double, Integer> enteredMoneyMap = new HashMap<>();
                System.out.println("You chose the coin/Notes payment method");
                System.out.println("Please start entering your coins or bank notes in dollar\n" +
                        "When you finish please press ENTER\n");
                while (true) {
                    String money = keypad.read();
                    //if customer press enter-->he finish inserting money to the machine.
                    if (money.equals("")) {
                        totalChangeMap.clear();
                        //if money entered by customer > snack price,calculate change if any exists.
                        if (getAccumulatedMoney(enteredMoneyMap).compareTo(BigDecimal.valueOf(price)) >= 0) {
                            return collectChange(enteredMoneyMap, price);
                        }
                        System.out.println("Sorry!Your payment is not enough to buy this item.");
                        System.out.println("This is the money you entered, please take them " + formatReturnedMoney(enteredMoneyMap));
                        localMoneyStore.clear();
                        localMoneyStore.putAll(moneyStore);
                        return doPayment(keypad, price);
                    }
                    //enteredMoney by customer.
                    double enteredMoney;
                    try {
                        enteredMoney = Double.parseDouble(money);

                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid money");
                        continue;
                    }
                    //validate money entered.
                    if (enteredMoney == 20.0 || enteredMoney == 50.0 || enteredMoney == 1.00 || enteredMoney == 0.50 || enteredMoney == 0.20 || enteredMoney == 0.10) {
                        enteredMoneyMap.merge(enteredMoney, 1, Integer::sum);
                        localMoneyStore.merge(enteredMoney, 1, Integer::sum);
                        System.out.println("Money entered : " + getAccumulatedMoney(enteredMoneyMap));
                    } else {
                        System.out.println("Sorry!Unaccepted Money entered, please enter one of .10/.20/.50/20/1/50");
                        System.out.println("This is the money you entered, please take them " + enteredMoney);
                    }

                }
            case 3:
                break;
            default:
                System.out.println("Sorry!Incorrect payment method choice, please try again");
                doPayment(keypad, price);
        }
        return false;
    }

    /**
     * This method will dispense changes to custom if any exists.
     */
    public void dispenseChange() {
        if (totalChangeMap.size() == 0) {
            System.out.println("There is no change to return");
            return;
        }
        moneyStore.clear();
        moneyStore.putAll(localMoneyStore);
        System.out.println("Please take your change : " + formatReturnedMoney(totalChangeMap));
    }

    /**
     * This method will call updateTotalChangeMap in case accumulatedMoney >price.
     *
     * @param enteredMoneyMap entered money by the customer,key is the money itself
     *                        value is the count of the money.
     * @param price           selected snack price.
     * @return false if there is no sufficient change.
     */
    private boolean collectChange(Map<Double, Integer> enteredMoneyMap, double price) {
        BigDecimal balance = getAccumulatedMoney(enteredMoneyMap);
        BigDecimal cost = BigDecimal.valueOf(price);
        BigDecimal changeAmount = (balance.subtract(cost));
        if (changeAmount.compareTo(BigDecimal.valueOf(0)) == 0) {
            System.out.println("Your payment has been performed successfully\uD83C\uDF89\uD83C\uDF89");
            moneyStore.clear();
            moneyStore.putAll(localMoneyStore);
            return true;
        }
        updateTotalChangeMap(changeAmount);
        if (totalChangeMap.size() == 0) {
            localMoneyStore.clear();
            localMoneyStore.putAll(moneyStore);
            System.out.println("Sorry!Not_Sufficient_Change.This is the money you entered, please take them " + formatReturnedMoney(enteredMoneyMap));
            System.out.println("Please choose another Snack");
            return false;
        }
        System.out.println("Your payment has been performed successfully\uD83C\uDF89\uD83C\uDF89");
        return true;
    }

    /**
     * This method will decrement localMoneyStore and will increment totalChange Map.
     *
     * @param amount change.
     * @param money  money itself.
     * @return remainingChange.
     */
    private BigDecimal update(BigDecimal amount, double money) {
        //decrement localMoneyStore
        int count = localMoneyStore.get(money) - 1;
        if (count == 0) {
            localMoneyStore.remove(money);

        } else {
            localMoneyStore.put(money, count);
        }
        totalChangeMap.merge(money, 1, Integer::sum);
        return amount.subtract(BigDecimal.valueOf(money));
    }


    /**
     * This method will use greedy algorithm to check if there is a sufficient change
     * or not.If yes,it will update totalChangeMap(represent change amount with
     * machine accepted money).
     *
     * @param amount is the change(accumulated - amount).
     */
    private void updateTotalChangeMap(BigDecimal amount) {
        while (amount.compareTo(BigDecimal.valueOf(0)) > 0) {
            if (amount.compareTo(BigDecimal.valueOf(50.0)) >= 0 && localMoneyStore.containsKey(50.0)) {
                amount = update(amount, 50.0);

            } else if (amount.compareTo(BigDecimal.valueOf(20)) >= 0 && localMoneyStore.containsKey(20.0)) {
                amount = update(amount, 20.0);

            } else if (amount.compareTo(BigDecimal.valueOf(1)) >= 0 && localMoneyStore.containsKey(1.0)) {
                amount = update(amount, 1);

            } else if (amount.compareTo(BigDecimal.valueOf(0.50)) >= 0 && localMoneyStore.containsKey(0.50)) {
                amount = update(amount, 0.50);

            } else if (amount.compareTo(BigDecimal.valueOf(0.20)) >= 0 && localMoneyStore.containsKey(0.20)) {
                amount = update(amount, 0.20);
            } else if (amount.compareTo(BigDecimal.valueOf(0.10)) >= 0 && localMoneyStore.containsKey(.10)) {
                amount = update(amount, .10);
            } else {
                totalChangeMap.clear();
                return;
            }
        }
    }

    /**
     * This method will display payment method to customer.
     */
    public void printPaymentMethods() {
        System.out.println("Please enter the index of your preferred payment method\uD83D\uDCB0\uD83D\uDCB0:");
        System.out.println("1-CreditCard");
        System.out.println("2-Coins&Notes");
        //cancel will forward customer to snack display.
        System.out.println("3-Cancel");
    }
}
