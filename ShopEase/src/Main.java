import cart.cart.ShoppingCart;
import cart.checkout.CheckoutService;
import cart.checkout.CouponCode;
import cart.checkout.Order;
import cart.exceptions.*;
import cart.products.Product;
import cart.products.ProductCatalog;
import cart.users.Customer;
import cart.utils.ConsoleDisplay;
import cart.utils.ConsoleInput;

import java.util.List;

/**
 * ═══════════════════════════════════════════════════════════════
 *   S H O P E A S E  —  Online Shopping Cart System
 *   Console-Based Java Application  |  OOP Assignment Project
 * ═══════════════════════════════════════════════════════════════
 *
 * Entry point for the ShopEase system. This class wires together the
 * service layer (ProductCatalog, CheckoutService) and drives the interactive
 * console menus. All OOP concepts are demonstrated across the package tree —
 * this file keeps the presentation logic only.
 *
 * PACKAGE STRUCTURE:
 *   cart.products   — Product (abstract), Electronics, Clothing, Food, ProductCatalog
 *   cart.cart       — CartItem, ShoppingCart
 *   cart.checkout   — Order, CouponCode, CheckoutService
 *   cart.users      — Customer
 *   cart.exceptions — 5 custom exception classes
 *   cart.utils      — ConsoleInput, ConsoleDisplay
 *
 * HOW TO RUN (from the out/ directory):
 *   java Main
 *
 * DEMO CREDENTIALS:
 *   Username: alice  |  Password: pass123
 *   Username: bob    |  Password: bob456
 *
 * DEMO COUPONS:
 *   SAVE10  — 10% off orders over $50
 *   SAVE20  — 20% off orders over $200
 *   FLAT30  — $30 flat off orders over $100
 *   WELCOME — $5 flat off any order
 */
public class Main {

    // ── Service objects (initialised once, shared across all menu methods) ──
    private static final ProductCatalog  catalog         = new ProductCatalog();
    private static final CheckoutService checkoutService = new CheckoutService();

    // ── Session state ──
    private static Customer      currentUser = null;
    private static ShoppingCart  cart        = null;

    // ═══════════════════════════════════════════════════════════════
    // APPLICATION ENTRY POINT
    // ═══════════════════════════════════════════════════════════════

    public static void main(String[] args) {
        ConsoleDisplay.printHeader("WELCOME TO SHOPEASE");
        System.out.println("  Your one-stop online shopping destination.");
        ConsoleDisplay.printSeparator();

        boolean running = true;
        while (running) {
            if (currentUser == null) {
                running = showAuthMenu();
            } else {
                running = showMainMenu();
            }
        }

        ConsoleInput.close();
        System.out.println("\n  Thank you for shopping with ShopEase. Goodbye!\n");
    }

    // ═══════════════════════════════════════════════════════════════
    // AUTHENTICATION MENU  (shown when no user is logged in)
    // ═══════════════════════════════════════════════════════════════

    private static boolean showAuthMenu() {
        System.out.println("\n  ┌─ MAIN MENU ───────────────────────────────────┐");
        System.out.println("  │  1. Login                                      │");
        System.out.println("  │  2. Register New Account                       │");
        System.out.println("  │  3. Browse Catalog (Guest)                     │");
        System.out.println("  │  0. Exit                                       │");
        System.out.println("  └────────────────────────────────────────────────┘");

        int choice = ConsoleInput.readIntInRange("  Enter choice: ", 0, 3);
        switch (choice) {
            case 1 -> handleLogin();
            case 2 -> handleRegister();
            case 3 -> {
                ConsoleDisplay.printHeader("PRODUCT CATALOG");
                catalog.display();
            }
            case 0 -> { return false; }
        }
        return true;
    }

    // ═══════════════════════════════════════════════════════════════
    // MAIN SHOPPING MENU  (shown when a user is logged in)
    // ═══════════════════════════════════════════════════════════════

