package com.example.ShoppingService.controller;

import com.example.ShoppingService.Exceptions.BookNotFoundException;
import com.example.ShoppingService.model.Account;
import com.example.ShoppingService.model.Book;
import com.example.ShoppingService.model.Order;
import com.example.ShoppingService.model.Request.BuyBookRequest;
import com.example.ShoppingService.model.Request.CreateAccountRequest;
import com.example.ShoppingService.model.rowMapper.AccountRowMapper;
import com.example.ShoppingService.model.rowMapper.BookRowMapper;
import com.example.ShoppingService.model.rowMapper.BookRowWithoutStockMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class BookController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RestTemplate restTemplate;

    private final String URLStock = "https://stock-dot-projetcloud-313614.ew.r.appspot.com/getStockOfBookByIsbn?isbn={isbn}";
    private final String URLBaseWholesaler = "https://shielded-earth-62387.herokuapp.com/";

    /**
     * Retourne tous les livres existants en base;
     * @return
     */
    @GetMapping(value = "/getallbooks", produces = "application/json")
    public @ResponseBody List<Book> getallbooks() {
        try {
            String sql = "SELECT * FROM books";
            return jdbcTemplate.query(sql, new BookRowWithoutStockMapper());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"erreur interne de la lors de la récuperation de la lise de tous les livres, veuillez réessayer ulterieurement.");
        }
    }

    @GetMapping(value = "/getBookByIsbn", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Book getBookByIsbn(@RequestParam String isbn) {
        try {
            boolean isbnExist = isIsbnExist(isbn);
            if(isbnExist){
                String sql = "SELECT isbn,title FROM BOOKS WHERE ISBN = ?";
                Book book =  jdbcTemplate.queryForObject(sql, new BookRowWithoutStockMapper(), isbn);

                Integer stockBook = restTemplate.getForObject(URLStock,Integer.class,isbn);
                book.setStock(stockBook);
                return book;
            }
            else{
                throw new BookNotFoundException(isbn);
            }
        }
        catch (BookNotFoundException bookexc) {
            throw  bookexc;
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }
    }

    private boolean isIsbnExist(String isbn) throws Exception {
        return jdbcTemplate.queryForObject("SELECT EXISTS(SELECT FROM books WHERE isbn = ?)", Boolean.class, isbn);
    }


    @PostMapping(value = "/buyBook", consumes = "application/json")
    public @ResponseBody Order buyBook(@RequestBody BuyBookRequest request) {
        try {
            Book bookToBuy = getBookByIsbn(request.getIsbn());

            String  URLWholesalerbuy = URLBaseWholesaler +"buyBook?isbn={isbn}?quantity={quantity}?account={account)";

            Map<String, String> urlParameters = new HashMap<>();
            urlParameters.put("account", Integer.toString(request.getAccountId()));
            urlParameters.put("quantity", Long.toString(request.getQuantity()));
            urlParameters.put("isbn", request.getIsbn());

           Order order = restTemplate.getForObject(URLWholesalerbuy, Order.class,urlParameters);
           return order;
        }
        catch (BookNotFoundException bookexc) {
            throw  bookexc;
        }
        catch (ResponseStatusException responseEx){
            throw  responseEx;
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"erreur inconnue, veuillez réessayer ulterieureent");
        }
    }



}
