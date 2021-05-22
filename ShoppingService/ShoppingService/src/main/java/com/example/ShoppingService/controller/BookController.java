package com.example.ShoppingService.controller;

import com.example.ShoppingService.Exceptions.BookNotFoundException;
import com.example.ShoppingService.model.Account;
import com.example.ShoppingService.model.Book;
import com.example.ShoppingService.model.BookWithoutStock;
import com.example.ShoppingService.model.Order;
import com.example.ShoppingService.model.Request.BuyBookRequest;
import com.example.ShoppingService.model.Request.CreateAccountRequest;
import com.example.ShoppingService.model.rowMapper.AccountRowMapper;
import com.example.ShoppingService.model.rowMapper.BookRowMapper;
import com.example.ShoppingService.model.rowMapper.BookRowWithoutStockMapper;
import com.example.ShoppingService.model.rowMapper.BookWithoutStockRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;

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
    public @ResponseBody
    List<BookWithoutStock> getallbooks() {
        try {
            String sql = "SELECT * FROM books";
            return jdbcTemplate.query(sql, new BookWithoutStockRowMapper());


        }catch (EmptyResultDataAccessException emptyResultDataAccessException ){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucun livre trouvé");
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"erreur interne de la lors de la récuperation de la lise de tous les livres, veuillez réessayer ulterieurement.");
        }
    }

    /***
     * Retourne un livre avec son stock (obtenu depuis le service stock)
     * @param isbn
     * @return
     */
    @GetMapping(value = "/getBookByIsbn", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Book getBookByIsbn(@RequestParam String isbn) {
        try {
            boolean isbnExist = isIsbnExist(isbn);
            if(isbnExist){
                String sql = "SELECT isbn,title FROM BOOKS WHERE ISBN = ?";
                Book book;
                try{
                     book =  jdbcTemplate.queryForObject(sql, new BookRowWithoutStockMapper(), isbn);
                }
                catch (EmptyResultDataAccessException emptyResultDataAccessException){
                    throw new BookNotFoundException(isbn);
                }

                Integer stockBook = restTemplate.getForObject(URLStock,Integer.class,isbn);
                book.setStock(stockBook);
                return book;
            }
            else{
                throw new BookNotFoundException(isbn);
            }
        }
        catch (ResponseStatusException | BookNotFoundException responseStatusException){
            throw  responseStatusException;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }
    }

    private boolean isIsbnExist(String isbn) throws DataAccessException {
        try {
            return jdbcTemplate.queryForObject("SELECT EXISTS(SELECT FROM books WHERE isbn = ?)", Boolean.class, isbn);
        }catch (DataAccessException dataAccessException){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Impossible de vérifier si cet isbn existe, veuillez réssayer ultérieurement");
        }
    }

    /**
     * Permet d'acheter un livre ; c'est le service wholesaler qui s'en occupe, il crée une commande et commande des livres si necessaires
     * @param request
     * @return
     */
    @PostMapping(value = "/buyBook", consumes = "application/json")
    public @ResponseBody Order buyBook(@RequestBody BuyBookRequest request) {
        try {
            boolean bookExist = isIsbnExist(request.getIsbn());
            if(!bookExist)
                throw new BookNotFoundException(request.getIsbn());

            String  URLWholesalerbuy = URLBaseWholesaler +"buyBook";

            Order order = restTemplate.postForObject( URLWholesalerbuy, request , Order.class );
            return order;
        }
        catch (BookNotFoundException | ResponseStatusException bookexc) {
            throw  bookexc;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }
    }



}
