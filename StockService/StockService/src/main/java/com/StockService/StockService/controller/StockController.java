package com.StockService.StockService.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@RestController
public class StockController {

    @GetMapping("/getStockOfBookByIsbn")
    public @ResponseBody
    Integer getStockOfBookByIsbn(@RequestParam String isbn) {
        if(isbn == null){
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "donnes moi un isbn !");
        }
       return 8;
    }

}
