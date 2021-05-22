package com.example.ShoppingService.controller;

import com.example.ShoppingService.Exceptions.AccountNotFoundException;
import com.example.ShoppingService.Exceptions.OrderNotFoundException;
import com.example.ShoppingService.model.Account;
import com.example.ShoppingService.model.Order;
import com.example.ShoppingService.model.Request.CreateAccountRequest;
import com.example.ShoppingService.model.rowMapper.AccountRowMapper;
import com.example.ShoppingService.model.rowMapper.OrderRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.websocket.server.PathParam;
import java.util.List;

@RestController
public class OrderController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping(value = "/getOrder/{orderId}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Order getOrder(@PathVariable int orderId) {
        try {
            String sql = "SELECT * FROM orders WHERE id = ?";
            Order order = jdbcTemplate.queryForObject(sql, new OrderRowMapper(), orderId);
            if (order == null) {
                throw new OrderNotFoundException(orderId);
            }
            return order;
        } catch (EmptyResultDataAccessException e) {
            throw new OrderNotFoundException(orderId);
        } catch (OrderNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Une erreur interne inconnue est survenu, veuillez réessayer ulterieurement");
        }
    }

    @GetMapping(value = "/getAllMyOrders/{accountId}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    List<Order> getAllMyOrders(@PathVariable int accountId) {
        try {
            String sql = "SELECT * FROM ORDERS where account = ? ";
            List<Order> orders = jdbcTemplate.query(sql, new OrderRowMapper(),accountId);
            if (orders.size() <= 0) {
                throw new OrderNotFoundException();
            }
            return orders;
        } catch (EmptyResultDataAccessException e) {
            throw new OrderNotFoundException();
        } catch (OrderNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Une erreur interne inconnue est survenu, veuillez réessayer ulterieurement");
        }
    }
}
