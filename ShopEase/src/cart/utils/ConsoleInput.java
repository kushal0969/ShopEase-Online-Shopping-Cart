package cart.utils;

import java.util.Scanner;

/**
 * Utility class providing safe, validated console input methods.
 *
 * All methods in this class are static — there is no need to instantiate it.
 * The single Scanner instance is shared for the lifetime of the application
 * to avoid resource leaks.
 *
 * OOP CONCEPTS: Classes/Objects (utility class with static methods), Encapsulation
 */
public final class ConsoleInput {

    /** Shared scanner — one per application lifetime. */
    private static final Scanner SCANNER = new Scanner(System.in);

    // Private constructor prevents instantiation of this utility class
    private ConsoleInput() {}

    /**
     * Prompts the user and reads a trimmed, non-blank string.
     * Repeats until a non-blank value is entered.
     *
     * @param prompt text displayed before the input cursor
     * @return trimmed non-blank string
     */
    public static String readString(String prompt) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = SCANNER.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("  [!] Input cannot be empty. Please try again.");
        }
    }

    /**
     * Prompts the user and reads a valid integer.
     * Repeats until a valid integer is entered.
     *
     * @param prompt text displayed before the input cursor
     * @return the parsed integer
     */
    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(SCANNER.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  [!] Please enter a whole number.");
            }
        }
    }

    /**
     * Prompts the user and reads a valid integer within the given range [min, max].
     *
     * @param prompt text displayed before the input cursor
     * @param min    inclusive lower bound
     * @param max    inclusive upper bound
     * @return a valid integer in [min, max]
     */
    public static int readIntInRange(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) return value;
            System.out.printf("  [!] Please enter a number between %d and %d.%n", min, max);
        }
    }

    /**
     * Reads any line (may be empty). Useful for optional inputs.
     *
     * @param prompt text displayed before the input cursor
     * @return raw trimmed input (may be empty string)
     */
    public static String readLine(String prompt) {
        System.out.print(prompt);
        return SCANNER.nextLine().trim();
    }

    /** Closes the shared scanner — call once at application shutdown. */
    public static void close() {
        SCANNER.close();
    }
}
