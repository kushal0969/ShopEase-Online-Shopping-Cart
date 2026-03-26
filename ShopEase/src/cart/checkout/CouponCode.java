package cart.checkout;

/**
 * Represents a promotional coupon code customers can apply at checkout.
 *
 * OOP CONCEPTS DEMONSTRATED:
 *   - Encapsulation  : all fields private
 *   - Constructors   : two overloads — percentage discount vs flat discount
 *   - Classes/Objects: a small, focused value-object
 */
public class CouponCode {

    /** Type of discount this coupon provides. */
    public enum DiscountType { PERCENTAGE, FLAT_AMOUNT }

    private final String       code;
    private final DiscountType type;
    private final double       value;          // percentage (0-100) or dollar amount
    private final double       minimumOrderValue; // coupon only valid above this threshold
    private       boolean      isUsed;         // single-use coupons

    // ------------------------------------------------------------------
    // Constructors  (Overloading: percentage vs flat)
    // ------------------------------------------------------------------

    /**
     * Creates a percentage-based coupon (e.g., "SAVE10" = 10% off).
     *
     * @param code             the promo code string customers type in
     * @param percentageOff    percentage to deduct (0–100)
     * @param minimumOrderValue minimum cart total required to apply this coupon
     */
    public CouponCode(String code, double percentageOff, double minimumOrderValue) {
        this.code              = code.toUpperCase();
        this.type              = DiscountType.PERCENTAGE;
        this.value             = percentageOff;
        this.minimumOrderValue = minimumOrderValue;
        this.isUsed            = false;
    }

    /**
     * Creates a flat-amount coupon (e.g., "FLAT20" = $20 off).
     * The boolean parameter {@code isFlat} distinguishes this overload from
     * the percentage constructor when both have the same numeric types.
     *
     * @param code             the promo code string
     * @param flatAmountOff    dollar amount to deduct
     * @param minimumOrderValue minimum cart total required
     * @param isFlat           must be {@code true}; acts as the overload discriminator
     */
    public CouponCode(String code, double flatAmountOff,
                      double minimumOrderValue, boolean isFlat) {
        this.code              = code.toUpperCase();
        this.type              = DiscountType.FLAT_AMOUNT;
        this.value             = flatAmountOff;
        this.minimumOrderValue = minimumOrderValue;
        this.isUsed            = false;
    }

    // ------------------------------------------------------------------
    // Business logic
    // ------------------------------------------------------------------

    /**
     * Calculates the dollar discount amount to deduct from the given order total.
     *
     * @param orderTotal the cart total before discount
     * @return the dollar amount to subtract (0 if coupon is not applicable)
     */
    public double calculateDiscount(double orderTotal) {
        if (isUsed || orderTotal < minimumOrderValue) return 0.0;

        return switch (type) {
            case PERCENTAGE  -> orderTotal * (value / 100.0);
            case FLAT_AMOUNT -> Math.min(value, orderTotal); // can't discount more than total
        };
    }

    /**
     * Validates whether this coupon can be applied to the given order total.
     *
     * @param orderTotal cart total to check against
     * @return true if the coupon is usable
     */
    public boolean isApplicable(double orderTotal) {
        return !isUsed && orderTotal >= minimumOrderValue;
    }

    /** Marks this coupon as used so it cannot be applied again. */
    public void markAsUsed() {
        this.isUsed = true;
    }

    // ------------------------------------------------------------------
    // Getters
    // ------------------------------------------------------------------
    public String       getCode()              { return code; }
    public DiscountType getType()              { return type; }
    public double       getValue()             { return value; }
    public double       getMinimumOrderValue() { return minimumOrderValue; }
    public boolean      isUsed()               { return isUsed; }

    @Override
    public String toString() {
        String valueStr = (type == DiscountType.PERCENTAGE)
            ? String.format("%.0f%% off", value)
            : String.format("$%.2f off", value);
        return String.format("Coupon [%s]: %s | Min. order: $%.2f | %s",
                             code, valueStr, minimumOrderValue,
                             isUsed ? "USED" : "ACTIVE");
    }
}
