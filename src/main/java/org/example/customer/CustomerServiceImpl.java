package org.example.customer;

import org.example.products.Item;
import org.example.products.StorageService;
import org.example.sales.SalesRegistrationService;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

public class CustomerServiceImpl implements CustomerService {

    private final static Scanner scanner = new Scanner(System.in);
    private static StringBuilder sb = new StringBuilder();
    private final StorageService storageService;
    private final SalesRegistrationService salesRegistrationService;

    public CustomerServiceImpl(StorageService storageService, SalesRegistrationService salesRegistrationService) {
        this.storageService = storageService;
        this.salesRegistrationService = salesRegistrationService;
    }

    @Override
    public void start() {
        while (true) {
            printMenu();
            int choice = -1;
            try {
                choice = scanner.nextInt();
            } catch (NoSuchElementException e) {
                System.out.println(e.getMessage());
                String s = scanner.next();
            }
            if (choice == 0) {
                System.out.println("До свидания!");
                break;
            } else {
                switch (choice) {
                    case 1: {
                        listAvailableItems();
                        break;
                    }
                    case 2: {
                        salesRegistrationService.runSalesMenu(this);
                        break;
                    }
                    default: {
                        System.out.println("Недопустимый ввод");
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void printMenu() {
        sb.append("Пожалуйста, выберите операцию, введя её номер и нажав ENTER:\n");
        sb.append("1. Посмотреть, что есть в наличии\n");
        sb.append("2. Сделать новую покупку\n");
        sb.append("0. Выход\n");
        System.out.print(sb.toString());
        sb.setLength(0);
    }

    @Override
    public void listAvailableItems() {
        Set<Map.Entry<Item, Integer>> productsSet = storageService.getStorageContents();
        if (productsSet.isEmpty() || productsSet.stream().allMatch(s -> s.getValue() == 0) || productsSet.stream().allMatch(s -> s.getKey().getRetailPrice() == 0)) {
            System.out.println("Извините, в данный момент ничего нет в наличии.");
        } else {
            sb.append("Доступны следующие позиции:\n");
            sb.append(String.format("%-10s %-25s %-15s %s%n", "Id", "Название", "Цена", "Кол-во"));
            productsSet.stream().filter(p -> p.getValue() > 0 && p.getKey().getRetailPrice() > 0).sorted((o1, o2) -> {
                if (o1.getKey().getId() == o2.getKey().getId()) {
                    return 0;
                } else {
                    return o1.getKey().getId() > o2.getKey().getId() ? 1 : -1;
                }
            }).forEach(s -> {
                sb.append(String.format("%-10d %-25s %-15d %d%n", s.getKey().getId(), s.getKey().getName(), s.getKey().getRetailPrice(), s.getValue()));
            });
            System.out.println(sb.toString());
            sb.setLength(0);
        }
    }
}
