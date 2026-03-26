package cart.users;

/**
 * Represents a registered customer in the ShopEase system.
 *
 * OOP CONCEPTS DEMONSTRATED:
 *   - Encapsulation  : all fields private; password is never exposed via a getter
 *   - Constructors   : two overloaded constructors (with/without address)
 *   - Classes/Objects: a focused model class with identity and state
 */
public class Customer {

    private static int idCounter = 100;  // auto-increment for runtime registrations

    private final int    customerId;
    private       String username;
    private       String email;
    private       String password;       // intentionally no getter — never expose passwords
    private       String deliveryAddress;

    // ------------------------------------------------------------------
    // Constructors  (Overloading)
    // ------------------------------------------------------------------

    /**
     * Full constructor — used when loading from a data source with a pre-assigned ID.
     */
    public Customer(int customerId, String username, String email,
                    String password, String deliveryAddress) {
        validateUsername(username);
        validateEmail(email);

        this.customerId      = customerId;
        this.username        = username;
        this.email           = email;
        this.password        = password;
        this.deliveryAddress = deliveryAddress;
    }

    /**
     * Auto-ID constructor — used when registering a new customer at runtime.
     * Delegates to the full constructor via this().
     */
    public Customer(String username, String email, String password, String deliveryAddress) {
        this(++idCounter, username, email, password, deliveryAddress);
    }

    // ------------------------------------------------------------------
    // Business logic
    // ------------------------------------------------------------------

    /**
     * Validates a login attempt.
     * Passwords are compared directly here; a real system would use hashed comparison.
     *
     * @param inputPassword the password the customer typed
     * @return true if it matches the stored password
     */
    public boolean checkPassword(String inputPassword) {
        return this.password != null && this.password.equals(inputPassword);
    }

    // ------------------------------------------------------------------
    // Getters (password intentionally absent — Encapsulation)
    // ------------------------------------------------------------------
    public int    getCustomerId()      { return customerId; }
    public String getUsername()        { return username; }
    public String getEmail()           { return email; }
    public String getDeliveryAddress() { return deliveryAddress; }

    // ------------------------------------------------------------------
    // Setters with validation
    // ------------------------------------------------------------------
    public void setDeliveryAddress(String address) {
        if (address == null || address.isBlank())
            throw new IllegalArgumentException("Delivery address cannot be blank.");
        this.deliveryAddress = address;
    }

    public void setEmail(String email) {
        validateEmail(email);
        this.email = email;
    }

    @Override
    public String toString() {
        return String.format("Customer[%d] %s <%s>", customerId, username, email);
    }

    // ------------------------------------------------------------------
    // Private validation
    // ------------------------------------------------------------------
    private static void validateUsername(String username) {
        if (username == null || username.isBlank())
            throw new IllegalArgumentException("Username cannot be blank.");
    }

    private static void validateEmail(String email) {
        if (email == null || !email.contains("@"))
            throw new IllegalArgumentException("Invalid email address: " + email);
    }
}