    private static boolean showMainMenu() {
        System.out.println("\n  Logged in as: " + currentUser.getUsername()
                           + " | Cart items: " + cart.itemCount());
        System.out.println("  ┌─ SHOPPING MENU ────────────────────────────────┐");
        System.out.println("  │  1. Browse Full Catalog                        │");
        System.out.println("  │  2. Search Products by Name                    │");
        System.out.println("  │  3. Filter Products by Category                │");
        System.out.println("  │  4. Add Item to Cart                           │");
        System.out.println("  │  5. View My Cart                               │");
        System.out.println("  │  6. Update Item Quantity                       │");
        System.out.println("  │  7. Remove Item from Cart                      │");
        System.out.println("  │  8. Checkout                                   │");
        System.out.println("  │  9. View Order History                         │");
        System.out.println("  │  0. Logout                                     │");
        System.out.println("  └────────────────────────────────────────────────┘");

        int choice = ConsoleInput.readIntInRange("  Enter choice: ", 0, 9);
        switch (choice) {
            case 1 -> {
                ConsoleDisplay.printHeader("PRODUCT CATALOG");
                catalog.display();
            }
            case 2 -> handleSearchProducts();
            case 3 -> handleFilterByCategory();
            case 4 -> handleAddToCart();
            case 5 -> handleViewCart();
            case 6 -> handleUpdateQuantity();
            case 7 -> handleRemoveFromCart();
            case 8 -> handleCheckout();
            case 9 -> checkoutService.displayOrderHistory(currentUser);
            case 0 -> handleLogout();
        }
        return true;
    }

    // ═══════════════════════════════════════════════════════════════
    // AUTHENTICATION HANDLERS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Handles login flow.
     * Exception Handling: username not found or wrong password are handled here.
     */
    private static void handleLogin() {
        ConsoleDisplay.printHeader("LOGIN");

        // Pre-seeded demo accounts for easy testing
        System.out.println("  Demo accounts: alice/pass123  |  bob/bob456");
        ConsoleDisplay.printSeparator();

        String username = ConsoleInput.readString("  Username : ");
        String password = ConsoleInput.readString("  Password : ");

        // Try to match against pre-seeded accounts
        // (A real system would have a UserRepository; we keep it simple here)
        Customer found = findDemoUser(username, password);

        if (found != null) {
            currentUser = found;
            cart        = new ShoppingCart(currentUser);
            ConsoleDisplay.printSuccess("Welcome back, " + currentUser.getUsername() + "!");
        } else {
            ConsoleDisplay.printError("Invalid username or password.");
        }
    }

    /**
     * Handles new account registration.
     * Exception Handling: catches IllegalArgumentException from Customer constructor
     *                     (e.g., blank username, invalid email).
     */
    private static void handleRegister() {
        ConsoleDisplay.printHeader("CREATE ACCOUNT");
        try {
            String username = ConsoleInput.readString("  Username         : ");
            String email    = ConsoleInput.readString("  Email            : ");
            String password = ConsoleInput.readString("  Password         : ");
            String address  = ConsoleInput.readString("  Delivery Address : ");

            // Customer constructor throws IllegalArgumentException on invalid input
            currentUser = new Customer(username, email, password, address);
            cart        = new ShoppingCart(currentUser);

            ConsoleDisplay.printSuccess("Account created! Welcome, " + currentUser.getUsername() + "!");

        } catch (IllegalArgumentException e) {
            // Encapsulation: Customer validated its own fields and surfaced this error
            ConsoleDisplay.printError("Registration failed: " + e.getMessage());
        }
    }

    private static void handleLogout() {
        ConsoleDisplay.printInfo("Logging out " + currentUser.getUsername() + "...");
        currentUser = null;
        cart        = null;
        ConsoleDisplay.printSuccess("You have been logged out.");
    }

    // ═══════════════════════════════════════════════════════════════
    // PRODUCT BROWSING HANDLERS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Searches products by keyword.
     * Demonstrates that the catalog's internal list is accessed only through
     * controlled public methods (Encapsulation).
     */
    private static void handleSearchProducts() {
        String keyword = ConsoleInput.readString("  Search keyword: ");
        List<Product> results = catalog.searchByName(keyword);

        if (results.isEmpty()) {
            ConsoleDisplay.printInfo("No products found matching: \"" + keyword + "\"");
        } else {
            System.out.println("\n  Found " + results.size() + " result(s):\n");
            // getDetails() calls dispatch polymorphically based on the actual subclass type
            results.forEach(p -> System.out.println("  " + p));
        }
    }

