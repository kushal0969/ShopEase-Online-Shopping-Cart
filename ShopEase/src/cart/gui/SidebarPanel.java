package cart.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Left-side navigation sidebar shown after login.
 *
 * Displays the logo, the logged-in user's name, and navigation buttons
 * for every section of the app. Fires a callback when the user clicks a nav item.
 *
 * OOP: extends JPanel (Inheritance). Uses inner interface for event callbacks.
 */
public class SidebarPanel extends JPanel {

    public interface NavListener {
        void onNavigate(String section);   // section: "catalog", "cart", "orders", "logout"
    }

    private NavListener navListener;
    private String      activeSection = "catalog";
    private String      username;

    // Keep button references so we can highlight the active one
    private JButton btnCatalog;
    private JButton btnCart;
    private JButton btnOrders;
    private JLabel  cartBadge;  // shows item count

    public SidebarPanel(String username, NavListener listener) {
        this.username    = username;
        this.navListener = listener;
        setPreferredSize(new Dimension(GuiTheme.SIDEBAR_WIDTH, 0));
        setBackground(GuiTheme.BG_SIDEBAR);
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        // Top: logo block
        add(buildLogoBlock(), BorderLayout.NORTH);
        // Middle: nav buttons
        add(buildNavBlock(), BorderLayout.CENTER);
        // Bottom: user info + logout
        add(buildBottomBlock(), BorderLayout.SOUTH);
    }

    // ── Logo block ────────────────────────────────────────────────

    private JPanel buildLogoBlock() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(24, 20, 20, 20));

        JLabel icon = new JLabel("🛒");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel logo = new JLabel("ShopEase");
        logo.setFont(GuiTheme.FONT_LOGO);
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(0x3D5A73));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        panel.add(icon);
        panel.add(Box.createVerticalStrut(4));
        panel.add(logo);
        panel.add(Box.createVerticalStrut(20));
        panel.add(sep);
        return panel;
    }

    // ── Nav buttons ───────────────────────────────────────────────

    private JPanel buildNavBlock() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        btnCatalog = navButton("🏪  Catalog",  "catalog");
        btnCart    = navButton("🛒  My Cart",  "cart");
        btnOrders  = navButton("📦  Orders",   "orders");

        // Cart badge overlay
        JPanel cartRow = new JPanel(new BorderLayout());
        cartRow.setOpaque(false);
        cartRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        cartBadge = new JLabel("0");
        cartBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        cartBadge.setForeground(Color.WHITE);
        cartBadge.setBackground(GuiTheme.DANGER);
        cartBadge.setOpaque(true);
        cartBadge.setBorder(BorderFactory.createEmptyBorder(1, 6, 1, 6));
        cartBadge.setVisible(false);
        cartRow.add(btnCart, BorderLayout.CENTER);
        cartRow.add(cartBadge, BorderLayout.EAST);

        panel.add(btnCatalog);
        panel.add(cartRow);
        panel.add(btnOrders);

        setActive("catalog");
        return panel;
    }

    private JButton navButton(String text, String section) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                if (getModel().isRollover() || section.equals(activeSection)) {
                    g2.setColor(section.equals(activeSection)
                        ? GuiTheme.SIDEBAR_ACTIVE : GuiTheme.SIDEBAR_HOVER);
                    g2.fillRoundRect(8, 2, getWidth() - 16, getHeight() - 4, 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(GuiTheme.FONT_NAV);
        btn.setForeground(Color.WHITE);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 16));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btn.addActionListener(e -> {
            activeSection = section;
            repaintAll();
            navListener.onNavigate(section);
        });
        return btn;
    }

    // ── Bottom: user + logout ─────────────────────────────────────

    private JPanel buildBottomBlock() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 16, 20, 16));

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(0x3D5A73));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        JLabel userLabel = new JLabel("👤  " + username);
        userLabel.setFont(GuiTheme.FONT_BODY_BOLD);
        userLabel.setForeground(new Color(0xABB2BA));
        userLabel.setBorder(BorderFactory.createEmptyBorder(12, 4, 8, 4));

        JButton logoutBtn = navButton("🚪  Logout", "logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logoutBtn.setForeground(new Color(0xE74C3C));

        panel.add(sep);
        panel.add(userLabel);
        panel.add(logoutBtn);
        return panel;
    }

    // ── Public API ────────────────────────────────────────────────

    /** Highlights the nav button for the given section. */
    public void setActive(String section) {
        activeSection = section;
        repaintAll();
    }

    /** Updates the cart item-count badge. */
    public void updateCartBadge(int count) {
        cartBadge.setText(String.valueOf(count));
        cartBadge.setVisible(count > 0);
        btnCart.repaint();
    }

    private void repaintAll() {
        if (btnCatalog != null) btnCatalog.repaint();
        if (btnCart    != null) btnCart.repaint();
        if (btnOrders  != null) btnOrders.repaint();
    }
}
