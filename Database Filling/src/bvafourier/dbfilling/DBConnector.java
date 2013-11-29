package bvafourier.dbfilling;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        String host = "dbstud.cosy.sbg.ac.at";
        String port = "5432";
        String database = "bvafourier";
        String pwd = "achu9Phu";
        String user = "agruschi";
        String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;
        return DriverManager.getConnection(url, user, pwd);
    }
}
