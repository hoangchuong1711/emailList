package com.example.emaillist.util;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool {

    private static final ConnectionPool instance =
            new ConnectionPool();

    private final DataSource dataSource;

    // Constructor
    private ConnectionPool() {

        try {

            InitialContext context =
                    new InitialContext();

            dataSource = (DataSource) context.lookup(
                    "java:comp/env/jdbc/email_list_db"
            );

        } catch (NamingException e) {

            throw new IllegalStateException(
                    "Cannot initialize Connection Pool",
                    e
            );
        }
    }

    // Lấy đối tượng ConnectionPool
    public static ConnectionPool getInstance() {

        return instance;
    }

    // Lấy một kết nối từ pool
    public Connection getConnection()
            throws SQLException {

        return dataSource.getConnection();
    }

    // Trả kết nối về pool
    public void freeConnection(Connection conn)
            throws SQLException {

        if (conn != null) {

            conn.close();
        }
    }
}