package com.example.ShoppingService.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException() {
        super("Aucune commande trouvé");
    }

    public OrderNotFoundException(int id) {
        super("Commande non trouvé pour l'id : " + id);
    }
}
