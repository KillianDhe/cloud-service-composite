package com.example.ShoppingService.model.rowMapper;

import com.example.ShoppingService.model.Account;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountRowMapper implements RowMapper<Account> {

    @Override
    public Account mapRow(ResultSet resultSet, int i) throws SQLException {
        Account book = new Account();

        book.setId(resultSet.getInt("ID"));
        book.setLogin(resultSet.getString("LOGIN"));
        book.setPassword(resultSet.getString("PASSWORD"));

        return book;
    }
}
