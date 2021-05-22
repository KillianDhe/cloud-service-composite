package com.example.ShoppingService.model.rowMapper;

import com.example.ShoppingService.model.Book;
import com.example.ShoppingService.model.BookWithoutStock;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BookWithoutStockRowMapper implements RowMapper<BookWithoutStock> {

    @Override
    public BookWithoutStock mapRow(ResultSet resultSet, int i) throws SQLException {
        BookWithoutStock bookWithoutStock = new BookWithoutStock();

        bookWithoutStock.setIsbn(resultSet.getString("ISBN"));
        bookWithoutStock.setTitle(resultSet.getString("TITLE"));

        return bookWithoutStock;
    }
}
