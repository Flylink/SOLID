package org.example;

import org.example.customer.*;
import org.example.products.*;
import org.example.sales.*;

public class Main {
    public static void main(String[] args) {
        Product apple = new Product("Apple");
        Product peach = new Product("Peach");
        Product pear = new Product("Pear");
        apple.setRetailPrice(20);
        peach.setRetailPrice(30);
        pear.setRetailPrice(25);
        StorageService mainStorage = new StorageServiceImpl("Основное хранилище");
        mainStorage.addItem(apple, 10);
        mainStorage.addItem(pear, 5);
        mainStorage.addItem(peach, 5);
        mainStorage.getStorageContents();
        SalesRegistrationService salesRegistration = new SalesRegistrationServiceImpl(mainStorage);
        CustomerService customerService = new CustomerServiceImpl(mainStorage, salesRegistration);
        customerService.start();
    }
}