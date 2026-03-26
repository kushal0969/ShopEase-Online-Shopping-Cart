package cart.checkout;

import cart.cart.CartItem;
import cart.users.Customer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

/**
 * An immutable snapshot of a successfully placed order.
 *
 * Once an order is created it must not be modified — it represents a historical
 * record of what was purchased, at what prices, and when. Every field is therefore
 * final or stored in an unmodifiable list.
 *
 * OOP CONCEPTS DEMONSTRATED:
 *   - Encapsulation  : all fields final/private; the item list is unmodifiable
 *   - Classes/Objects: a focused, immutable value-object
 *   - Constructors   : two overloads (with and without coupon discount)
 */
public class Order {

    /** Status lifecycle: CONFIRMED -> PROCESSING -> SHIPPED -> DELIVERED (or CANCELLED) */
    public enum Status { CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED }

    // Auto-incrementing order counter (shared across all orders in the session)
    private static int orderCounter = 5000;

    private final int           orderId;
    private final Customer      customer;
    private final List<CartItem> items;         // snapshot — items list is unmodifiable
    private final double        subtotal;       // pre-discount total
    private final double        discountAmount; // dollar value deducted
    private final double        grandTotal;     // amount actually charged
    private final String        paymentMethod;
    private final String        placedAt;       // timestamp
    private       Status        status;         // mutable — can advance through lifecycle

    // ------------------------------------------------------------------
    // Constructors  (Overloading: with discount vs without discount)
    // ------------------------------------------------------------------

    /**
     * Full constructor — records an order with a discount applied.
     *
     * @param customer      the purchasing customer
     * @param items         the cart items at the moment of purchase
     * @param subtotal      total before discount
     * @param discountAmount dollar amount deducted by coupon
     * @param paymentMethod e.g. "Credit Card", "eSewa", "Cash on Delivery"
     */
    public Order(Customer customer, List<CartItem> items,
                 double subtotal, double discountAmount, String paymentMethod) {

        this.orderId        = ++orderCounter;
        this.customer       = customer;
        this.items          = Collections.unmodifiableList(items);  // Encapsulation: freeze the list
        this.subtotal       = subtotal;
        this.discountAmount = discountAmount;
        this.grandTotal     = Math.max(0.0, subtotal - discountAmount);
        this.paymentMethod  = paymentMethod;
        this.status         = Status.CONFIRMED;
        this.placedAt       = LocalDateTime.now()
                              .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * Overloaded convenience constructor — no discount applied.
     * Delegates to the full constructor with discountAmount = 0.
     */
    public Order(Customer customer, List<CartItem> items,
                 double subtotal, String paymentMethod) {
        this(customer, items, subtotal, 0.0, paymentMethod);
    }

    // ------------------------------------------------------------------
    // State mutation (status is the only mutable field post-construction)
    // ------------------------------------------------------------------

    /**
     * Advances the order to the next status in the lifecycle.
     * Cancelled orders cannot be reactivated.
     *
     * @param newStatus the next status
     */
    public void updateStatus(Status newStatus) {
        if (this.status == Status.CANCELLED)
            throw new IllegalStateException("Cannot update a cancelled order.");
        this.status = newStatus;
    }

    // ------------------------------------------------------------------
    // Receipt display
    // ------------------------------------------------------------------

    /**
     * Prints a formatted receipt to the console.
     * The receipt is the primary end-user facing output for a completed order.
     */
    public void printReceipt() {
        System.out.println("\n  ╔══════════════════════════════════════════════════════════╗");
        System.out.println("  ║                     ORDER RECEIPT                        ║");
        System.out.println("  ╠══════════════════════════════════════════════════════════╣");
        System.out.printf ("  ║  Order ID     : #%d%n",   orderId);
        System.out.printf ("  ║  Customer     : %s%n",    customer.getUsername());
        System.out.printf ("  ║  Email        : %s%n",    customer.getEmail());
        System.out.printf ("  ║  Deliver To   : %s%n",    customer.getDeliveryAddress());
        System.out.printf ("  ║  Placed At    : %s%n",    placedAt);
        System.out.printf ("  ║  Payment      : %s%n",    paymentMethod);
        System.out.printf ("  ║  Status       : %s%n",    status);
        System.out.println("  ╠══════════════════════════════════════════════════════════╣");
        System.out.println("  ║  ITEMS ORDERED:                                          ║");
        items.forEach(System.out::println);
        System.out.println("  ╠══════════════════════════════════════════════════════════╣");
        System.out.printf ("  ║  Subtotal     :                              $%8.2f   ║%n", subtotal);
        if (discountAmount > 0) {
            System.out.printf("  ║  Discount     :                             -$%8.2f   ║%n", discountAmount);
        }
        System.out.printf ("  ║  GRAND TOTAL  :                              $%8.2f   ║%n", grandTotal);
        System.out.println("  ╚══════════════════════════════════════════════════════════╝\n");
    }

    // ------------------------------------------------------------------
    // Getters
    // ------------------------------------------------------------------
    public int           getOrderId()       { return orderId; }
    public Customer      getCustomer()      { return customer; }
    public List<CartItem> getItems()        { return items; }
    public double        getSubtotal()      { return subtotal; }
    public double        getDiscountAmount(){ return discountAmount; }
    public double        getGrandTotal()    { return grandTotal; }
    public String        getPaymentMethod() { return paymentMethod; }
    public String        getPlacedAt()      { return placedAt; }
    public Status        getStatus()        { return status; }
}
