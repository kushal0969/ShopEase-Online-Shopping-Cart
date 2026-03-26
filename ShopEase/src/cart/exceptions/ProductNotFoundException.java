package cart.exceptions;

/**
 * Thrown when a product ID does not match any entry in the catalog or cart.
 *
 * OOP CONCEPTS: Custom Exception, Inheritance (extends Exception)
 */
public class ProductNotFoundException extends Exception {

    private final int productId;

    public ProductNotFoundException(int productId) {
        super("No product found with ID: " + productId);
        this.productId = productId;
    }

    public ProductNotFoundException(String message) {
        super(message);
        this.productId = -1;
    }

    public int getProductId() { return productId; }
}
