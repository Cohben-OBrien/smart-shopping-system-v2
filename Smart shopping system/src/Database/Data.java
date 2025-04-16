package Database;

import Records.ProductSale;
import Product.Product;
import Records.SalesRecord;
import manager.InventoryManager;

import java.sql.*;
import java.util.ArrayList;

public class Data {
    public static Connection connection;
    public static Statement cur;

    public static void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:Smart shopping system/Database/Shop.sqlite");
        cur =  connection.createStatement();

    }

    public static ArrayList<Product> getProducts() throws SQLException {
        connect();

        ArrayList<Product> products = new ArrayList<>();

        String query = "SELECT * FROM items";
        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            products.add(new Product(rs.getInt("id"), rs.getString("name"), rs.getFloat("price"), rs.getInt("stock")));
        }

        return products;
    }


    public static int get_sale_number() throws SQLException {
        String sql = "SELECT count(*) FROM sales";
        PreparedStatement ps = connection.prepareStatement(sql);

        ResultSet rs = ps.executeQuery();


        return rs.getInt(1);
    }

   public static void addProduct(Product product) throws SQLException {
        String query = "INSERT INTO items (id, name, price, stock) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, product.getId());
        ps.setString(2, product.getName().replace(" ", "_"));
        ps.setFloat(3, product.getPrice());
        ps.setInt(4, product.getQuantity());

        ps.executeUpdate();

        String name = product.getName().replace(" ", "_") + "_" + product.getId();
        String query2 = "CREATE TABLE " + name + " (sale_id INTEGER, sale_quantity INTEGER, sale_totel real)";
        PreparedStatement ps2 = connection.prepareStatement(query2);
        ps2.executeUpdate();
    }

    public static void Add_Sale(ArrayList<ProductSale> Products, SalesRecord sale) throws SQLException {
        double total = 0;

        for (ProductSale productSale : Products) {
            String table = productSale.getProduct().getName().replace(" ", "_") + "_" + productSale.getProduct().getId();
            String query = "INSERT INTO " + table + " VALUES (?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, sale.get_id());
            ps.setInt(2, productSale.getQuantity());
            ps.setDouble(3, productSale.getProduct().getPrice() * productSale.getQuantity());
            ps.executeUpdate();
            total =+ productSale.getProduct().getPrice() * productSale.getQuantity();
        }


        String query = "INSERT INTO sales VALUES (?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, sale.get_id());
        ps.setDouble(2, total);
        ps.setString(3, sale.get_date());
        ps.executeUpdate();
    }

   //add filter

}
