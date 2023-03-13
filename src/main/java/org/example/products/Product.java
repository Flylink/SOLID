package org.example.products;

public class Product extends Item {
    public Product(String name) {
        this.name = name;
    }

    @Override
    protected void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getRetailPrice() {
        return retailPrice;
    }

    @Override
    public void setRetailPrice(int retailPrice) {
        this.retailPrice = retailPrice;
    }
}
