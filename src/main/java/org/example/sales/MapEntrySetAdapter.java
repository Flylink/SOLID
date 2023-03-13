package org.example.sales;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.example.products.Item;
import org.example.products.Product;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MapEntrySetAdapter extends TypeAdapter<Set<Map.Entry<Item, Integer>>> {
    @Override
    public void write(JsonWriter out, Set<Map.Entry<Item, Integer>> value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.beginArray();
        value.forEach(s -> {
            try {
                out.beginObject();
                out.name("id").value(s.getKey().getId());
                out.name("name").value(s.getKey().getName());
                out.name("retail_price").value(s.getKey().getRetailPrice());
                out.name("quantity").value(s.getValue());
                out.endObject();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });
        out.endArray();
    }

    @Override
    public Set<Map.Entry<Item, Integer>> read(JsonReader in) throws IOException {
        Set<Map.Entry<Item, Integer>> entrySet = new HashSet<>();
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        in.beginArray();
        while (in.hasNext()) {
            entrySet.add(readProduct(in));
        }
        in.endArray();
        return entrySet;
    }

    public Map.Entry<Item, Integer> readProduct(JsonReader in) throws IOException {
        int id = -1;
        String name = null;
        int retailPrice = -1;
        int quantity = -1;
        in.beginObject();
        while (in.hasNext()) {
            String nextName = in.nextName();
            if (name.equals("name")) {
                name = nextName;
            } else if (name.equals("id")) {
                id = in.nextInt();
            } else if (name.equals("retail_price")) {
                retailPrice = in.nextInt();
            } else if (name.equals("quantity")) {
                quantity = in.nextInt();
            } else {
                in.skipValue();
            }
        }
        in.endObject();
        Product p = new Product(name);
        p.setRetailPrice(retailPrice);
        return new AbstractMap.SimpleEntry(p, quantity);
    }
}
