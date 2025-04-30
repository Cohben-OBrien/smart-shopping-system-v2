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

    public boolean update(int quantity) {
        this.quantity += quantity;
        if (this.quantity > this.product.getQuantity()) {
            this.quantity -= quantity;
            return false;
        } else {
            this.total = product.getPrice() * quantity;
            return true;
        }
    }

    public Product getProduct() {
        return product;
    }

    public boolean setQuantity(int quantity) {
        if (quantity <= this.product.getQuantity()) {
            this.quantity = quantity;
            return true;
        } else {
            return false;
        }
    }

    public int getQuantity() {
        return quantity;
    }
}
