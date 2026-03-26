package cart.products;

/**
 * Represents a food or grocery product.
 *
 * OOP CONCEPTS DEMONSTRATED:
 *   - Inheritance    : extends Product
 *   - Polymorphism   : overrides getCategory() and getDetails()
 *   - Encapsulation  : food-specific fields are private
 *   - Constructors   : two overloaded constructors
 */
public class Food extends Product {

    // Food-specific attributes
    private final String  weightGrams;  // e.g., "500g", "1kg"
    private final boolean isOrganic;
    private final String  expiryDate;   // format: YYYY-MM-DD

    // ------------------------------------------------------------------
    // Constructors  (Overloading)
    // ------------------------------------------------------------------

    /** Full constructor with explicit product ID. */
    public Food(int productId, String name, double price, int stockQuantity,
                String weightGrams, boolean isOrganic, String expiryDate) {
        super(productId, name, price, stockQuantity);
        this.weightGrams = weightGrams;
        this.isOrganic   = isOrganic;
        this.expiryDate  = expiryDate;
    }

    /** Auto-ID constructor. */
    public Food(String name, double price, int stockQuantity,
                String weightGrams, boolean isOrganic, String expiryDate) {
        super(name, price, stockQuantity);
        this.weightGrams = weightGrams;
        this.isOrganic   = isOrganic;
        this.expiryDate  = expiryDate;
    }

    // ------------------------------------------------------------------
    // Polymorphic method implementations
    // ------------------------------------------------------------------

    @Override
    public String getCategory() {
        return "Food & Grocery";
    }

    /**
     * Returns Food-specific attributes.
     * Demonstrates polymorphism: same method call, entirely different output
     * compared to Electronics.getDetails() or Clothing.getDetails().
     */
    @Override
    public String getDetails() {
        return String.format("Weight: %-6s | Organic: %-3s | Expires: %s",
                             weightGrams,
                             isOrganic ? "Yes" : "No",
                             expiryDate);
    }

    @Override
    public String toString() {
        return super.toString() + "\n        " + getDetails();
    }

    // ------------------------------------------------------------------
    // Getters
    // ------------------------------------------------------------------
    public String  getWeightGrams() { return weightGrams; }
    public boolean isOrganic()      { return isOrganic; }
    public String  getExpiryDate()  { return expiryDate; }
}
