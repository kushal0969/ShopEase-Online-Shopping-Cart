package cart.cart;

import cart.products.Product;

/**
 * Represents a single line item in the shopping cart.
 *
 * A CartItem pairs a Product reference with the quantity the customer wants.
 * It is intentionally kept lightweight — it owns no business logic beyond
 * computing its own subtotal.
 *
 * OOP CONCEPTS DEMONSTRATED:
 *   - Encapsulation  : fields are private; quantity is mutable via a validated setter
 *   - Classes/Objects: a small, focused class with a single clear responsibility
 *   - Polymorphism   : holds a Product reference that can point to any subclass,
 *                      so getName() / getPrice() / getDetails() dispatch polymorphically
 */
public class CartItem {

    private final Product product;   // The item being purchased (any subclass of Product)
    private       int     quantity;  // How many units the customer wants

    // ------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------

    /**
     * @param product  the product to purchase (must not be null)
     * @param quantity units requested (must be >= 1)
     */
    public CartItem(Product product, int quantity) {
        if (product == null)
            throw new IllegalArgumentException("CartItem: product cannot be null.");
        if (quantity < 1)
            throw new IllegalArgumentException("CartItem: quantity must be at least 1.");

        this.product  = product;
        this.quantity = quantity;
    }

    // ------------------------------------------------------------------
    // Business logic
    // ------------------------------------------------------------------

    /**
     * Returns the total cost for this line item (unit price × quantity).
     */
    public double getSubtotal() {
        return product.getPrice() * quantity;
    }

    /**
     * Increases or decreases the quantity for this item.
     * Validates that the new value is >= 1.
     *
     * @param newQuantity the replacement quantity
     */
    public void setQuantity(int newQuantity) {
        if (newQuantity < 1)
            throw new IllegalArgumentException(
                "CartItem: quantity must be at least 1. Given: " + newQuantity);
        this.quantity = newQuantity;
    }

    // ------------------------------------------------------------------
    // Display
    // ------------------------------------------------------------------

    /**
     * Produces a fixed-width receipt line for console output.
     */
    @Override
    public String toString() {
        return String.format("  %-34s x%-3d  @  $%7.2f  =  $%8.2f",
                             product.getName(),
                             quantity,
                             product.getPrice(),
                             getSubtotal());
    }

    // ------------------------------------------------------------------
    // Getters
    // ------------------------------------------------------------------
    public Product getProduct()  { return product; }
    public int     getQuantity() { return quantity; }
}
