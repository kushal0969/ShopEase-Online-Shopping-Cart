package cart.gui;

import cart.users.Customer;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * The Login / Register screen shown before any user is authenticated.
 *
 * Layout:
 *   LEFT  — branding panel (dark background, logo, tagline)
 *   RIGHT — card with tab switcher: Login | Register
 *
 * OOP: extends JPanel (Inheritance), uses GuiComponents factory (Encapsulation).
 */
public class LoginPanel extends JPanel {

    // ── Callback interface so MainFrame can react to a successful login ──
    public interface LoginListener {
        void onLoginSuccess(Customer customer);
    }

    private LoginListener listener;

    // Pre-seeded demo customers (mirrors Main.java DEMO_USERS)
    private static final Customer[] DEMO_USERS = {
        new Customer(1, "alice", "alice@shopease.com", "pass123", "123 Thamel St, Kathmandu"),
        new Customer(2, "bob",   "bob@shopease.com",   "bob456",  "45 Lakeside Rd, Pokhara")
    };

    // ── Login tab fields ──
    private JTextField     loginUsernameField;
    private JPasswordField loginPasswordField;
    private JLabel         loginErrorLabel;

    // ── Register tab fields ──
    private JTextField     regUsernameField;
    private JTextField     regEmailField;
    private JPasswordField regPasswordField;
    private JTextField     regAddressField;
    private JLabel         regErrorLabel;

    // ── Tab toggle ──
    private JPanel loginForm;
    private JPanel registerForm;
    private JButton tabLogin;
    private JButton tabRegister;

    public LoginPanel(LoginListener listener) {
        this.listener = listener;
        setLayout(new BorderLayout());
        setBackground(GuiTheme.BG_MAIN);
        buildUI();
    }

    private void buildUI() {
        // Left branding panel
        add(buildBrandPanel(), BorderLayout.WEST);
        // Right form panel
        add(buildFormPanel(), BorderLayout.CENTER);
    }

    // ── Left: branding ────────────────────────────────────────────

