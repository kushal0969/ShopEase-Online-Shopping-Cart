package cart.gui;

import cart.cart.CartItem;
import cart.cart.ShoppingCart;
import cart.checkout.CheckoutService;
import cart.checkout.CouponCode;
import cart.checkout.Order;
import cart.exceptions.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Displays the shopping cart contents in a styled table and handles checkout.
 *
 * Layout:
 *   TOP    — heading + summary strip
 *   CENTER — JTable listing all cart items
 *   BOTTOM — coupon field, totals panel, checkout button
 *
 * OOP: extends JPanel. Coordinates ShoppingCart and CheckoutService (Composition).
 *      All custom exceptions from the project are caught and surfaced via dialogs.
 */
public class CartPanel extends JPanel {

    public interface CheckoutListener {
        void onCheckoutComplete(Order order);
        void onCartChanged();
    }

    private final ShoppingCart      cart;
    private final CheckoutService   checkoutService;
    private final CheckoutListener  listener;

    // Table
    private JTable          cartTable;
    private DefaultTableModel tableModel;

    // Summary labels
    private JLabel subtotalLabel;
    private JLabel discountLabel;
    private JLabel totalLabel;

    // Coupon
    private JTextField couponField;
    private CouponCode appliedCoupon = null;

    public CartPanel(ShoppingCart cart, CheckoutService checkoutService,
                     CheckoutListener listener) {
        this.cart            = cart;
        this.checkoutService = checkoutService;
        this.listener        = listener;
        setLayout(new BorderLayout(0, 0));
        setBackground(GuiTheme.BG_MAIN);
        buildUI();
    }

    private void buildUI() {
        add(buildHeader(),   BorderLayout.NORTH);
        add(buildTable(),    BorderLayout.CENTER);
        add(buildFooter(),   BorderLayout.SOUTH);
    }

