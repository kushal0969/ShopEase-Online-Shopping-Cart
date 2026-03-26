package cart.exceptions;

/**
 * Thrown when a requested quantity exceeds the available stock for a product.
 *
 * OOP CONCEPTS: Custom Exception, Encapsulation (carries structured error context),
 *               Inheritance (extends Exception)
 */
public class OutOfStockException extends Exception {

    private final String productName;
    private final int    requested;
    private final int    available;

    public OutOfStockException(String productName, int requested, int available) {
        super(String.format(
            "'%s' has insufficient stock. Requested: %d, Available: %d.",
            productName, requested, available
        ));
        this.productName = productName;
        this.requested   = requested;
        this.available   = available;
    }

    public String getProductName() { return productName; }
    public int    getRequested()   { return requested; }
    public int    getAvailable()   { return available; }
}