    /**
     * Filters products by category.
     * The filter list drives a sub-menu so the user picks from valid options.
     */
    private static void handleFilterByCategory() {
        System.out.println("\n  Categories:");
        System.out.println("    1. Electronics");
        System.out.println("    2. Clothing");
        System.out.println("    3. Food & Grocery");

        int choice = ConsoleInput.readIntInRange("  Select category: ", 1, 3);
        String category = switch (choice) {
            case 1 -> "Electronics";
            case 2 -> "Clothing";
            default -> "Food & Grocery";
        };

        List<Product> results = catalog.filterByCategory(category);
        System.out.println("\n  ── " + category.toUpperCase() + " ──");
        results.forEach(p -> System.out.println("  " + p));
    }

    // ═══════════════════════════════════════════════════════════════
    // CART HANDLERS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Adds an item to the cart.
     *
     * Exception Handling (all custom exceptions demonstrated here):
     *   - ProductNotFoundException : if the ID doesn't exist in the catalog
     *   - InvalidQuantityException : if qty < 1
     *   - OutOfStockException      : if requested qty > available stock
     */
    private static void handleAddToCart() {
        catalog.display();
        int productId = ConsoleInput.readInt("  Enter Product ID to add: ");
        int quantity  = ConsoleInput.readInt("  Enter quantity          : ");

        try {
            // Throws ProductNotFoundException if ID not found (Encapsulation: catalog guards itself)
            Product product = catalog.findById(productId);

            // Throws InvalidQuantityException or OutOfStockException
            cart.addItem(product, quantity);

            ConsoleDisplay.printSuccess("Added " + quantity + "x \"" + product.getName() + "\" to cart.");

        } catch (ProductNotFoundException e) {
            ConsoleDisplay.printError(e.getMessage());
        } catch (OutOfStockException e) {
            ConsoleDisplay.printError(e.getMessage());
            ConsoleDisplay.printInfo("Only " + e.getAvailable() + " unit(s) available.");
        } catch (InvalidQuantityException e) {
            ConsoleDisplay.printError(e.getMessage());
        }
    }

    /**
     * Displays the current cart contents.
     * Exception Handling: EmptyCartException is caught and reported gracefully.
     */
    private static void handleViewCart() {
        try {
            cart.display(); // throws EmptyCartException if no items
        } catch (EmptyCartException e) {
            ConsoleDisplay.printInfo(e.getMessage());
        }
    }

    /**
     * Updates the quantity of a cart item.
     * Exception Handling: three different exceptions may surface, each handled distinctly.
     */
    private static void handleUpdateQuantity() {
        try {
            cart.display();
        } catch (EmptyCartException e) {
            ConsoleDisplay.printInfo(e.getMessage());
            return;
        }

        int productId   = ConsoleInput.readInt("  Enter Product ID to update: ");
        int newQuantity = ConsoleInput.readInt("  Enter new quantity         : ");

        try {
            cart.updateQuantity(productId, newQuantity);
            ConsoleDisplay.printSuccess("Quantity updated to " + newQuantity + ".");
        } catch (ProductNotFoundException e) {
            ConsoleDisplay.printError("That product is not in your cart.");
        } catch (InvalidQuantityException e) {
            ConsoleDisplay.printError(e.getMessage());
        } catch (OutOfStockException e) {
            ConsoleDisplay.printError(e.getMessage());
        }
    }

