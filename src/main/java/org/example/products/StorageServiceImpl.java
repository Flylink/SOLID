package org.example.products;

import java.util.*;

public class StorageServiceImpl implements StorageService {

    private String name;
    private final Map<Item, Integer> storage = new HashMap<>();

    public StorageServiceImpl(String name) {
        this.name = name;
    }

    @Override
    public boolean addItem(Item item, int quantity) {
        if (quantity < 0) {
            return false;
        }
        if (storage.containsKey(item)) {
            storage.replace(item, storage.get(item) + quantity);
        } else {
            item.setId(generateItemId());
            storage.put(item, quantity);
        }
        return true;
    }

    @Override
    public boolean takeItem(Item item, int quantity) {
        boolean result = true;
        if (storage.containsKey(item)) {
            if (storage.get(item) >= quantity) {
                storage.replace(item, storage.get(item) - quantity);
            } else {
                System.out.println("Недостаточно предметов в \"" + this.name + "\" Только " + storage.get(item) + " доступен.");
                result = false;
            }
        } else {
            System.out.println("Хранилище \"" + name + "\" не содержит " + item.getName());
            result = false;
        }
        return result;
    }

    @Override
    public Map.Entry<Item, Integer> getItemEntryById(int id) {
        Map.Entry<Item, Integer> itemEntry = null;
        Optional<Map.Entry<Item, Integer>> optItemEntry = storage.entrySet().stream().filter(s -> s.getKey().getId() == id).findFirst();
        if (optItemEntry.isPresent()) {
            itemEntry = optItemEntry.get();
        }
        return itemEntry;
    }

    @Override
    public Set<Map.Entry<Item, Integer>> getStorageContents() {
        return storage.entrySet();
    }

    private int generateItemId() {
        Optional<Item> maxId = storage.keySet().stream().max((o1, o2) -> {
            if (o1.getId() == o2.getId()) {
                return 0;
            } else {
                return o1.getId() > o2.getId() ? 1 : -1;
            }
        });
        int newId = 1;
        if (maxId.isPresent()) {
            newId = maxId.get().getId() + 1;
        }
        return newId;
    }
}