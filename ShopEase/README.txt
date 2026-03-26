╔══════════════════════════════════════════════════════════════╗
║         SHOPEASE — Online Shopping Cart System               ║
║         Console-Based Java OOP Assignment Project            ║
╚══════════════════════════════════════════════════════════════╝

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 QUICK START
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 Requires: JDK 17 or later  (https://adoptium.net)

 Windows  :  Double-click  compile_and_run_windows.bat
 Mac/Linux:  Run  ./compile_and_run_mac_linux.sh

 Manual compile (from project root):
   mkdir out
   javac -d out src/cart/exceptions/*.java src/cart/users/*.java \
         src/cart/products/*.java src/cart/cart/*.java \
         src/cart/checkout/*.java src/cart/utils/*.java src/Main.java
   cd out && java Main

 IntelliJ IDEA / VS Code:
   1. Open the ShopEase/ folder as a project
   2. Mark src/ as the Sources Root
   3. Run Main.java

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 DEMO LOGIN CREDENTIALS
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
   alice / pass123
   bob   / bob456
   (Or register a new account from the menu)

 DEMO COUPON CODES
   SAVE10  — 10% off orders over $50
   SAVE20  — 20% off orders over $200
   FLAT30  — $30 flat off orders over $100
   WELCOME — $5 flat off any order (no minimum)

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 PROJECT STRUCTURE
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 ShopEase/
 ├── src/
 │   ├── Main.java                          ← Entry point + all menu logic
 │   └── cart/
 │       ├── products/
 │       │   ├── Product.java               ← Abstract base (Encapsulation, Polymorphism)
 │       │   ├── Electronics.java           ← Extends Product (Inheritance, Override)
 │       │   ├── Clothing.java              ← Extends Product (Inheritance, Override)
 │       │   ├── Food.java                  ← Extends Product (Inheritance, Override)
 │       │   └── ProductCatalog.java        ← Inventory store + search/filter
 │       ├── cart/
 │       │   ├── CartItem.java              ← Product + quantity pair
 │       │   └── ShoppingCart.java          ← Core cart (add/remove/update/total)
 │       ├── checkout/
 │       │   ├── Order.java                 ← Immutable order snapshot + receipt
 │       │   ├── CouponCode.java            ← Discount codes (% and flat overloads)
 │       │   └── CheckoutService.java       ← Orchestrates checkout + order history
 │       ├── users/
 │       │   └── Customer.java              ← Registered customer model
 │       ├── exceptions/
 │       │   ├── OutOfStockException.java   ← Custom: stock insufficient
 │       │   ├── ProductNotFoundException.java ← Custom: ID not found
 │       │   ├── InvalidQuantityException.java ← Custom: qty < 1
 │       │   ├── EmptyCartException.java    ← Custom: checkout on empty cart
 │       │   └── InvalidCouponException.java← Custom: bad/expired/min-not-met coupon
 │       └── utils/
 │           ├── ConsoleInput.java          ← Safe, validated scanner wrapper
 │           └── ConsoleDisplay.java        ← Consistent formatted output
 ├── compile_and_run_windows.bat
 ├── compile_and_run_mac_linux.sh
 └── README.txt

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 OOP CONCEPTS — WHERE EACH IS DEMONSTRATED
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

 1. CLASSES & OBJECTS
    Every .java file defines a class. Objects are instantiated at runtime:
    ProductCatalog (seeds product objects), ShoppingCart, Order, Customer,
    CouponCode, CartItem — all instantiated and interacting in Main.java.

 2. CONSTRUCTORS (Overloading)
    Product  : Product(int id, String, double, int)  vs  Product(String, double, int)
    Order    : Order(..., discountAmount, ...)        vs  Order(...) [no discount]
    CouponCode: CouponCode(code, percent, min)        vs  CouponCode(code, flat, min, true)
    Customer : Customer(int id, ...)                  vs  Customer(String, ...)
    Electronics/Clothing/Food: same two-pattern overloads as Product.

 3. ENCAPSULATION
    - All fields in every class are private.
    - Getters expose read-only access; setters validate before mutating.
    - Customer has no getPassword() — the field is intentionally unexposed.
    - ProductCatalog.getAllProducts() returns an unmodifiable list.
    - ShoppingCart.getItems() returns an unmodifiable list.
    - Order's item list is wrapped in Collections.unmodifiableList() at construction.

 4. INHERITANCE
    Product (abstract)
      └── Electronics    (brand, warrantyMonths)
      └── Clothing       (size, material, color)
      └── Food           (weightGrams, isOrganic, expiryDate)
    Exception (Java built-in)
      └── OutOfStockException, ProductNotFoundException, InvalidQuantityException,
          EmptyCartException, InvalidCouponException

 5. POLYMORPHISM (Runtime)
    - ProductCatalog stores List<Product> holding Electronics, Clothing, and Food objects.
    - Calling p.getCategory() or p.getDetails() on each list element dispatches
      to the correct subclass method at runtime — the caller never checks the type.
    - ProductCatalog.display() loops over products and calls toString() + getDetails()
      — each prints different output depending on the actual object type.

 6. METHOD OVERLOADING
    - Product.getPriceAfterDiscount(double percent)
      Product.getPriceAfterDiscount(double amount, boolean isFlat)
    - ShoppingCart.addItem(Product product)
      ShoppingCart.addItem(Product product, int quantity)
    - All constructor pairs listed under point 2 above.

 7. METHOD OVERRIDING
    - getCategory()  : abstract in Product, overridden in each subclass.
    - getDetails()   : abstract in Product, overridden in each subclass.
    - toString()     : overridden in Product (replaces Object default),
                       then further overridden in Electronics, Clothing, Food
                       (calls super.toString() and appends subclass detail).

 8. EXCEPTION HANDLING
    Five custom exception classes, all used with try-catch in Main.java:
    - OutOfStockException      : ShoppingCart.addItem(), updateQuantity()
    - ProductNotFoundException : ProductCatalog.findById(), ShoppingCart.removeItem()
    - InvalidQuantityException : ShoppingCart.addItem(), updateQuantity()
    - EmptyCartException       : ShoppingCart.display(), CheckoutService.processCheckout()
    - InvalidCouponException   : CheckoutService.validateCoupon()
    Main.java has one try-catch block per handler method, each catching only
    the specific exceptions it expects — not a blanket catch(Exception).

 9. PACKAGES
    cart.products   — Product hierarchy and catalog (domain model)
    cart.cart       — Cart item and cart state management
    cart.checkout   — Order, coupon, and checkout orchestration
    cart.users      — Customer identity and authentication
    cart.exceptions — All custom exception types (single responsibility)
    cart.utils      — Cross-cutting utilities (input, display)

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 FEATURES
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  ✓ User login and registration with validation
  ✓ Full product catalog: 15 products across 3 subclass types
  ✓ Browse all / search by name / filter by category
  ✓ Add, remove, and update cart items
  ✓ Stock management (auto-deducted on checkout)
  ✓ Percentage and flat-amount coupon codes
  ✓ Three payment methods
  ✓ Formatted order receipt with grand total
  ✓ Per-user order history
  ✓ Graceful error handling at every user input point
