package cart.exceptions;

/**
 * Thrown when an operation that requires a non-empty cart (e.g., checkout, display)
 * is attempted on an empty cart.
 *
 * OOP CONCEPTS: Custom Exception, Inheritance (extends Exception)
 */
public class EmptyCartException extends Exception {

    public EmptyCartException() {
        super("Your shopping cart is empty. Please add items before proceeding.");
    }
}
