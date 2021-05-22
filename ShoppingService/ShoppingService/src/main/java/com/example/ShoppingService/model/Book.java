package com.example.ShoppingService.model;

public class Book extends BookWithoutStock{

    private int stock;

    public Book(){

    }

    public Book(String isbn, String title,int stock){
        this.isbn = isbn;
        this.title = title;
        this.stock = stock;
    }


    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
