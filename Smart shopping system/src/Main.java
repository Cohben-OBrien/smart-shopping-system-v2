public class Main {
    public static void main(String[] args) {
        // Create an InventoryManager instance to manage products and sales
        InventoryManager manager = new InventoryManager();

        // Add some initial sample products
        manager.addProduct(new smartshop.Product(1, "Socks", 12.0f, 24));
        manager.addProduct(new smartshop.Product(2, "Boxers", 25.0f, 6));

        // Create and launch the MainFrame GUI window
        new MainFrame(manager);  // This will launch the GUI
    }
}










