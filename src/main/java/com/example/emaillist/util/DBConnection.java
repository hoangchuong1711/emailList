package com.example.emaillist.util;

import java.sql.Connection;
import java.sql.SQLException;

public class DBConnection {

    public static Connection getConnection()
            throws SQLException {

        ConnectionPool pool =
                ConnectionPool.getInstance();

        return pool.getConnection();
    }
}