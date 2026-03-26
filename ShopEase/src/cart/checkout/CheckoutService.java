package cart.checkout;

import cart.cart.CartItem;
import cart.cart.ShoppingCart;
import cart.exceptions.EmptyCartException;
import cart.exceptions.InvalidCouponException;
import cart.users.Customer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Orchestrates the checkout process from cart validation through to order creation.
 *
 * Responsibilities:
 *   - Validate coupon codes
 *   - Compute final totals
 *   - Deduct stock from purchased products
 *   - Create and store Order objects
 *   - Maintain per-customer order history
 *
 * OOP CONCEPTS DEMONSTRATED:
 *   - Exception Handling : throws EmptyCartException and InvalidCouponException
 *                          with try-catch in the calling layer (Main)
 *   - Encapsulation      : coupon registry and order history are private
 *   - Classes/Objects    : a dedicated service class with clear single responsibility
 */
public class CheckoutService {

    /** Available coupons keyed by their uppercased code string. */
    private final Map<String, CouponCode> couponRegistry;

    /** Order history — maps each customer ID to their list of orders. */
    private final Map<Integer, List<Order>> orderHistory;

    // ------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------

    public CheckoutService() {
        this.couponRegistry = new HashMap<>();
        this.orderHistory   = new HashMap<>();
        registerDefaultCoupons();
    }

    // ------------------------------------------------------------------
    // Coupon management
    // ------------------------------------------------------------------

    /**
     * Validates and retrieves a coupon by code string.
     *
     * @param code       the code the customer entered
     * @param orderTotal the current cart total (for minimum-order validation)
     * @return the matching CouponCode
     * @throws InvalidCouponException if the code does not exist, is already used,
     *                                or the order does not meet the minimum value
     */
    public CouponCode validateCoupon(String code, double orderTotal)
            throws InvalidCouponException {

        CouponCode coupon = couponRegistry.get(code.toUpperCase());

        if (coupon == null) {
            throw new InvalidCouponException(code, "Coupon code not found.");
        }
        if (coupon.isUsed()) {
            throw new InvalidCouponException(code, "This coupon has already been used.");
        }
        if (!coupon.isApplicable(orderTotal)) {
            throw new InvalidCouponException(code,
                String.format("Minimum order of $%.2f required (your total: $%.2f).",
                              coupon.getMinimumOrderValue(), orderTotal));
        }
        return coupon;
    }

    // ------------------------------------------------------------------
    // Checkout
    // ------------------------------------------------------------------

    /**
     * Processes a checkout: validates the cart, applies any discount, deducts stock,
     * creates an Order record, and clears the cart.
     *
     * @param cart          the customer's shopping cart (must not be empty)
     * @param paymentMethod selected payment method string
     * @param coupon        an already-validated CouponCode, or null for no discount
     * @return the newly created Order
     * @throws EmptyCartException if the cart has no items
     */
    public Order processCheckout(ShoppingCart cart, String paymentMethod, CouponCode coupon)
            throws EmptyCartException {

        // Guard: cannot check out with an empty cart
        if (cart.isEmpty()) {
            throw new EmptyCartException();
        }

        double subtotal       = cart.getTotal();
        double discountAmount = (coupon != null) ? coupon.calculateDiscount(subtotal) : 0.0;

        // Deduct stock for every item that was purchased
        for (CartItem item : cart.getItems()) {
            item.getProduct().deductStock(item.getQuantity());
        }

        // Snapshot the items (copy the list before clearing the cart)
        List<CartItem> snapshot = new ArrayList<>(cart.getItems());

        // Create the order (uses the two-parameter overload when no discount is applied)
        Order order = (discountAmount > 0)
            ? new Order(cart.getOwner(), snapshot, subtotal, discountAmount, paymentMethod)
            : new Order(cart.getOwner(), snapshot, subtotal, paymentMethod);

        // Mark coupon as used so it cannot be reapplied
        if (coupon != null) coupon.markAsUsed();

        // Persist order in history
        storeOrder(cart.getOwner(), order);

        // Clear cart after successful checkout
        cart.clear();

        return order;
    }

    // ------------------------------------------------------------------
    // Order history
    // ------------------------------------------------------------------

    /**
     * Returns the order history for a given customer.
     *
     * @param customer the customer whose history to retrieve
     * @return unmodifiable list of orders (empty if none placed yet)
     */
    public List<Order> getOrderHistory(Customer customer) {
        return Collections.unmodifiableList(
            orderHistory.getOrDefault(customer.getCustomerId(), Collections.emptyList())
        );
    }

    /**
     * Displays a summary of all past orders for the customer to the console.
     */
    public void displayOrderHistory(Customer customer) {
        List<Order> orders = getOrderHistory(customer);
        if (orders.isEmpty()) {
            System.out.println("  No orders found for " + customer.getUsername() + ".");
            return;
        }
        System.out.println("\n  Order History for " + customer.getUsername() + ":");
        System.out.println("  " + "─".repeat(60));
        for (Order o : orders) {
            System.out.printf("  #%d | %s | $%.2f | %s%n",
                              o.getOrderId(), o.getPlacedAt(),
                              o.getGrandTotal(), o.getStatus());
        }
        System.out.println("  " + "─".repeat(60) + "\n");
    }

    // ------------------------------------------------------------------
    // Private helpers
    // ------------------------------------------------------------------

    private void storeOrder(Customer customer, Order order) {
        orderHistory
            .computeIfAbsent(customer.getCustomerId(), k -> new ArrayList<>())
            .add(order);
    }

    /**
     * Pre-registers a set of demo coupon codes.
     * A production system would load these from a database.
     */
    private void registerDefaultCoupons() {
        // SAVE10 : 10% off orders over $50
        couponRegistry.put("SAVE10",   new CouponCode("SAVE10",  10.0, 50.0));
        // SAVE20 : 20% off orders over $200
        couponRegistry.put("SAVE20",   new CouponCode("SAVE20",  20.0, 200.0));
        // FLAT30 : flat $30 off orders over $100
        couponRegistry.put("FLAT30",   new CouponCode("FLAT30",  30.0, 100.0, true));
        // WELCOME: flat $5 off any order (no minimum)
        couponRegistry.put("WELCOME",  new CouponCode("WELCOME",  5.0, 0.0, true));
    }
}
