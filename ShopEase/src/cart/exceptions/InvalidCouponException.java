package cart.exceptions;

/**
 * Thrown when a coupon code is invalid, expired, already used, or the order
 * does not meet the minimum value requirement.
 *
 * OOP CONCEPTS: Custom Exception, Encapsulation (carries code + reason),
 *               Inheritance (extends Exception)
 */
public class InvalidCouponException extends Exception {

    private final String couponCode;
    private final String reason;

    public InvalidCouponException(String couponCode, String reason) {
        super("Coupon '" + couponCode + "' cannot be applied: " + reason);
        this.couponCode = couponCode;
        this.reason     = reason;
    }

    public String getCouponCode() { return couponCode; }
    public String getReason()     { return reason; }
}
