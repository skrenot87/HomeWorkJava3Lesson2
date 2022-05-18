package ru.geekbrains.sqlite;
/*
CREATE TABLE students (
    id    INTEGER PRIMARY KEY AUTOINCREMENT,
    name  TEXT,
    score INTEGER
);
 */


import java.sql.*;

public class JdbcMainApp {
    private static Connection connection;
    private static Statement stmt;
    private static PreparedStatement psInsert;


    public static void main(String[] args) {
        try {
            connect();
            dropAndCreateTable();
            fillTable();
            // preparedStatements();
            // preparedStatementExample();


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    private static void preparedStatementExample() throws SQLException {
        connection.setAutoCommit(false);
        for (int i = 1; i <= 50; i++) {
            // insert into students(name, score) values (?,?);
            psInsert.setString(1, "BOB" + i);
            psInsert.setInt(2, 100);
            psInsert.executeUpdate();
        }
        connection.commit();
    }

    private static void preparedStatements() throws SQLException {
        psInsert = connection.prepareStatement("insert into students(name, score) values (?,?);");
    }

    private static void batchExample() throws SQLException {
        connection.setAutoCommit(false);
        for (int i = 1; i <= 50; i++) {
            stmt.addBatch(String.format("insert into students (name, score) values ('%s', 100);", "Bob #" + i, 100));
        }
        stmt.executeBatch();
        connection.commit();
    }

    private static void dropAndCreateTable() throws SQLException {
        stmt.executeUpdate("drop table if exists students;");
        stmt.executeUpdate("CREATE TABLE students (\n" +
                "    id    INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    name  TEXT,\n" +
                "    score INTEGER\n" +
                ");");
    }

    private static void fillTable() throws SQLException {
        long time = System.currentTimeMillis();
        connection.setAutoCommit(false);
        for (int i = 1; i <= 50; i++) {
            stmt.executeUpdate(String.format("insert into students (name, score) values ('%s', 100);", "Bob #" + i, 100));
        }
        connection.setAutoCommit(true);
        System.out.println("TIME: " + (System.currentTimeMillis() - time));
    }

    private static void clearTableExample() throws SQLException {
        stmt.executeUpdate("delete from students;");
    }

    private static void deleteOneExample() throws SQLException {
        stmt.executeUpdate("delete from students where id = 5;");
    }

    private static void updateExample() throws SQLException {
        updateExample("update students set score = 100 where id > 0;");
    }

    private static void updateExample(String sql) throws SQLException {
        stmt.executeUpdate(sql);
    }

    private static void readExample() throws SQLException {
        try (ResultSet rs = stmt.executeQuery("select * from students where id > 2;")) {
            while (rs.next()) {
                System.out.println(rs.getInt(1) + " " + rs.getString(2) + " " + rs.getInt("score"));
            }
        }
    }

    private static void insertExample() throws SQLException {
        stmt.executeUpdate("insert into students (name, score)  values ('John', 90);");
    }

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:mydatabase.db");
            stmt = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Невозможно подключиться к БД");
        }

    }

    public static void disconnect() {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (psInsert != null) {
                psInsert.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
