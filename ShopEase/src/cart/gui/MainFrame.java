package cart.gui;

import cart.cart.ShoppingCart;
import cart.checkout.CheckoutService;
import cart.checkout.Order;
import cart.products.ProductCatalog;
import cart.users.Customer;

import javax.swing.*;
import java.awt.*;

/**
 * The root JFrame for the ShopEase GUI application.
 *
 * Responsibilities:
 *   - Sets up the main window (title, size, look-and-feel)
 *   - Shows the LoginPanel first; swaps to the main layout on successful login
 *   - Hosts the SidebarPanel (nav) + a CardLayout content area (catalog, cart, orders)
 *   - Coordinates all inter-panel communication via listener callbacks
 *
 * OOP:
 *   - Extends JFrame (Inheritance)
 *   - Implements LoginPanel.LoginListener and CartPanel.CheckoutListener (Polymorphism)
 *   - Composes ProductCatalog, CheckoutService, ShoppingCart (Composition)
 *   - Demonstrates all custom exceptions through the panels it hosts
 */
public class MainFrame extends JFrame
        implements LoginPanel.LoginListener, CartPanel.CheckoutListener {

    // ── Shared services (created once, injected into panels) ──────
    private final ProductCatalog  catalog         = new ProductCatalog();
    private final CheckoutService checkoutService = new CheckoutService();

    // ── Session state ─────────────────────────────────────────────
    private Customer     currentUser;
    private ShoppingCart cart;

    // ── Panel references ──────────────────────────────────────────
    private SidebarPanel sidebarPanel;
    private CatalogPanel catalogPanel;
    private CartPanel    cartPanel;
    private OrdersPanel  ordersPanel;
    private JPanel       contentArea;   // CardLayout host

    // ── Card names ────────────────────────────────────────────────
    private static final String CARD_LOGIN   = "login";
    private static final String CARD_APP     = "app";
    private static final String CARD_CATALOG = "catalog";
    private static final String CARD_CART    = "cart";
    private static final String CARD_ORDERS  = "orders";

    public MainFrame() {
        configureWindow();
        showLoginScreen();
    }

    // ── Window setup ──────────────────────────────────────────────

    private void configureWindow() {
        setTitle("ShopEase — Online Shopping Cart");
        setSize(GuiTheme.WINDOW_WIDTH, GuiTheme.WINDOW_HEIGHT);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Use the OS system look-and-feel for native widgets
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Falls back to default Metal L&F — not a fatal error
        }

        // Main container uses CardLayout to swap between login and the app shell
        setLayout(new CardLayout());
    }

    // ── Login screen ──────────────────────────────────────────────

    private void showLoginScreen() {
        LoginPanel loginPanel = new LoginPanel(this);
        add(loginPanel, CARD_LOGIN);
        ((CardLayout) getContentPane().getLayout()).show(getContentPane(), CARD_LOGIN);
        setVisible(true);
    }

    // ── LoginListener callback ────────────────────────────────────

    /**
     * Called by LoginPanel when a user successfully logs in or registers.
     * Transitions the UI from the login screen to the main shopping shell.
     *
     * Demonstrates: Interface callback (Polymorphism — MainFrame IS-A LoginListener),
     *               object creation and dependency injection.
     */
    @Override
    public void onLoginSuccess(Customer customer) {
        this.currentUser = customer;
        this.cart        = new ShoppingCart(currentUser);

        buildAppShell();

        CardLayout cl = (CardLayout) getContentPane().getLayout();
        cl.show(getContentPane(), CARD_APP);
    }

    // ── Main app shell ────────────────────────────────────────────

    /**
     * Constructs the main application layout:
     *   LEFT  — SidebarPanel (navigation)
     *   RIGHT — CardLayout content area (catalog | cart | orders)
     */
    private void buildAppShell() {
        JPanel appShell = new JPanel(new BorderLayout(0, 0));
        appShell.setBackground(GuiTheme.BG_MAIN);

        // Sidebar
        sidebarPanel = new SidebarPanel(currentUser.getUsername(), section -> {
            switch (section) {
                case "catalog" -> showSection(CARD_CATALOG);
                case "cart"    -> {
                    cartPanel.refresh();
                    showSection(CARD_CART);
                }
                case "orders"  -> {
                    ordersPanel.refresh();
                    showSection(CARD_ORDERS);
                }
                case "logout"  -> handleLogout(appShell);
            }
            sidebarPanel.setActive(section);
        });

        // Content area (CardLayout)
        contentArea  = new JPanel(new CardLayout());
        contentArea.setBackground(GuiTheme.BG_MAIN);

        // Create all section panels (injecting shared services)
        catalogPanel = new CatalogPanel(catalog, cart, this::onCartUpdated);
        cartPanel    = new CartPanel(cart, checkoutService, this);
        ordersPanel  = new OrdersPanel(checkoutService, currentUser);

        contentArea.add(catalogPanel, CARD_CATALOG);
        contentArea.add(cartPanel,    CARD_CART);
        contentArea.add(ordersPanel,  CARD_ORDERS);

        showSection(CARD_CATALOG); // default section

        appShell.add(sidebarPanel, BorderLayout.WEST);
        appShell.add(contentArea,  BorderLayout.CENTER);

        add(appShell, CARD_APP);
        revalidate();
        repaint();
    }

    /** Switches the content area to the named card. */
    private void showSection(String card) {
        ((CardLayout) contentArea.getLayout()).show(contentArea, card);
    }

    // ── CartPanel.CheckoutListener callbacks ──────────────────────

    /**
     * Called when a cart mutation happens (add/remove/update).
     * Keeps the sidebar badge in sync.
     */
    public void onCartChanged() {
        sidebarPanel.updateCartBadge(cart.itemCount());
    }

    /**
     * Called by CartPanel after a successful checkout.
     * Shows the receipt dialog and navigates to Order History.
     */
    @Override
    public void onCheckoutComplete(Order order) {
        sidebarPanel.updateCartBadge(0);
        ordersPanel.refresh();
        // Show receipt in a modal dialog
        OrdersPanel.showReceiptDialog(order, this);
        // Navigate to orders after the dialog closes
        showSection(CARD_ORDERS);
        sidebarPanel.setActive("orders");
    }

    // Called from CatalogPanel when items are added
    private void onCartUpdated() {
        sidebarPanel.updateCartBadge(cart.itemCount());
    }

    // ── Logout ────────────────────────────────────────────────────

    private void handleLogout(JPanel appShell) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to log out?", "Logout",
            JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        // Tear down the app shell and return to login screen
        getContentPane().remove(appShell);
        currentUser  = null;
        cart         = null;
        sidebarPanel = null;
        catalogPanel = null;
        cartPanel    = null;
        ordersPanel  = null;

        showLoginScreen();
        revalidate();
        repaint();
    }
}
