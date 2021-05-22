package com.StockService.StockService.controller;

import com.StockService.StockService.model.rowMapper.BookRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class StockController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/getStockOfBookByIsbn")
    public @ResponseBody
    Integer getStockOfBookByIsbn(@RequestParam String isbn) {
        if(isbn == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "donnes moi un isbn !");
        }
        if(isbn.equals("")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "donnes moi un isbn non vide !");
        }
        try {
            String sql = "SELECT stock FROM books WHERE isbn = ?";
            int stock = jdbcTemplate.queryForObject(sql,Integer.class,isbn);
            return stock;
        }catch (DataAccessException dataAccessException){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Impossible de récuprer le stock pour ce livre");
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Erreur inconnue, veuillez réessayer ulterieurement");
        }
    }

}
