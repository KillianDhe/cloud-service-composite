package com.example.ShoppingService.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Livre non trouvé")
public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(String isbn) {
        super("Livre non trouvé pour l'isbn: " + isbn);
    }
}
