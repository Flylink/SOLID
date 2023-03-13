package org.example.products;

import java.util.Map;
import java.util.Set;

public interface StorageService {

    boolean addItem(Item item, int quantity);

    boolean takeItem(Item item, int quantity);

    Map.Entry<Item, Integer> getItemEntryById(int id);

    Set<Map.Entry<Item, Integer>> getStorageContents();
}
