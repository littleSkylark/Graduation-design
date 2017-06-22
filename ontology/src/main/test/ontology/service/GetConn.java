package ontology.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by skylark on 2017/6/20.
 */
public class GetConn {
    static String url = "jdbc:mysql://localhost:3306/daa";
    static String username = "root";
    static String password = "";

    public static Connection getCon() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(url, username, password);
        } catch (SQLException se) {
            System.out.println("数据库连接失败！");
            se.printStackTrace();
        }
        return con;
    }
}
