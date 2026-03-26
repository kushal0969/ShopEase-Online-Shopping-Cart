package cart.products;

import cart.exceptions.ProductNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages the complete product inventory for the ShopEase store.
 *
 * Responsibilities:
 *   - Acts as the single source of truth for all available products
 *   - Provides search and filter operations
 *   - Pre-loads a realistic sample catalog on construction
 *
 * OOP CONCEPTS DEMONSTRATED:
 *   - Encapsulation  : internal list is private; only unmodifiable views are exposed
 *   - Polymorphism   : the catalog holds Product references, each pointing to a
 *                      concrete subclass — iterating and calling getDetails() or
 *                      getCategory() produces different output per object type
 *   - Classes/Objects: ProductCatalog is a service object with single responsibility
 */
public class ProductCatalog {

    /** Internal store — typed as abstract Product, holds mixed subclasses (Polymorphism). */
    private final List<Product> products;

    // ------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------

    /**
     * Constructs the catalog and immediately seeds it with sample products.
     * The sample data includes Electronics, Clothing, and Food items so that
     * polymorphic behaviour is exercised as soon as the app starts.
     */
    public ProductCatalog() {
        this.products = new ArrayList<>();
        seedCatalog();
    }

    // ------------------------------------------------------------------
    // Public query methods
    // ------------------------------------------------------------------

    /**
     * Returns an unmodifiable view of all products.
     * Callers can read the list but cannot add or remove items directly,
     * preserving encapsulation of the internal list.
     */
    public List<Product> getAllProducts() {
        return Collections.unmodifiableList(products);
    }

    /**
     * Looks up a product by its unique ID.
     *
     * @param productId the ID to find
     * @return the matching Product
     * @throws ProductNotFoundException if no product has the given ID
     */
    public Product findById(int productId) throws ProductNotFoundException {
        return products.stream()
                       .filter(p -> p.getProductId() == productId)
                       .findFirst()
                       .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    /**
     * Case-insensitive name search — returns all products whose name contains the keyword.
     *
     * @param keyword search term (partial match supported)
     * @return possibly-empty list of matching products
     */
    public List<Product> searchByName(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return products.stream()
                       .filter(p -> p.getName().toLowerCase().contains(lowerKeyword))
                       .collect(Collectors.toList());
    }

    /**
     * Filters products by category name (case-insensitive).
     *
     * @param category e.g. "Electronics", "Clothing", "Food & Grocery"
     * @return all products in that category
     */
    public List<Product> filterByCategory(String category) {
        return products.stream()
                       .filter(p -> p.getCategory().equalsIgnoreCase(category))
                       .collect(Collectors.toList());
    }

    /** Returns the total number of distinct products in the catalog. */
    public int size() {
        return products.size();
    }

    // ------------------------------------------------------------------
    // Display helpers
    // ------------------------------------------------------------------

    /**
     * Prints the full catalog to the console, grouped by category.
     * The loop calls p.toString() and p.getDetails() on each Product reference —
     * the actual method executed at runtime depends on the concrete subclass.
     * This is runtime polymorphism in action.
     */
    public void display() {
        String[] categories = {"Electronics", "Clothing", "Food & Grocery"};
        for (String cat : categories) {
            System.out.println("\n  ── " + cat.toUpperCase() + " ──────────────────────────────");
            products.stream()
                    .filter(p -> p.getCategory().equalsIgnoreCase(cat))
                    .forEach(p -> System.out.println("  " + p));
        }
        System.out.println();
    }

    // ------------------------------------------------------------------
    // Private catalog seeding
    // ------------------------------------------------------------------

    /**
     * Loads the initial product inventory.
     * Real systems would read from a database; here we hard-code for demo clarity.
     * Notice that the list holds Product references but stores concrete subclass objects.
     */
    private void seedCatalog() {
        // ----- Electronics -----
        products.add(new Electronics(101, "Samsung Galaxy S24 Ultra",  1199.99, 12, "Samsung", 24));
        products.add(new Electronics(102, "Apple MacBook Air M3",      1299.99,  8, "Apple",   12));
        products.add(new Electronics(103, "Sony WH-1000XM5 Headphones", 349.99, 25, "Sony",    12));
        products.add(new Electronics(104, "Logitech MX Master 3S Mouse",  99.99, 40, "Logitech", 6));
        products.add(new Electronics(105, "Dell 27\" 4K Monitor",       549.99, 10, "Dell",    36));

        // ----- Clothing -----
        products.add(new Clothing(201, "Nike Air Max Running Shoes",    89.99, 30, "42",  "Mesh Fabric",     "White"));
        products.add(new Clothing(202, "Levi's 501 Original Jeans",     59.99, 45, "32",  "100% Denim",      "Indigo Blue"));
        products.add(new Clothing(203, "Adidas Ultraboost 23",         139.99, 20, "44",  "Primeknit+",      "Core Black"));
        products.add(new Clothing(204, "Uniqlo HeatTech Fleece Jacket", 69.99, 35, "L",   "Polyester Fleece","Dark Green"));
        products.add(new Clothing(205, "Lululemon Align Leggings",      98.00, 18, "M",   "Nulu Fabric",     "Heathered Gray"));

        // ----- Food & Grocery -----
        products.add(new Food(301, "Organic Green Tea (50 bags)",  12.99,  80, "100g",  true,  "2026-12-31"));
        products.add(new Food(302, "Lindt 70% Dark Chocolate",      4.99, 150, "100g",  false, "2026-08-30"));
        products.add(new Food(303, "Himalayan Pink Salt",            8.99,  60, "1kg",   false, "2028-01-01"));
        products.add(new Food(304, "Manuka Honey UMF 15+",          34.99,  25, "500g",  true,  "2027-06-15"));
        products.add(new Food(305, "Cold Pressed Olive Oil",        22.99,  40, "750ml", true,  "2026-09-20"));
    }
}
