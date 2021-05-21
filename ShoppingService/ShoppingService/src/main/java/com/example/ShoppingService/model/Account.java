package com.example.ShoppingService.model;

import java.sql.Types;

public class Account {
    private int id;
    private String login;
    private String password;

    public Account() {

    }

    public Account(int id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Object[] SQLParams() {
        return new Object[] { this.getLogin(), this.getPassword() };
    }

    public int[] SQLTypes() {
        return new int[] { Types.VARCHAR, Types.VARCHAR };
    }
}
