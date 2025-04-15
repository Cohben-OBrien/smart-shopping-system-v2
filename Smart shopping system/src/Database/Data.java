package Database;

import smartshop.Product;

import java.sql.*;
import java.util.ArrayList;

public class Data {
    public static Connection connection;
    public static Statement cur;

    public static void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:Smart shopping system/Database/Shop.sqlite");
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

   public static void addProduct(smartshop.Product product) throws SQLException {
        String query = "INSERT INTO items (id, name, price, stock) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, product.getId());
        ps.setString(2, product.getName());
        ps.setFloat(3, product.getPrice());
        ps.setInt(4, product.getQuantity());

        ps.executeUpdate();

        String name = product.getName() + "_" + product.getId();
        String query2 = "CREATE TABLE " + name + " (sale_id INTEGER, sale_quantity INTEGER, sale_totel real)";
        PreparedStatement ps2 = connection.prepareStatement(query2);
        ps2.executeUpdate();
    }


   //add filter

}
