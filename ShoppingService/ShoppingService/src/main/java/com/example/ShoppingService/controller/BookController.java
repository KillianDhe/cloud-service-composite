package com.example.ShoppingService.controller;

import com.example.ShoppingService.Exceptions.BookNotFoundException;
import com.example.ShoppingService.model.Book;
import com.example.ShoppingService.model.rowMapper.BookRowMapper;
import com.example.ShoppingService.model.rowMapper.BookRowWithoutStockMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

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
    public @ResponseBody Book getBookByIsbn(@RequestParam String isbn, HttpServletResponse response) {
        try {
            String sql = "SELECT isbn,title FROM BOOKS WHERE ISBN = ?";
            boolean isbnExist = jdbcTemplate.queryForObject("SELECT EXISTS(SELECT FROM books WHERE isbn = ?)", Boolean.class, isbn);
            if(isbnExist){
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
