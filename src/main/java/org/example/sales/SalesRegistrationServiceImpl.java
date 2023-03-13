package org.example.sales;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.customer.CustomerService;
import org.example.products.Item;
import org.example.products.StorageService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SalesRegistrationServiceImpl implements SalesRegistrationService {

    private final StorageService storageService;
    private static final Scanner scanner = new Scanner(System.in);
    private static int saleCounter;
    private static SalesLogger salesLogger = SalesLogger.getInstance();
    private static LocalDate currentDate;

    public SalesRegistrationServiceImpl(StorageService storageService) {
        this.storageService = storageService;
        currentDate = LocalDateTime.now().toLocalDate();
    }

    @Override
    public void runSalesMenu(CustomerService customerService) {
        Basket basket = new Basket();
        while (true) {
            System.out.println("Пожалуйста, выберите операцию, введя её номер и нажав ENTER:");
            System.out.println("1. Добавить товары в корзину");
            System.out.println("2. Удалить товары из корзины");
            System.out.println("3. Очистить корзину");
            System.out.println("4. Посмотреть содержимое корзины");
            System.out.println("5. Завершить покупку");
            System.out.println("0. Вернуться в предыдущее меню");
            int choice = -1;
            try {
                choice = scanner.nextInt();
                String s = scanner.nextLine();
            } catch (Exception e) {
                String s = scanner.next();
            }
            if (choice == 0) {
                if (basket.getBasketContents().size() > 0) {
                    emptyBasket(basket);
                    break;
                } else {
                    break;
                }
            } else {
                switch (choice) {
                    case 1: {
                        //Добавляем товар
                        while (true) {
                            customerService.listAvailableItems();
                            System.out.println("Введите идентификатор товара и количество (пример: 1 3) " +
                                                       "и нажмите ENTER, чтобы продолжить, или X, чтобы отменить");
                            String input = scanner.nextLine();
                            Pattern p = Pattern.compile("[0-9]+[ ]{1}[0-9]+");
                            Matcher m = p.matcher(input);
                            if (input.equals("X")) {
                                break;
                            } else if (!m.matches()) {
                                System.out.println("Недопустимый ввод");
                            } else {
                                String[] splitInput = new String[2];
                                splitInput = input.split(" ");
                                int id = Integer.parseInt(splitInput[0]);
                                int qty = Integer.parseInt(splitInput[1]);
                                addItem(basket, id, qty);
                            }
                        }
                        break;
                    }
                    case 2: {
                        //Удаляем товар
                        while (true) {
                            if (basket.getBasketContents() == null
                                        || basket.getBasketContents().stream().allMatch(s -> s.getValue() == 0)) {
                                System.out.println("Нечего удалять. Ваша корзина пуста.");
                                break;
                            } else {
                                viewBasketContents(basket);
                                System.out.println("Введите идентификатор товара и количество (пример: 1 3) " +
                                                           "и нажмите ENTER, чтобы продолжить, или X, чтобы отменить");
                                String input = scanner.nextLine();
                                Pattern p = Pattern.compile("[0-9]+[ ]{1}[0-9]+");
                                Matcher m = p.matcher(input);
                                String[] splitInput = new String[2];
                                if (input.equals("X")) {
                                    break;
                                } else if (!m.matches()) {
                                    System.out.println("Недопустимый ввод");
                                    break;
                                } else {
                                    splitInput = input.split(" ");
                                }
                                int id = Integer.parseInt(splitInput[0]);
                                int qty = Integer.parseInt(splitInput[1]);
                                removeItem(basket, id, qty);
                            }
                        }
                        break;
                    }
                    case 3: {
                        //Пустая корзина
                        emptyBasket(basket);
                        break;
                    }
                    case 4: {
                        viewBasketContents(basket);
                        break;
                    }
                    case 5: {
                        if (basket.getBasketContents() == null
                                    || basket.getBasketContents().stream().allMatch(s -> s.getValue() == 0)) {
                            System.out.println("Ваша корзина пуста.");
                            break;
                        }
                        viewBasketContents(basket);
                        System.out.println("Вы хотите завершить свою покупку?");
                        while (true) {
                            System.out.println("Введите Y или N и нажмите ENTER, чтобы продолжить.");
                            String input = scanner.nextLine();
                            Pattern p = Pattern.compile("Y{1}|N{1}");
                            Matcher m = p.matcher(input);
                            if (!m.matches()) {
                                System.out.println("Недопустимый ввод");
                            } else if (input.equals("N")) {
                                break;
                            } else {
                                recordSale(basket);
                                break;
                            }
                        }
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

    public void addItem(Basket basket, int id, int qty) {
        // Проверяем, является ли введенное количество отрицательным
        if (qty < 0) {
            System.out.println("Отрицательные значения не допускаются.");
            return;
        }
        Map.Entry<Item, Integer> itemEntry = storageService.getItemEntryById(id);
        // Проверяем, есть ли товар в наличии, есть ли положительное количество и есть ли розничная цена
        if (itemEntry == null || itemEntry.getValue() <= 0 || itemEntry.getKey().getRetailPrice() <= 0) {
            System.out.println("Извините, с идентификатором ничего не найдено " + id);
        } else {
            Item item = itemEntry.getKey();
            Integer availableQty = itemEntry.getValue();
            if (availableQty >= qty) {
                if (storageService.takeItem(item, qty)) {
                    basket.addItem(item, qty);
                    System.out.println(qty + " " + item.getName() +
                                               " успешно добавлен в корзину");
                }
            } else {
                System.out.println("Недостаточно предметов. Только " + availableQty + " доступный");
            }
        }
    }

    public void removeItem(Basket basket, int id, int qty) {
        //Проверяем, не равно ли количество нулю
        if (qty == 0) {
            System.out.println("Вы не ввели количество для удаления.");
        } else if (qty < 0) {
            System.out.println("Отрицательные значения не допускаются.");
        } else {
            //Проверяем, есть ли в хранилище элемент с id
            Item item = storageService.getItemEntryById(id).getKey();
            //Если в хранилище есть товар - проверяем, есть ли в корзине этот товар
            if (item != null) {
                Optional<Map.Entry<Item, Integer>> optItem = basket.getBasketContents()
                                                                     .stream()
                                                                     .filter(s -> s.getKey().getId() == id && s.getValue() > 0)
                                                                     .findFirst();
                Integer availableQty = 0;
                //Если товар есть в корзине, получаем доступное количество
                if (optItem.isPresent()) {
                    availableQty = optItem.get().getValue();
                    //Если товара нет в корзине - вывести сообщение покупателю
                } else {
                    System.out.println("Извините, в вашей корзине нет товаров с идентификатором " + id);
                }
                //Проверяем, достаточно ли в корзине товаров для удаления
                if (availableQty >= qty) {
                    storageService.addItem(item, qty);
                    basket.removeItem(item, qty);
                    System.out.println(qty + " " + item.getName() +
                                               " успешно удалено из корзины");
                    //Если в корзине недостаточно товаров для удаления - вывести сообщение покупателю
                } else if (availableQty > 0) {
                    System.out.println("Есть только " + availableQty + " товары в вашей корзине");
                }
                //Если в хранилище нет товара - выводим сообщение покупателю
            } else {
                System.out.println("Извините, с идентификатором ничего не найдено " + id);
            }
        }

    }

    public void emptyBasket(Basket basket) {
        Set<Map.Entry<Item, Integer>> basketContents = basket.getBasketContents();
        if (basketContents == null) {
            System.out.println("Ваша корзина пуста.");
        } else {
            basketContents.forEach(s -> storageService.addItem(s.getKey(), s.getValue()));
            basket.emptyBasket();
            System.out.println("Корзина успешно очищена.");
        }
    }

    public void viewBasketContents(Basket basket) {
        Set<Map.Entry<Item, Integer>> basketContents = basket.getBasketContents();
        if (basketContents == null) {
            System.out.println("Ваша корзина пуста");
        } else {
            System.out.printf("%-10s%-25s%-10s%-10s%s%n", "Id", "Название", "Цена", "Кол-во", "Всего");
            basketContents.stream()
                    .sorted((o1, o2) -> {
                        if (o1.getKey().getId() == o2.getKey().getId()) {
                            return 0;
                        } else {
                            return o1.getKey().getId() > o2.getKey().getId() ? 1 : -1;
                        }
                    })
                    .forEach(s -> {
                        if (s.getValue() > 0) {
                            System.out.printf("%-10s%-25s%-10d%-10d%d%n",
                                    s.getKey().getId(),
                                    s.getKey().getName(),
                                    s.getKey().getRetailPrice(),
                                    s.getValue(),
                                    s.getKey().getRetailPrice() * s.getValue());
                        }
                    });
            AtomicInteger subtotal = new AtomicInteger();
            basketContents.forEach(s -> {
                subtotal.addAndGet(s.getKey().getRetailPrice() * s.getValue());
            });
            System.out.printf("%55s%d%n", "ИТОГО:",
                    subtotal.get());
        }
    }

    @Override
    public void recordSale(Basket basket) {
        Set<Map.Entry<Item, Integer>> basketContents = basket.getBasketContents();
        Gson gson = new GsonBuilder()
                            .registerTypeAdapter(basketContents.getClass(), new MapEntrySetAdapter())
                            .setPrettyPrinting()
                            .create();
        String json = gson.toJson(basketContents);
        saleCounter++;
        if (!LocalDateTime.now().toLocalDate().equals(currentDate)) {
            currentDate = LocalDateTime.now().toLocalDate();
            saleCounter = 1;
        } else {
            String fileName = currentDate + "_sale_" + saleCounter;
            salesLogger.log(json, "out", fileName);
            basket.emptyBasket();
        }
    }
}
