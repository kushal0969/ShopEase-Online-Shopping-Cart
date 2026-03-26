package cart.products;

/**
 * Represents a clothing or apparel product.
 *
 * OOP CONCEPTS DEMONSTRATED:
 *   - Inheritance    : extends Product
 *   - Polymorphism   : overrides getCategory() and getDetails()
 *   - Encapsulation  : clothing-specific fields are private
 *   - Constructors   : overloaded to match Product's two construction patterns
 */
public class Clothing extends Product {

    // Clothing-specific attributes
    private final String size;       // e.g., "XS", "S", "M", "L", "XL", "42"
    private final String material;   // e.g., "100% Cotton", "Polyester Blend"
    private final String color;

    // ------------------------------------------------------------------
    // Constructors  (Overloading)
    // ------------------------------------------------------------------

    /** Full constructor with explicit product ID. */
    public Clothing(int productId, String name, double price,
                    int stockQuantity, String size, String material, String color) {
        super(productId, name, price, stockQuantity);
        this.size     = size;
        this.material = material;
        this.color    = color;
    }

    /** Auto-ID constructor. */
    public Clothing(String name, double price,
                    int stockQuantity, String size, String material, String color) {
        super(name, price, stockQuantity);
        this.size     = size;
        this.material = material;
        this.color    = color;
    }

    // ------------------------------------------------------------------
    // Polymorphic method implementations
    // ------------------------------------------------------------------

    @Override
    public String getCategory() {
        return "Clothing";
    }

    /**
     * Returns Clothing-specific attributes.
     * Runtime polymorphism: iterating a List<Product> and calling getDetails()
     * on a Clothing object produces this output, not Electronics' or Food's.
     */
    @Override
    public String getDetails() {
        return String.format("Size: %-4s | Material: %-20s | Color: %s",
                             size, material, color);
    }

    @Override
    public String toString() {
        return super.toString() + "\n        " + getDetails();
    }

    // ------------------------------------------------------------------
    // Getters
    // ------------------------------------------------------------------
    public String getSize()     { return size; }
    public String getMaterial() { return material; }
    public String getColor()    { return color; }
}
