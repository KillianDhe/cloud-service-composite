package com.example.ShoppingService.model.rowMapper;

import com.example.ShoppingService.model.Book;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BookRowWithoutStockMapper implements RowMapper<Book> {
    @Override
    public Book mapRow(ResultSet resultSet, int i) throws SQLException {
        Book book = new Book();

        book.setIsbn(resultSet.getString("ISBN"));
        book.setTitle(resultSet.getString("TITLE"));

        return book;
    }
}
