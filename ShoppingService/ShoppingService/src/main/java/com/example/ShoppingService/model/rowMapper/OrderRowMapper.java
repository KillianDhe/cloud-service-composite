package com.example.ShoppingService.model.rowMapper;

import com.example.ShoppingService.model.Order;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderRowMapper implements RowMapper<Order> {

    @Override
    public Order mapRow(ResultSet resultSet, int i) throws SQLException {
        Order order = new Order();

        order.setId(resultSet.getInt("ID"));
        order.setIsbn(resultSet.getString("ISBN"));
        order.setQuantity(resultSet.getInt("QUANTITY"));
        order.setAccount(resultSet.getInt("ACCOUNT"));

        return order;
    }
}
