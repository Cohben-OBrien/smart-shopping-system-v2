/*import smartshop.Product;

import java.sql.*;
import java.util.ArrayList;

public class Data {
    public static Connection connection;
    public static Statement cur;

    public static void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:Database/Shop.sqlite");
        cur =  connection.createStatement();

    }

    public static ArrayList<smartshop.Product> getProducts() throws SQLException {
        connect();

        ArrayList<smartshop.Product> products = new ArrayList<>();

        String query = "SELECT * FROM items";
        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            products.add(new Product(rs.getInt("id"), rs.getString("name"), rs.getFloat("price"), rs.getInt("stock")));
        }

        return products;
    }

    public static void main(String[] args) throws SQLException {
        ArrayList<smartshop.Product> products = getProducts();
        System.out.println(products.get(0).getName());
    }


}
*/