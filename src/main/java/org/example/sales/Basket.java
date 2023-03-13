package org.example.sales;

import org.example.products.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Basket {
    private final Map<Item, Integer> basket = new HashMap<>();

    protected void addItem(Item item, int quantity) {
        if (basket.containsKey(item)) {
            basket.replace(item, basket.get(item) + quantity);
        } else {
            basket.put(item, quantity);
        }
    }

    protected boolean removeItem(Item item, int quantity) {
        if (basket.containsKey(item)) {
            if (basket.get(item) >= quantity) {
                basket.replace(item, basket.get(item) - quantity);
            }
        } else {
            return false;
        }
        return true;
    }

    protected void emptyBasket() {
        basket.clear();
    }

    protected Set<Map.Entry<Item, Integer>> getBasketContents() {
        if (basket.isEmpty()) {
            return null;
        }
        return basket.entrySet();
    }
}