    // ── Header ────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(
            GuiTheme.PAD_LG, GuiTheme.PAD_LG, GuiTheme.PAD, GuiTheme.PAD_LG));

        JLabel heading = GuiComponents.heading("My Shopping Cart");
        panel.add(heading, BorderLayout.WEST);

        JButton clearBtn = GuiComponents.dangerButton("Clear Cart");
        clearBtn.addActionListener(e -> {
            if (cart.isEmpty()) return;
            int confirm = JOptionPane.showConfirmDialog(this,
                "Remove all items from your cart?", "Clear Cart",
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                cart.clear();
                appliedCoupon = null;
                refresh();
                listener.onCartChanged();
            }
        });
        panel.add(clearBtn, BorderLayout.EAST);
        return panel;
    }

    // ── Cart table ────────────────────────────────────────────────

    private JScrollPane buildTable() {
        String[] cols = {"Product", "Category", "Unit Price", "Qty", "Subtotal", "Action"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 3; } // qty editable
            @Override public Class<?> getColumnClass(int c) {
                return c == 3 ? Integer.class : String.class;
            }
        };

        cartTable = new JTable(tableModel);
        styleTable();

        // Listen for inline quantity edits
        tableModel.addTableModelListener(e -> {
            if (e.getColumn() == 3 && e.getFirstRow() >= 0) {
                handleQuantityEdit(e.getFirstRow());
            }
        });

        JScrollPane sp = GuiComponents.scrollPane(cartTable);
        sp.setBorder(BorderFactory.createEmptyBorder(
            0, GuiTheme.PAD_LG, 0, GuiTheme.PAD_LG));
        return sp;
    }

    private void styleTable() {
        cartTable.setFont(GuiTheme.FONT_BODY);
        cartTable.setRowHeight(44);
        cartTable.setShowGrid(false);
        cartTable.setIntercellSpacing(new Dimension(0, 0));
        cartTable.setBackground(Color.WHITE);
        cartTable.setSelectionBackground(new Color(0xEBF5FB));
        cartTable.setSelectionForeground(GuiTheme.TEXT_PRIMARY);
        cartTable.setFillsViewportHeight(true);
        cartTable.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Header style
        JTableHeader header = cartTable.getTableHeader();
        header.setFont(GuiTheme.FONT_BODY_BOLD);
        header.setBackground(GuiTheme.TABLE_HEADER);
        header.setForeground(GuiTheme.TEXT_SECONDARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, GuiTheme.BORDER));
        header.setReorderingAllowed(false);

        // Column widths
        int[] widths = {240, 120, 100, 60, 100, 90};
        for (int i = 0; i < widths.length; i++) {
            cartTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // "Remove" button column renderer + editor
        cartTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        cartTable.getColumnModel().getColumn(5).setCellEditor(
            new ButtonEditor(new JCheckBox(), this::removeSelected));

        // Alternating row colours via custom renderer
        cartTable.setDefaultRenderer(Object.class, new AlternatingRowRenderer());
        cartTable.setDefaultRenderer(Integer.class, new AlternatingRowRenderer());
    }

    // ── Footer: coupon + totals + checkout ────────────────────────

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout(GuiTheme.PAD_LG, 0));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(
            GuiTheme.PAD, GuiTheme.PAD_LG, GuiTheme.PAD_LG, GuiTheme.PAD_LG));

        footer.add(buildCouponPanel(), BorderLayout.WEST);
        footer.add(buildTotalsPanel(), BorderLayout.EAST);
        return footer;
    }

    private JPanel buildCouponPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel hint = GuiComponents.secondaryLabel("Available: SAVE10 · SAVE20 · FLAT30 · WELCOME");
        hint.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        couponField = GuiComponents.createTextField("Enter coupon code");
        couponField.setPreferredSize(new Dimension(200, GuiTheme.FIELD_HEIGHT));
        JButton applyBtn = GuiComponents.primaryButton("Apply");
        applyBtn.addActionListener(e -> handleApplyCoupon());

        row.add(couponField, BorderLayout.CENTER);
        row.add(applyBtn,    BorderLayout.EAST);

        panel.add(hint);
        panel.add(row);
        return panel;
    }

    private JPanel buildTotalsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(GuiTheme.BORDER, 1, true),
            BorderFactory.createEmptyBorder(16, 24, 16, 24)
        ));
        panel.setPreferredSize(new Dimension(280, 140));

        GridBagConstraints l = new GridBagConstraints();
        l.anchor = GridBagConstraints.WEST; l.gridx = 0; l.insets = new Insets(2, 0, 2, 24);
        GridBagConstraints r = new GridBagConstraints();
        r.anchor = GridBagConstraints.EAST; r.gridx = 1; r.insets = new Insets(2, 0, 2, 0);

        subtotalLabel = new JLabel("$0.00");
        subtotalLabel.setFont(GuiTheme.FONT_BODY);
        discountLabel = new JLabel("-$0.00");
        discountLabel.setFont(GuiTheme.FONT_BODY);
        discountLabel.setForeground(GuiTheme.SUCCESS);
        totalLabel    = new JLabel("$0.00");
        totalLabel.setFont(GuiTheme.FONT_PRICE);
        totalLabel.setForeground(GuiTheme.ACCENT);

        l.gridy = 0; panel.add(GuiComponents.bodyLabel("Subtotal"), l);
        r.gridy = 0; panel.add(subtotalLabel, r);
        l.gridy = 1; panel.add(GuiComponents.bodyLabel("Discount"), l);
        r.gridy = 1; panel.add(discountLabel, r);

        // Separator
        GridBagConstraints sep = new GridBagConstraints();
        sep.gridx = 0; sep.gridy = 2; sep.gridwidth = 2;
        sep.fill = GridBagConstraints.HORIZONTAL; sep.insets = new Insets(6, 0, 6, 0);
        panel.add(GuiComponents.separator(), sep);

        l.gridy = 3; JLabel totalLbl = GuiComponents.subheading("Total");
        panel.add(totalLbl, l);
        r.gridy = 3; panel.add(totalLabel, r);

        // Checkout button
        GridBagConstraints btnC = new GridBagConstraints();
        btnC.gridx = 0; btnC.gridy = 4; btnC.gridwidth = 2;
        btnC.fill = GridBagConstraints.HORIZONTAL; btnC.insets = new Insets(12, 0, 0, 0);
        JButton checkoutBtn = GuiComponents.successButton("Proceed to Checkout");
        checkoutBtn.setFont(GuiTheme.FONT_BODY_BOLD);
        checkoutBtn.addActionListener(e -> handleCheckout());
        panel.add(checkoutBtn, btnC);

        return panel;
    }

    // ── Public refresh ────────────────────────────────────────────

    /** Reloads table rows and recalculates totals. Call after any cart mutation. */
    public void refresh() {
        // Block model listener while reloading
        tableModel.setRowCount(0);

        for (CartItem item : cart.getItems()) {
            tableModel.addRow(new Object[]{
                item.getProduct().getName(),
                item.getProduct().getCategory(),
                String.format("$%.2f", item.getProduct().getPrice()),
                item.getQuantity(),
                String.format("$%.2f", item.getSubtotal()),
                "Remove"
            });
        }

        updateTotals();
    }

    private void updateTotals() {
        double subtotal = cart.getTotal();
        double discount = (appliedCoupon != null)
            ? appliedCoupon.calculateDiscount(subtotal) : 0.0;
        double total = subtotal - discount;

        subtotalLabel.setText(String.format("$%.2f", subtotal));
        discountLabel.setText(String.format("-$%.2f", discount));
        totalLabel.setText(String.format("$%.2f", total));
    }

    // ── Handlers ──────────────────────────────────────────────────

    private void handleQuantityEdit(int row) {
        if (row >= cart.getItems().size()) return;
        Object val = tableModel.getValueAt(row, 3);
        if (val == null) return;
        try {
            int newQty = Integer.parseInt(val.toString());
            int productId = cart.getItems().get(row).getProduct().getProductId();
            cart.updateQuantity(productId, newQty);
            refresh();
            listener.onCartChanged();
        } catch (NumberFormatException ignored) {
            refresh(); // revert
        } catch (InvalidQuantityException | OutOfStockException | ProductNotFoundException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Update Failed", JOptionPane.WARNING_MESSAGE);
            refresh();
        }
    }

    private void removeSelected(int row) {
        if (row < 0 || row >= cart.getItems().size()) return;
        int productId = cart.getItems().get(row).getProduct().getProductId();
        try {
            cart.removeItem(productId);
            appliedCoupon = null;
            couponField.setText("Enter coupon code");
            couponField.setForeground(GuiTheme.TEXT_SECONDARY);
            refresh();
            listener.onCartChanged();
        } catch (ProductNotFoundException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleApplyCoupon() {
        String code = couponField.getText().trim();
        if (code.isEmpty() || code.equals("Enter coupon code")) {
            JOptionPane.showMessageDialog(this, "Please enter a coupon code.",
                "No Code", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            appliedCoupon = checkoutService.validateCoupon(code, cart.getTotal());
            double saved = appliedCoupon.calculateDiscount(cart.getTotal());
            updateTotals();
            JOptionPane.showMessageDialog(this,
                "Coupon applied! You save $" + String.format("%.2f", saved),
                "Coupon Applied", JOptionPane.INFORMATION_MESSAGE);
        } catch (InvalidCouponException ex) {
            appliedCoupon = null;
            updateTotals();
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Invalid Coupon", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleCheckout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Your cart is empty. Add items before checking out.",
                "Empty Cart", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] methods = {"Credit/Debit Card", "eSewa", "Cash on Delivery"};
        String payMethod = (String) JOptionPane.showInputDialog(
            this, "Select payment method:", "Checkout",
            JOptionPane.QUESTION_MESSAGE, null, methods, methods[0]);
        if (payMethod == null) return; // cancelled

        // Confirm
        double subtotal  = cart.getTotal();
        double discount  = (appliedCoupon != null)
            ? appliedCoupon.calculateDiscount(subtotal) : 0.0;
        double grandTotal = subtotal - discount;

        String msg = String.format(
            "<html><b>Order Summary</b><br><br>" +
            "Subtotal:  $%.2f<br>" +
            "Discount: -$%.2f<br>" +
            "<b>Total:     $%.2f</b><br><br>" +
            "Payment: %s<br><br>Confirm order?</html>",
            subtotal, discount, grandTotal, payMethod
        );
        int confirm = JOptionPane.showConfirmDialog(this, msg,
            "Confirm Order", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            Order order = checkoutService.processCheckout(cart, payMethod, appliedCoupon);
            appliedCoupon = null;
            refresh();
            listener.onCheckoutComplete(order);
        } catch (EmptyCartException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Inner classes: table renderers/editors ────────────────────

    /** Renders a button in a table cell. */
    static class ButtonRenderer extends JButton implements TableCellRenderer {
        ButtonRenderer() {
            setOpaque(true);
            setFont(GuiTheme.FONT_SMALL);
            setForeground(Color.WHITE);
            setBackground(GuiTheme.DANGER);
            setBorderPainted(false);
            setFocusPainted(false);
        }
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean s, boolean f, int r, int c) {
            setText("Remove");
            return this;
        }
    }

    /** Makes a table cell act as a clickable button. */
    static class ButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private final java.util.function.IntConsumer action;
        private int currentRow;

        ButtonEditor(JCheckBox cb, java.util.function.IntConsumer action) {
            super(cb);
            this.action = action;
            button = new JButton("Remove");
            button.setFont(GuiTheme.FONT_SMALL);
            button.setForeground(Color.WHITE);
            button.setBackground(GuiTheme.DANGER);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.addActionListener(e -> {
                fireEditingStopped();
                action.accept(currentRow);
            });
        }
        @Override
        public Component getTableCellEditorComponent(JTable t, Object v,
                boolean s, int r, int c) {
            currentRow = r;
            return button;
        }
        @Override public Object getCellEditorValue() { return "Remove"; }
    }

    /** Alternating row background renderer. */
    static class AlternatingRowRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            super.getTableCellRendererComponent(t, v, sel, foc, row, col);
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            if (sel) {
                setBackground(new Color(0xEBF5FB));
            } else {
                setBackground(row % 2 == 0 ? Color.WHITE : GuiTheme.TABLE_STRIPE);
            }
            setForeground(GuiTheme.TEXT_PRIMARY);
            return this;
        }
    }
}
