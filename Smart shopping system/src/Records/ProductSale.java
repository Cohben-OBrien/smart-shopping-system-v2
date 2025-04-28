package Records;

import Product.Product;

public class ProductSale {
    private Product product;
    private int quantity;
    private double total;
    private int saleId;

    public ProductSale(Product product, int quantity, double total) {
        this.product = product;
        this.quantity = quantity;
        this.total = total;
    }

    public ProductSale(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.total = product.getPrice() * quantity;
        this.saleId = 1; // temp
    }

    public void update(int quantity) {
        this.quantity += quantity;
        this.total = product.getPrice() * quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }





}