    /**
     * Removes an item from the cart by product ID.
     * Exception Handling: ProductNotFoundException caught and reported.
     */
    private static void handleRemoveFromCart() {
        try {
            cart.display();
        } catch (EmptyCartException e) {
            ConsoleDisplay.printInfo(e.getMessage());
            return;
        }

        int productId = ConsoleInput.readInt("  Enter Product ID to remove: ");
        try {
            cart.removeItem(productId);
            ConsoleDisplay.printSuccess("Item removed from cart.");
        } catch (ProductNotFoundException e) {
            ConsoleDisplay.printError("Product ID " + productId + " is not in your cart.");
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // CHECKOUT HANDLER
    // ═══════════════════════════════════════════════════════════════

    /**
     * Guides the user through the full checkout flow:
     *   1. Display cart
     *   2. Optionally apply a coupon
     *   3. Select payment method
     *   4. Confirm and place order
     *
     * Exception Handling:
     *   - EmptyCartException     : cart must have items
     *   - InvalidCouponException : coupon validation failure
     */
    private static void handleCheckout() {
        // Step 1: Show cart — abort if empty
        try {
            cart.display();
        } catch (EmptyCartException e) {
            ConsoleDisplay.printInfo(e.getMessage());
            return;
        }

        double orderTotal = cart.getTotal();

        // Step 2: Optional coupon
        CouponCode appliedCoupon = null;
        System.out.println("  Available coupons: SAVE10 | SAVE20 | FLAT30 | WELCOME");
        String couponInput = ConsoleInput.readLine("  Enter coupon code (or press Enter to skip): ");

        if (!couponInput.isEmpty()) {
            try {
                // Throws InvalidCouponException if code is invalid/used/below minimum
                appliedCoupon = checkoutService.validateCoupon(couponInput, orderTotal);
                double discount = appliedCoupon.calculateDiscount(orderTotal);
                ConsoleDisplay.printSuccess(
                    "Coupon applied! You save $" + String.format("%.2f", discount)
                    + " → New total: $" + String.format("%.2f", orderTotal - discount)
                );
            } catch (InvalidCouponException e) {
                ConsoleDisplay.printError(e.getMessage());
                ConsoleDisplay.printInfo("Proceeding without a coupon discount.");
            }
        }

        // Step 3: Payment method
        System.out.println("\n  Payment Methods:");
        System.out.println("    1. Credit / Debit Card");
        System.out.println("    2. eSewa Digital Wallet");
        System.out.println("    3. Cash on Delivery");
        int payChoice = ConsoleInput.readIntInRange("  Select payment method: ", 1, 3);
        String paymentMethod = switch (payChoice) {
            case 1 -> "Credit/Debit Card";
            case 2 -> "eSewa";
            default -> "Cash on Delivery";
        };

        // Step 4: Confirmation
        double finalTotal = (appliedCoupon != null)
            ? cart.getTotalAfterDiscount(
                  appliedCoupon.getType() == CouponCode.DiscountType.PERCENTAGE
                      ? appliedCoupon.getValue()
                      : 0)
            : orderTotal;
        // For flat discounts we need a different final calculation path
        if (appliedCoupon != null && appliedCoupon.getType() == CouponCode.DiscountType.FLAT_AMOUNT) {
            finalTotal = orderTotal - appliedCoupon.calculateDiscount(orderTotal);
        }

        System.out.printf("%n  Subtotal  : $%.2f%n", orderTotal);
        if (appliedCoupon != null) {
            System.out.printf("  Discount  : -$%.2f%n", appliedCoupon.calculateDiscount(orderTotal));
        }
        System.out.printf("  You pay   : $%.2f via %s%n%n", finalTotal, paymentMethod);

        String confirm = ConsoleInput.readString("  Confirm order? (yes / no): ");
        if (!confirm.equalsIgnoreCase("yes")) {
            ConsoleDisplay.printInfo("Order cancelled. Your cart is unchanged.");
            return;
        }

        // Step 5: Process checkout
        try {
            Order order = checkoutService.processCheckout(cart, paymentMethod, appliedCoupon);
            order.printReceipt();
            ConsoleDisplay.printSuccess("Order #" + order.getOrderId() + " placed successfully!");
        } catch (EmptyCartException e) {
            // This shouldn't happen here (checked above), but we handle it defensively
            ConsoleDisplay.printError("Unexpected error: " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // DEMO USER STORE  (replaces a UserRepository for simplicity)
    // ═══════════════════════════════════════════════════════════════

    private static final Customer[] DEMO_USERS = {
        new Customer(1, "alice", "alice@shopease.com", "pass123", "123 Thamel St, Kathmandu"),
        new Customer(2, "bob",   "bob@shopease.com",   "bob456",  "45 Lakeside Rd, Pokhara")
    };

    /**
     * Looks up a pre-seeded demo user by username and validates the password.
     * Returns null if no match is found (the caller handles the failure).
     */
    private static Customer findDemoUser(String username, String password) {
        for (Customer user : DEMO_USERS) {
            if (user.getUsername().equalsIgnoreCase(username)
                    && user.checkPassword(password)) {
                return user;
            }
        }
        return null;
    }
}
