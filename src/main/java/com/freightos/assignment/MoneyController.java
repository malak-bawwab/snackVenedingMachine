package com.freightos.assignment;

import java.io.*;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.*;

public class MoneyController {
    private final Map<Double, Integer> moneyStore = new HashMap<>();
    private final List<CreditCard> cardList = new ArrayList<>();
    private Map<Double, Integer> localMoneyStore;
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

    private void fillMoneyStore() {
        moneyStore.put(.10, 1);
        moneyStore.put(1.0, 15);
        moneyStore.put(.20, 1);
        moneyStore.put(50.0, 1);
        moneyStore.put(20.0, 2);
        localMoneyStore = new HashMap<>(moneyStore);
    }

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

    public boolean isCardAvailable(String cardId) {
        for (CreditCard card : cardList) {
            if (card.getCardId().equals(cardId))
                return true;
        }
        return false;
    }

    public boolean doPayment(Keypad keypad, double price) {
        printPaymentMethods();
        int paymentChoice = 0;
        paymentChoice = Integer.parseInt(keypad.read());
        return executePaymentMethod(keypad, paymentChoice, price);
    }

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

    private BigDecimal getAccumulatedMoney(Map<Double, Integer> enteredMoneyMap) {
        BigDecimal accumulatedMoney = new BigDecimal(0);
        for (Map.Entry<Double, Integer> entry : enteredMoneyMap.entrySet()) {
            accumulatedMoney = accumulatedMoney.add(BigDecimal.valueOf(entry.getKey()).multiply(BigDecimal.valueOf(entry.getValue())));
        }
        return accumulatedMoney;
    }

    private boolean executePaymentMethod(Keypad keypad, int paymentChoice, double price) {
        System.out.print(moneyStore);
        switch (paymentChoice) {
            case 1:
                totalChangeMap.clear();
                System.out.println("You chose the creditCard payment method");
                while (true) {
                    System.out.println("Please Enter your cardId or press ENTER to cancel");
                    String cardId = keypad.read();
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
            case 2:
                Map<Double, Integer> enteredMoneyMap = new HashMap<>();
                System.out.println("You chose the coin/Notes payment method");
                System.out.println("Please start entering your coins or bank notes in dollar\n" +
                        "When you finish please press ENTER\n");
                while (true) {
                    String money = keypad.read();
                    if (money.equals("")) {
                        totalChangeMap.clear();
                        if (getAccumulatedMoney(enteredMoneyMap).compareTo(BigDecimal.valueOf(price)) >= 0) {
                            return collectChange(enteredMoneyMap, price);
                        }
                        System.out.println("Sorry!Your payment is not enough to buy this item.");
                        System.out.println("This is the money you entered, please take them " + formatReturnedMoney(enteredMoneyMap));
                        localMoneyStore.clear();
                        localMoneyStore.putAll(moneyStore);
                        return doPayment(keypad, price);
                    }
                    double enteredMoney;
                    try {
                        enteredMoney = Double.parseDouble(money);

                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid money");
                        continue;
                    }
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

    public void dispenseChange() {
        if (totalChangeMap.size() == 0) {
            System.out.println("There is no change to return");
            return;
        }
        moneyStore.clear();
        moneyStore.putAll(localMoneyStore);
        System.out.println("Please take your change : " + formatReturnedMoney(totalChangeMap));
    }

    private boolean collectChange(Map<Double, Integer> enteredMoneyMap, double price) {
        BigDecimal balance = getAccumulatedMoney(enteredMoneyMap);
        BigDecimal cost = BigDecimal.valueOf(price);
        BigDecimal changeAmount = (balance.subtract(cost));
        if (changeAmount.compareTo(BigDecimal.valueOf(0)) == 0) {
            System.out.println("Your payment has been performed successfully\uD83C\uDF89\uD83C\uDF89");
            moneyStore.clear();
            System.out.println("GG" + localMoneyStore);
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

    private BigDecimal update(BigDecimal amount, double money) {
        //decrement local store
        int count = localMoneyStore.get(money) - 1;
        if (count == 0) {
            localMoneyStore.remove(money);

        } else {
            localMoneyStore.put(money, count);
        }
        totalChangeMap.merge(money, 1, Integer::sum);
        return amount.subtract(BigDecimal.valueOf(money));
    }

    private Map<Double, Integer> updateTotalChangeMap(BigDecimal amount) {
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
                return totalChangeMap;
            }
        }
        return totalChangeMap;
    }

    public void printPaymentMethods() {
        System.out.println("Please enter the index of your preferred payment method\uD83D\uDCB0\uD83D\uDCB0:");
        System.out.println("1-CreditCard");
        System.out.println("2-Coins&Notes");
        System.out.println("3-Cancel");
    }
}
