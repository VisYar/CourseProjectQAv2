package ru.netology.data;

import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.*;

public class SQL {
    private static QueryRunner runner = new QueryRunner();

    private static String url = System.getProperty("db.url");
    private static String user = System.getProperty("db.user");
    private static String password = System.getProperty("db.password");

    @SneakyThrows
    public static Connection connection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            conn = DriverManager.getConnection(System.getProperty("db.urlpostgresql"), user, password);
        }
        return conn;
    }

    @SneakyThrows
    public static void clear() {
        val deletePayment = "DELETE FROM payment_entity;";
        val deleteCredit = "DELETE FROM credit_request_entity;";
        val deleteOrder = "DELETE FROM order_entity;";
        runner.update(connection(), deletePayment);
        runner.update(connection(), deleteCredit);
        runner.update(connection(), deleteOrder);
    }

    @SneakyThrows
    public static String getStatusPayment() {
        val sql = "SELECT status FROM payment_entity;";
        return runner.query(connection(), sql, new ScalarHandler<>());
    }

    @SneakyThrows
    public static String getStatusCredit() {
        val status = "SELECT status FROM credit_request_entity;";
        return runner.query(connection(), status, new ScalarHandler<>());
    }

    @SneakyThrows
    public static long getPaymentCount() {
        val sql = "SELECT COUNT(id) as count FROM payment_entity;";
        return runner.query(connection(), sql, new ScalarHandler<>());
    }

    @SneakyThrows
    public static long getCreditCount() {
        val sql = "SELECT COUNT(id) as count FROM credit_request_entity;";
        return runner.query(connection(), sql, new ScalarHandler<>());
    }

    @SneakyThrows
    public static long getOrderCount() {
        val sql = "SELECT COUNT(id) as count FROM order_entity;";
        return runner.query(connection(), sql, new ScalarHandler<>());
    }
}
