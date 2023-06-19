package connection;

import java.sql.*;

public class DBConnect {
    public Connection conn;
    public Statement stat;
    public ResultSet result;
    public PreparedStatement pstat;

    public DBConnect() {
        try{
            //Laptop-Pribadi
            //String url = "jdbc:sqlserver://LAPTOP-H6F615RS:1433;databaseName=Apotek;integratedSecurity=true;";
            //Laptop-rizky-Kampus
            String url = "jdbc:sqlserver://10.8.10.160:1433;databaseName=Supermarket023;integratedSecurity=false; user=sa; password=polman";
            //laptop anggun

            //laptop jilbran

            conn = DriverManager.getConnection(url);
            stat = conn.createStatement();

        }catch (Exception e)
        {
            System.out.println("Error saat connect database: "+e.getMessage());
        }
    }

    public static void main(String[] args) {
        DBConnect connect = new DBConnect();
        System.out.println("Connection Berhasil");
    }
}
