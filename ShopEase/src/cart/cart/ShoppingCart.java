package cart.cart;

import cart.exceptions.EmptyCartException;
import cart.exceptions.InvalidQuantityException;
import cart.exceptions.OutOfStockException;
import cart.exceptions.ProductNotFoundException;
import cart.products.Product;
import cart.users.Customer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * The shopping cart — the central object customers interact with while browsing.
 *
 * Responsibilities:
 *   - Add, remove, and update products
 *   - Calculate running totals (with and without discounts)
 *   - Guard all operations with appropriate exception handling
 *   - Associate itself with a Customer for personalised display
 *
 * OOP CONCEPTS DEMONSTRATED:
 *   - Encapsulation     : items list is private; only safe access methods are public
 *   - Exception Handling: every mutating operation throws a specific custom exception
 *                         on invalid input; callers must handle them
 *   - Classes/Objects   : a focused service class owning cart-specific state and logic
 *   - Method Overloading: addItem() can be called with or without an explicit quantity
 *   - Polymorphism      : operates on List<CartItem>, which hold Product references
 *                         pointing to any subclass
 */
public class ShoppingCart {

    private final Customer      owner;   // The customer who owns this cart
    private final List<CartItem> items;  // Line items currently in the cart

    // ------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------

    /**
     * Creates an empty shopping cart for the given customer.
     *
     * @param owner the logged-in customer (must not be null)
     */
    public ShoppingCart(Customer owner) {
        if (owner == null)
            throw new IllegalArgumentException("ShoppingCart: owner cannot be null.");
        this.owner = owner;
        this.items = new ArrayList<>();
    }

    // ------------------------------------------------------------------
    // Add methods  (Method Overloading: with vs without explicit quantity)
    // ------------------------------------------------------------------

    /**
     * Adds {@code quantity} units of {@code product} to the cart.
     *
     * If the product is already in the cart the quantity is merged rather than
     * creating a duplicate line. Stock availability is checked before the merge.
     *
     * @param product  the product to add
     * @param quantity how many units (must be >= 1)
     * @throws InvalidQuantityException if quantity < 1
     * @throws OutOfStockException      if the requested quantity exceeds available stock
     */
    public void addItem(Product product, int quantity)
            throws InvalidQuantityException, OutOfStockException {

        // --- Guard: quantity must be positive ---
        if (quantity < 1) {
            throw new InvalidQuantityException(quantity);
        }

        // --- Guard: stock availability ---
        if (!product.isInStock(quantity)) {
            throw new OutOfStockException(product.getName(), quantity, product.getStockQuantity());
        }

        // --- Merge if already in cart ---
        Optional<CartItem> existing = findExistingItem(product.getProductId());
        if (existing.isPresent()) {
            CartItem item = existing.get();
            int newTotal = item.getQuantity() + quantity;

            // Re-check stock against the merged quantity
            if (!product.isInStock(newTotal)) {
                throw new OutOfStockException(product.getName(), newTotal, product.getStockQuantity());
            }
            item.setQuantity(newTotal);
        } else {
            items.add(new CartItem(product, quantity));
        }
    }

    /**
     * Overloaded convenience method — adds exactly 1 unit of the given product.
     * Delegates to the full addItem(Product, int) so all guards are applied.
     *
     * @param product the product to add
     * @throws InvalidQuantityException should never trigger (quantity is always 1)
     * @throws OutOfStockException      if there is no stock at all
     */
    public void addItem(Product product)
            throws InvalidQuantityException, OutOfStockException {
        addItem(product, 1);
    }

    // ------------------------------------------------------------------
    // Remove / Update
    // ------------------------------------------------------------------

    /**
     * Removes a product from the cart entirely by its product ID.
     *
     * @param productId ID of the product to remove
     * @throws ProductNotFoundException if no item in the cart has this ID
     */
    public void removeItem(int productId) throws ProductNotFoundException {
        CartItem toRemove = findExistingItem(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        items.remove(toRemove);
    }

    /**
     * Updates the quantity of an existing cart item.
     *
     * @param productId   the product whose quantity should change
     * @param newQuantity the replacement quantity (must be >= 1)
     * @throws ProductNotFoundException if the product is not in the cart
     * @throws InvalidQuantityException if newQuantity < 1
     * @throws OutOfStockException      if newQuantity exceeds available stock
     */
    public void updateQuantity(int productId, int newQuantity)
            throws ProductNotFoundException, InvalidQuantityException, OutOfStockException {

        if (newQuantity < 1) throw new InvalidQuantityException(newQuantity);

        CartItem item = findExistingItem(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        if (!item.getProduct().isInStock(newQuantity)) {
            throw new OutOfStockException(item.getProduct().getName(),
                                          newQuantity,
                                          item.getProduct().getStockQuantity());
        }
        item.setQuantity(newQuantity);
    }

    /** Removes every item from the cart (e.g., after a successful checkout). */
    public void clear() {
        items.clear();
    }

    // ------------------------------------------------------------------
    // Totals and discount calculations
    // ------------------------------------------------------------------

    /**
     * Sums all line-item subtotals to produce the cart total before any discounts.
     */
    public double getTotal() {
        return items.stream()
                    .mapToDouble(CartItem::getSubtotal)
                    .sum();
    }

    /**
     * Returns the total after applying a percentage coupon code discount.
     *
     * @param discountPercent percentage off the entire order (0–100)
     */
    public double getTotalAfterDiscount(double discountPercent) {
        double total = getTotal();
        if (discountPercent <= 0)   return total;
        if (discountPercent >= 100) return 0.0;
        return total * (1.0 - discountPercent / 100.0);
    }

    // ------------------------------------------------------------------
    // Display
    // ------------------------------------------------------------------

    /**
     * Prints the full cart contents and running total to the console.
     *
     * @throws EmptyCartException if called on an empty cart
     */
    public void display() throws EmptyCartException {
        if (items.isEmpty()) throw new EmptyCartException();

        System.out.println("\n  ╔══════════════════════════════════════════════════════════╗");
        System.out.println("  ║            SHOPPING CART — " + owner.getUsername() + "'s Cart");
        System.out.println("  ╠══════════════════════════════════════════════════════════╣");
        items.forEach(System.out::println);
        System.out.println("  ╠══════════════════════════════════════════════════════════╣");
        System.out.printf("  ║  Cart Total                                   $%8.2f   ║%n", getTotal());
        System.out.println("  ╚══════════════════════════════════════════════════════════╝\n");
    }

    // ------------------------------------------------------------------
    // Accessors
    // ------------------------------------------------------------------

    /** Returns an unmodifiable view of the cart items (Encapsulation). */
    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public boolean isEmpty()       { return items.isEmpty(); }
    public int     itemCount()     { return items.size(); }
    public Customer getOwner()     { return owner; }

    // ------------------------------------------------------------------
    // Private helpers
    // ------------------------------------------------------------------

    /** Linear search for a cart item matching the given product ID. */
    private Optional<CartItem> findExistingItem(int productId) {
        return items.stream()
                    .filter(ci -> ci.getProduct().getProductId() == productId)
                    .findFirst();
    }
}
