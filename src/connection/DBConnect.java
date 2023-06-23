package connection;

import java.sql.*;

public class DBConnect {
    public Connection conn;
    public Statement stat;
    public ResultSet result;
    public PreparedStatement pstat;

    public DBConnect() {
        try{
            //Laptop rizky
            //String url = "jdbc:sqlserver://LAPTOP-H6F615RS:1433;databaseName=Project-Supermarket;integratedSecurity=true;";
            //Database Utama
            //String url = "jdbc:sqlserver://10.8.10.160:1433;databaseName=Project-Supermarket;integratedSecurity=false; user=sa; password=polman";
            //laptop anggun

            //ini dari rizky

            //laptop jilbran
            //ini dari Branda
            String url = "jdbc:sqlserver://UPTIF-141;database=Project-Supermarket;user=sa; password=polman";
            //Jangan di ganti ganti okehh??

            conn = DriverManager.getConnection(url);
            stat = conn.createStatement();

        }catch (Exception e)
        {
            System.out.println("Error saat connect database: "+e.getMessage());
        }
    }

    public static void main(String[] args) {
        DBConnect connection = new DBConnect();
        System.out.println("Connection Berhasil");
    }
}
