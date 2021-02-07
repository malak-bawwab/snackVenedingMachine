package com.freightos.assignment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for snack operations:store,get,update,etc.
 *
 * @author Malak
 */
public class Inventory {

    private Map<Integer, Snack> snackStore = new HashMap<>();

    public Inventory() {
        fillSnackStore();
    }

    public Snack getItem(int key) {
        return snackStore.get(key);
    }

    public boolean isSnackAvailable(int index) {
        return snackStore.get(index).getQuantity() > 0;
    }

    /**
     * This method will deliver the selected snack to customer.
     *
     * @param index selected snack slot index
     */
    public void dispenseSnack(int index) {
        decrementQuantity(index);
        System.out.println("Please pick your snack " + snackStore.get(index).getName() + " Enjoy \uD83D\uDE01");
    }

    /**
     * This method will iterate over all snacks in the snacks file located in resource folder
     * and store them into snackStore.
     */
    public void fillSnackStore() {
        ClassLoader classLoader = getClass().getClassLoader();
        int key = 0;
        File snackFile = new File("snacks.txt");
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(snackFile))) {
            String line;
            String[] splitLine;
            while ((line = bufferedReader.readLine()) != null) {
                splitLine = line.split(" ");
                snackStore.put(key++, new Snack(splitLine[0], Double.parseDouble(splitLine[1]), Integer.parseInt(splitLine[2])));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * This method displays snack list to customers.
     */
    public void printSnackList() {
        int i;
        System.out.println("-------------------------------------------------------------");
        for (i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                System.out.print(i * 5 + j + " \t\t");
            }
            System.out.print("\n");
            for (int j = 0; j < 5; j++) {
                System.out.print(getItem(i * 5 + j).getName() + "  ");
            }
            System.out.print("\n\n");
        }
        System.out.println("-------------------------------------------------------------");
        System.out.println("Please Enter the index of your chosen snack\uD83C\uDF6B\uD83C\uDF6B: ");
    }

    private int decrementQuantity(int index) {
        int updatedQuantity = snackStore.get(index).getQuantity() - 1;
        snackStore.get(index).setQuantity(updatedQuantity);
        return updatedQuantity;
    }
}
