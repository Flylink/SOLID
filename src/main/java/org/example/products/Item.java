package org.example.products;

import java.util.Objects;

public abstract class Item {

    protected int id;
    protected String name;

    protected int retailPrice;

    protected abstract void setId(int id);

    public abstract int getId();

    public abstract void setName(String name);

    public abstract String getName();

    public abstract int getRetailPrice();

    public abstract void setRetailPrice(int retailPrice);

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public boolean equals(Object obj) {
        Item s = (Item) obj;
        return this.getName().equals(s.getName()) && this.getId() == s.getId();
    }
}
