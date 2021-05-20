package com.StockService.StockService.controller;

import com.StockService.StockService.model.rowMapper.BookRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@RestController
public class StockController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/getStockOfBookByIsbn")
    public @ResponseBody
    Integer getStockOfBookByIsbn(@RequestParam String isbn) {
        if(isbn == null){
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "donnes moi un isbn !");
        }
        try {
            String sql = "SELECT stock FROM books WHERE isbn = ?";
            int stock = jdbcTemplate.queryForObject(sql,Integer.class,isbn);
            return stock;
        }catch (Exception e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,"requet sql cassée");
        }
    }

    @GetMapping(value = "/db", produces = "application/json")
    public @ResponseBody String db() {
        try {
            String sql = "CREATE TABLE IF NOT EXISTS books (isbn varchar primary key, title varchar, stock integer );";
            String sql2 = "ALTER ROLE wbjxiprofndwic CONNECTION LIMIT -1";
             jdbcTemplate.execute(sql2);
            int rows = jdbcTemplate.update(sql);
        } catch (Exception e) {
            return e.toString();
        }
        return "ok";
    }


}
