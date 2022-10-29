package ru.netology.data;
import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;

public class SQL {
    String url = System.getProperty("db.url");
    String user = System.getProperty("db.user");
    String password = System.getProperty("db.password");

    @SneakyThrows
    public String getStatusPayment() {
        QueryRunner runner = new QueryRunner();
        String dataSQL = "SELECT status FROM payment_entity ORDER BY created DESC LIMIT 1";
        String status;
        try (
                Connection connection = DriverManager.getConnection(
                        url, user, password
                )
        ) {
            status = runner.query(connection, dataSQL, new ScalarHandler<>());
        }
        return status;
    }

    @SneakyThrows
    public long getNumberOfPayment() {
        QueryRunner runner = new QueryRunner();
        String dataSQL = "SELECT COUNT(transaction_id) FROM payment_entity";
        long number = 0;
        try (
                Connection connection = DriverManager.getConnection(
                        url, user, password
                )
        ) {
            number = runner.query(connection, dataSQL, new ScalarHandler<>());
        }
        return number;
    }
}
