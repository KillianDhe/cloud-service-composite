package com.example.ShoppingService.controller;

import com.example.ShoppingService.Exceptions.BookNotFoundException;
import com.example.ShoppingService.model.Book;
import com.example.ShoppingService.model.Order;
import com.example.ShoppingService.model.Request.BuyBookRequest;
import com.example.ShoppingService.model.Request.CreateAccountRequest;
import com.example.ShoppingService.model.rowMapper.BookRowMapper;
import com.example.ShoppingService.model.rowMapper.BookRowWithoutStockMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

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
            String sql = "SELECT * FROM BOOKS";
            return jdbcTemplate.query(sql, new BookRowMapper());
        } catch (Exception e) {
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR,"erreur interne de la lors de la récuperation de la lise de tous les livres, veuillez réessayer ulterieurement.");
        }
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


    @GetMapping("/BookNotFoundError")
    public @ResponseBody
    void BookNotFoundError() {
        throw new BookNotFoundException("isbntest");
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

    @PostMapping(value = "/createAccount", consumes = "application/json")
    public @ResponseBody int buyBook(@RequestBody CreateAccountRequest request) {
        try {
            String sql = "INSERT INTO accounts (login, password) VALUES (?,?);";
           // String passhashed =  new DigestUtils("SHA3-256").digestAsHex(request.getPassword());

            int rows = jdbcTemplate.update(sql, request.getLogin());
            if (rows == 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Impossible d'inserer un account dans la base");
            }
            //TODO get l'identifiant generé à partir du login unique et le donner au client;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }

        return 0;
    }

}
