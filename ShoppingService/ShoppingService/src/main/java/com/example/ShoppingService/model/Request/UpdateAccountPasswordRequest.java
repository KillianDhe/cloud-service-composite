package com.example.ShoppingService.model.Request;

public class UpdateAccountPasswordRequest {
    private String login;
    private String actualPassword;
    private String newPassword;
    public String getLogin() {
        return login;
    }

    public UpdateAccountPasswordRequest() {
    }

    public void setLogin(String login) {
        this.login = login;
    }


    public String getActualPassword() {
        return actualPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
