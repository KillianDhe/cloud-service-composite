package com.StockService.StockService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@RestController
public class StockController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/getStockOfBookByIsbn")
    public @ResponseBody
    Integer getStockOfBookByIsbn(@RequestParam String isbn) {
        if(isbn == null){
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "donnes moi un isbn !");
        }
       return 8;
    }

    @GetMapping(value = "/db", produces = "application/json")
    public @ResponseBody String db() {
        try {
            String sql = "CREATE TABLE IF NOT EXISTS books (isbn varchar primary key, title varchar, stock integer );";

            int rows = jdbcTemplate.update(sql);
        } catch (Exception e) {
            return e.toString();
        }
        return "ok";
    }


}
