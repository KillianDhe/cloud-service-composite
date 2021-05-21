package com.example.ShoppingService.model;

public class Order {
        private int id;
        private String isbn;
        private int quantity;
        private int account;

        public Order() {

        }

        public Order(int id, String isbn, int quantity) {
            this.id = id;
            this.isbn = isbn;
            this.quantity = quantity;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getIsbn() {
            return isbn;
        }

        public void setIsbn(String isbn) {
            this.isbn = isbn;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public int getAccount() {
            return account;
        }

        public void setAccount(int account) {
            this.account = account;
        }
}
