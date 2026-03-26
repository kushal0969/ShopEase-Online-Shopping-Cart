package cart.exceptions;

/**
 * Thrown when a quantity value is zero, negative, or otherwise invalid.
 *
 * OOP CONCEPTS: Custom Exception, Inheritance (extends Exception)
 */
public class InvalidQuantityException extends Exception {

    private final int givenQuantity;

    public InvalidQuantityException(int givenQuantity) {
        super("Invalid quantity: " + givenQuantity + ". Quantity must be at least 1.");
        this.givenQuantity = givenQuantity;
    }

    public int getGivenQuantity() { return givenQuantity; }
}
