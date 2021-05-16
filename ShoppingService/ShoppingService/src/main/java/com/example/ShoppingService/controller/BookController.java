package com.example.ShoppingService.controller;

import com.example.ShoppingService.model.Book;
import com.example.ShoppingService.model.rowMapper.BookRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
public class BookController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

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

    @GetMapping(value = "/getBookByIsbn", produces = "application/json")
    public @ResponseBody Book getBookByIsbn(@RequestParam String isbn) {
        try {
            String sql = "SELECT * FROM BOOKS WHERE ISBN = ?";
            return jdbcTemplate.queryForObject(sql, new BookRowMapper(), isbn);
        } catch (Exception e) {
            return null;
        }
    }

    @GetMapping("/getExampleBook")
    public @ResponseBody
    Book getExampleBook() {
        return new Book("123456","Example book",0);
    }


    @GetMapping("/getBook")
    public @ResponseBody
    Book getBook(String isbn) {
        RestTemplate restTemplate = new RestTemplate();
        // Test getBook
        String stockServiceGetStockUrl = "https://stock-dot-projetcloud-313614.ew.r.appspot.com/getStockOfBookByIsbn?isbn={isbn}";
        Integer response = restTemplate.getForObject(stockServiceGetStockUrl,Integer.class,isbn);

        return new Book("123456","Example book",response);
    }

    /**
     * Méthode permettant simplement de tester , à modifier
     * Cette méthode devra contenir du code client pour appeler StocvkService
     * et certainement utililser un objet avec le stock du livre
     * @param isbn
     * @return
     */
    @GetMapping("/getStockOfBookByIsbn")
    public @ResponseBody
    String getStockOfBookByIsbn(@RequestParam String isbn) {
        return "Stock of " + isbn + ": 8";
    }

}
