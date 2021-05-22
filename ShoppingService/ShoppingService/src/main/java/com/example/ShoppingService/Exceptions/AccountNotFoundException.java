package com.example.ShoppingService.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException() {
        super("Aucun account trouvé");
    }

    public AccountNotFoundException(int id) {
        super("Account non trouvé pour l'id : " + id);
    }

    public AccountNotFoundException(String message) {
        super(message);
    }

}