    private JPanel buildBrandPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                // Gradient from PRIMARY_DARK to PRIMARY
                GradientPaint gp = new GradientPaint(
                    0, 0, GuiTheme.PRIMARY_DARK,
                    0, getHeight(), new Color(0x3D5A73)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        panel.setPreferredSize(new Dimension(380, 0));
        panel.setLayout(new GridBagLayout());

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        // Logo icon (cart emoji placeholder)
        JLabel iconLabel = new JLabel("🛒");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 56));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel logoLabel = new JLabel("ShopEase");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel taglineLabel = new JLabel("<html><div style='text-align:center;'>"
            + "Your smart online shopping<br>destination</div></html>");
        taglineLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        taglineLabel.setForeground(new Color(0xBDC3C7));
        taglineLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        taglineLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Feature bullet points
        String[] features = {"✓  Browse 15+ curated products",
                             "✓  Smart coupon discounts",
                             "✓  Instant order receipts",
                             "✓  Full order history"};
        JPanel featuresPanel = new JPanel();
        featuresPanel.setOpaque(false);
        featuresPanel.setLayout(new BoxLayout(featuresPanel, BoxLayout.Y_AXIS));
        featuresPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        featuresPanel.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        for (String f : features) {
            JLabel fl = new JLabel(f);
            fl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            fl.setForeground(new Color(0xABB2BA));
            fl.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
            featuresPanel.add(fl);
        }

        // Demo hint box
        JPanel hintBox = new JPanel();
        hintBox.setOpaque(false);
        hintBox.setLayout(new BoxLayout(hintBox, BoxLayout.Y_AXIS));
        hintBox.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(0x5D7D99), 1, true),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        hintBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel hintTitle = new JLabel("Demo Credentials");
        hintTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        hintTitle.setForeground(new Color(0xABB2BA));
        JLabel hintUser1 = new JLabel("alice / pass123");
        hintUser1.setFont(new Font("Consolas", Font.PLAIN, 12));
        hintUser1.setForeground(new Color(0xECF0F1));
        JLabel hintUser2 = new JLabel("bob   / bob456");
        hintUser2.setFont(new Font("Consolas", Font.PLAIN, 12));
        hintUser2.setForeground(new Color(0xECF0F1));
        hintBox.add(hintTitle);
        hintBox.add(Box.createVerticalStrut(6));
        hintBox.add(hintUser1);
        hintBox.add(hintUser2);

        inner.add(iconLabel);
        inner.add(Box.createVerticalStrut(8));
        inner.add(logoLabel);
        inner.add(Box.createVerticalStrut(12));
        inner.add(taglineLabel);
        inner.add(featuresPanel);
        inner.add(Box.createVerticalStrut(32));
        inner.add(hintBox);

        panel.add(inner);
        return panel;
    }

    // ── Right: form panel ─────────────────────────────────────────

    private JPanel buildFormPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(GuiTheme.BG_MAIN);

        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(0, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(GuiTheme.BORDER, 1, true),
            BorderFactory.createEmptyBorder(32, 40, 32, 40)
        ));
        card.setPreferredSize(new Dimension(400, 480));

        // Tab buttons
        JPanel tabPanel = buildTabSwitcher();
        card.add(tabPanel, BorderLayout.NORTH);

        // Forms in a CardLayout
        JPanel formsContainer = new JPanel(new CardLayout());
        formsContainer.setOpaque(false);
        loginForm    = buildLoginForm();
        registerForm = buildRegisterForm();
        formsContainer.add(loginForm, "login");
        formsContainer.add(registerForm, "register");
        card.add(formsContainer, BorderLayout.CENTER);

        // Wire tab buttons to show correct card
        tabLogin.addActionListener(e -> {
            ((CardLayout) formsContainer.getLayout()).show(formsContainer, "login");
            setActiveTab(tabLogin, tabRegister);
        });
        tabRegister.addActionListener(e -> {
            ((CardLayout) formsContainer.getLayout()).show(formsContainer, "register");
            setActiveTab(tabRegister, tabLogin);
        });

        outer.add(card);
        return outer;
    }

    private JPanel buildTabSwitcher() {
        JPanel tabs = new JPanel(new GridLayout(1, 2));
        tabs.setOpaque(false);
        tabs.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));

        tabLogin    = makeTabButton("Login",    true);
        tabRegister = makeTabButton("Register", false);
        tabs.add(tabLogin);
        tabs.add(tabRegister);
        return tabs;
    }

    private JButton makeTabButton(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setFont(GuiTheme.FONT_BODY_BOLD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        applyTabStyle(btn, active);
        return btn;
    }

    private void applyTabStyle(JButton btn, boolean active) {
        if (active) {
            btn.setBackground(GuiTheme.ACCENT);
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(GuiTheme.TABLE_HEADER);
            btn.setForeground(GuiTheme.TEXT_SECONDARY);
        }
    }

    private void setActiveTab(JButton active, JButton inactive) {
        applyTabStyle(active, true);
        applyTabStyle(inactive, false);
    }

    // ── Login form ────────────────────────────────────────────────

    private JPanel buildLoginForm() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        loginUsernameField = GuiComponents.createTextField("Username");
        loginPasswordField = GuiComponents.createPasswordField("Password");
        loginErrorLabel    = new JLabel(" ");
        loginErrorLabel.setFont(GuiTheme.FONT_SMALL);
        loginErrorLabel.setForeground(GuiTheme.DANGER);

        JButton loginBtn = GuiComponents.primaryButton("Login");
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, GuiTheme.BUTTON_HEIGHT));
        loginBtn.addActionListener(e -> handleLogin());

        // Allow pressing Enter to login
        loginPasswordField.addActionListener(e -> handleLogin());

        panel.add(fieldBlock("Username", loginUsernameField));
        panel.add(Box.createVerticalStrut(12));
        panel.add(fieldBlock("Password", loginPasswordField));
        panel.add(Box.createVerticalStrut(6));
        panel.add(loginErrorLabel);
        panel.add(Box.createVerticalStrut(16));
        panel.add(loginBtn);
        return panel;
    }

    private void handleLogin() {
        String username = loginUsernameField.getText().trim();
        String password = new String(loginPasswordField.getPassword());

        for (Customer user : DEMO_USERS) {
            if (user.getUsername().equalsIgnoreCase(username)
                    && user.checkPassword(password)) {
                listener.onLoginSuccess(user);
                return;
            }
        }
        loginErrorLabel.setText("Invalid username or password.");
    }

    // ── Register form ─────────────────────────────────────────────

    private JPanel buildRegisterForm() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        regUsernameField = GuiComponents.createTextField("Choose a username");
        regEmailField    = GuiComponents.createTextField("your@email.com");
        regPasswordField = GuiComponents.createPasswordField("Password");
        regAddressField  = GuiComponents.createTextField("Delivery address");
        regErrorLabel    = new JLabel(" ");
        regErrorLabel.setFont(GuiTheme.FONT_SMALL);
        regErrorLabel.setForeground(GuiTheme.DANGER);

        JButton regBtn = GuiComponents.successButton("Create Account");
        regBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, GuiTheme.BUTTON_HEIGHT));
        regBtn.addActionListener(e -> handleRegister());

        panel.add(fieldBlock("Username", regUsernameField));
        panel.add(Box.createVerticalStrut(8));
        panel.add(fieldBlock("Email", regEmailField));
        panel.add(Box.createVerticalStrut(8));
        panel.add(fieldBlock("Password", regPasswordField));
        panel.add(Box.createVerticalStrut(8));
        panel.add(fieldBlock("Address", regAddressField));
        panel.add(Box.createVerticalStrut(4));
        panel.add(regErrorLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(regBtn);
        return panel;
    }

    private void handleRegister() {
        String username = regUsernameField.getText().trim();
        String email    = regEmailField.getText().trim();
        String password = new String(regPasswordField.getPassword());
        String address  = regAddressField.getText().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || address.isEmpty()) {
            regErrorLabel.setText("All fields are required.");
            return;
        }
        if (!email.contains("@")) {
            regErrorLabel.setText("Please enter a valid email.");
            return;
        }
        try {
            Customer newUser = new Customer(username, email, password, address);
            listener.onLoginSuccess(newUser);
        } catch (IllegalArgumentException ex) {
            regErrorLabel.setText(ex.getMessage());
        }
    }

    // ── Helper ────────────────────────────────────────────────────

    /** Wraps a label + field in a vertical block. */
    private JPanel fieldBlock(String labelText, JComponent field) {
        JPanel block = new JPanel();
        block.setOpaque(false);
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        JLabel label = GuiComponents.secondaryLabel(labelText.toUpperCase());
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, GuiTheme.FIELD_HEIGHT));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        block.add(label);
        block.add(field);
        return block;
    }
}
