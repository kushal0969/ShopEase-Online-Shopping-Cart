import cart.gui.MainFrame;

import javax.swing.*;

/**
 * Entry point for the ShopEase GUI application.
 *
 * All Swing operations must run on the Event Dispatch Thread (EDT).
 * SwingUtilities.invokeLater() ensures thread safety — this is a Swing best-practice
 * required for correct rendering and event handling.
 *
 * HOW TO COMPILE (from the src/ directory):
 *   javac -d ../out \
 *     cart/exceptions/*.java \
 *     cart/users/*.java \
 *     cart/products/*.java \
 *     cart/cart/*.java \
 *     cart/checkout/*.java \
 *     cart/utils/*.java \
 *     cart/gui/*.java \
 *     MainGUI.java
 *
 * HOW TO RUN (from the out/ directory):
 *   java MainGUI
 */
public class MainGUI {

    public static void main(String[] args) {
        // Schedule UI creation on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new MainFrame();   // constructor shows the window
        });
    }
}
