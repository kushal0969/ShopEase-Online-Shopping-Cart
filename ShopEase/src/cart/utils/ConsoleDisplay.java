package cart.utils;

/**
 * Utility class for consistent, formatted console output throughout the application.
 *
 * Centralising display logic here means that changing the look of the app
 * (e.g., switching from box-drawing chars to plain ASCII) requires editing
 * only this one class.
 *
 * OOP CONCEPTS: Classes/Objects (utility class), Encapsulation (static-only)
 */
public final class ConsoleDisplay {

    private static final int WIDTH = 62;

    // Prevent instantiation
    private ConsoleDisplay() {}

    /** Prints a bold header banner. */
    public static void printHeader(String title) {
        System.out.println("\n  " + "═".repeat(WIDTH));
        int padding = (WIDTH - title.length()) / 2;
        System.out.println("  " + " ".repeat(Math.max(0, padding)) + title);
        System.out.println("  " + "═".repeat(WIDTH));
    }

    /** Prints a thin section separator. */
    public static void printSeparator() {
        System.out.println("  " + "─".repeat(WIDTH));
    }

    /** Prints a success message with a [✓] prefix. */
    public static void printSuccess(String message) {
        System.out.println("  [✓] " + message);
    }

    /** Prints an error message with a [✗] prefix. */
    public static void printError(String message) {
        System.out.println("  [✗] " + message);
    }

    /** Prints an informational message with an [i] prefix. */
    public static void printInfo(String message) {
        System.out.println("  [i] " + message);
    }

    /** Prints a blank line. */
    public static void printBlankLine() {
        System.out.println();
    }
}
