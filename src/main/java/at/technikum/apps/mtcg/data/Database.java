package at.technikum.apps.mtcg.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    // TODO: Put connection parameters in external file in gitignore
    private static final String URL = "jdbc:postgresql://localhost:5432/mtcg";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "changeit#1";


    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
