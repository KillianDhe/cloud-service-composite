package com.example.ShoppingService.model;

public class Book {

    private String isbn;

    private String title;

    private int stock;

    public Book(){

    }

    public Book(String isbn, String title,int stock){
        this.isbn = isbn;
        this.title = title;
        this.stock = stock;
    }


    public void setTitle(String title){
        this.title = title ;
    }
    
    public String getIsbn(){
        return isbn;
    }
    
    public void setIsbn(String isbn){
        this.isbn = isbn ;
    }

    public String getTitle() {
        return title;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
