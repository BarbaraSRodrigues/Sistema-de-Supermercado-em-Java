package com.example.ootrabalhopratico.controller.records;

public record AddedProduct (String name, Double unitPrice, int quantity, Double finalPrice){
    public AddedProduct(String name, Double unitPrice, int quantity){
        this(name, unitPrice, quantity, unitPrice*quantity);
    }

    @Override
    public String toString() {
        return name
                + "\tR$"
                + unitPrice
                + "           \tQuantidade "
                + quantity
                + "           \tSubtotal: R$ "
                + finalPrice;
    }
}
