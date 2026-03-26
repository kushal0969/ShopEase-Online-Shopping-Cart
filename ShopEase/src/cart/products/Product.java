package cart.products;

/**
 * Abstract base class representing a generic product in the ShopEase catalog.
 *
 * DESIGN RATIONALE:
 *   This class is abstract because no product in the real system exists without
 *   a specific type (Electronics, Clothing, Food). Shared behaviour (pricing,
 *   stock management, discount logic) lives here to avoid duplication, while
 *   category-specific attributes are delegated to concrete subclasses.
 *
 * OOP CONCEPTS DEMONSTRATED:
 *   - Encapsulation  : all fields are private; access is via validated getters/setters
 *   - Inheritance    : Electronics, Clothing, Food all extend this class
 *   - Polymorphism   : getCategory() and getDetails() declared abstract here,
 *                      overridden with distinct behaviour in each subclass
 *   - Constructors   : two overloaded constructors (explicit ID vs auto-generated ID)
 *   - Method Overload: getPriceAfterDiscount() accepts percentage OR flat amount
 */
public abstract class Product {

    // ------------------------------------------------------------------
    // Private fields  (Encapsulation: direct mutation from outside is blocked)
    // ------------------------------------------------------------------
    private final int    productId;      // Immutable once assigned
    private       String name;
    private       double price;
    private       int    stockQuantity;

    /** Auto-incrementing counter ensures unique IDs for runtime-created products. */
    private static int idCounter = 1000;

    // ------------------------------------------------------------------
    // Constructors  (Overloading: two ways to construct a product)
    // ------------------------------------------------------------------

    /**
     * Primary constructor — caller supplies an explicit product ID.
     * Useful when loading products from a data source with pre-assigned IDs.
     *
     * @param productId     unique identifier
     * @param name          display name (must not be blank)
     * @param price         unit price in USD (must be >= 0)
     * @param stockQuantity units available (must be >= 0)
     */
    public Product(int productId, String name, double price, int stockQuantity) {
        validateName(name);
        validatePrice(price);
        validateStock(stockQuantity);

        this.productId     = productId;
        this.name          = name;
        this.price         = price;
        this.stockQuantity = stockQuantity;
    }

    /**
     * Convenience constructor — product ID is auto-generated.
     * Delegates to the primary constructor (constructor chaining with this()).
     *
     * @param name          display name
     * @param price         unit price in USD
     * @param stockQuantity units available
     */
    public Product(String name, double price, int stockQuantity) {
        this(++idCounter, name, price, stockQuantity);
    }

    // ------------------------------------------------------------------
    // Abstract methods  (Polymorphism: subclasses must provide concrete behaviour)
    // ------------------------------------------------------------------

    /**
     * Returns the human-readable category label for this product type.
     * Example return values: "Electronics", "Clothing", "Food & Grocery"
     */
    public abstract String getCategory();

    /**
     * Returns a formatted string describing category-specific attributes.
     * This is the main runtime-polymorphism demonstration: calling getDetails()
     * on a List<Product> produces a different result depending on the actual type.
     *
     * Example:
     *   Electronics -> "Brand: Sony | Warranty: 24 months"
     *   Clothing    -> "Size: M | Material: Cotton"
     *   Food        -> "Weight: 500g | Organic: Yes | Expires: 2026-12-31"
     */
    public abstract String getDetails();

    // ------------------------------------------------------------------
    // Concrete (shared) behaviour
    // ------------------------------------------------------------------

    /**
     * Returns true if at least {@code requestedQty} units are in stock.
     */
    public boolean isInStock(int requestedQty) {
        return stockQuantity >= requestedQty;
    }

    /**
     * Reduces stock after a confirmed purchase.
     * Pre-condition: caller must verify isInStock() before calling this.
     */
    public void deductStock(int quantity) {
        this.stockQuantity -= quantity;
    }

    /**
     * Restores stock, for example when an order is cancelled.
     */
    public void restoreStock(int quantity) {
        this.stockQuantity += quantity;
    }

    // ------------------------------------------------------------------
    // Discount logic  (Method Overloading: percentage vs flat-amount)
    // ------------------------------------------------------------------

    /**
     * Calculates the sale price after a percentage discount.
     *
     * @param discountPercent percentage to deduct (0–100)
     * @return discounted price
     */
    public double getPriceAfterDiscount(double discountPercent) {
        if (discountPercent < 0 || discountPercent > 100)
            throw new IllegalArgumentException(
                "Discount percentage must be between 0 and 100. Given: " + discountPercent);
        return price * (1.0 - discountPercent / 100.0);
    }

    /**
     * Overloaded variant: calculates the sale price after a flat dollar discount.
     * The boolean parameter {@code isFlat} exists solely to differentiate this
     * signature from the percentage overload at the call site.
     *
     * @param discountAmount fixed dollar amount to subtract
     * @param isFlat         must be {@code true}; signals this is a flat discount
     * @return discounted price (minimum $0.00)
     */
    public double getPriceAfterDiscount(double discountAmount, boolean isFlat) {
        if (!isFlat) {
            return getPriceAfterDiscount(discountAmount); // fall back to % overload
        }
        return Math.max(0.0, price - discountAmount);
    }

    // ------------------------------------------------------------------
    // toString  (Method Overriding: replaces Object's default implementation)
    // ------------------------------------------------------------------

    /**
     * Produces a fixed-width single-line catalog row for console display.
     * Overriding Object.toString() is a Java best-practice for readable output.
     */
    @Override
    public String toString() {
        return String.format(
            "[%d] %-30s | %-16s | $%7.2f | Stock: %3d",
            productId, name, getCategory(), price, stockQuantity
        );
    }

    // ------------------------------------------------------------------
    // Getters  (Encapsulation: read access only, no mutation)
    // ------------------------------------------------------------------
    public int    getProductId()     { return productId; }
    public String getName()          { return name; }
    public double getPrice()         { return price; }
    public int    getStockQuantity() { return stockQuantity; }

    // ------------------------------------------------------------------
    // Setters with validation  (Encapsulation: write access with guard checks)
    // ------------------------------------------------------------------

    /** Updates the product name after validating it is not blank. */
    public void setName(String name) {
        validateName(name);
        this.name = name;
    }

    /** Updates the price after validating it is non-negative. */
    public void setPrice(double price) {
        validatePrice(price);
        this.price = price;
    }

    // ------------------------------------------------------------------
    // Private validation helpers
    // ------------------------------------------------------------------
    private static void validateName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Product name cannot be blank.");
    }

    private static void validatePrice(double price) {
        if (price < 0)
            throw new IllegalArgumentException("Product price cannot be negative. Given: " + price);
    }

    private static void validateStock(int stock) {
        if (stock < 0)
            throw new IllegalArgumentException("Stock quantity cannot be negative. Given: " + stock);
    }
}
