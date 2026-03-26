package cart.gui;

import cart.checkout.CheckoutService;
import cart.checkout.Order;
import cart.users.Customer;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Shows the logged-in customer's order history in a table,
 * and allows viewing a detailed receipt for any past order.
 *
 * OOP: extends JPanel, holds a reference to CheckoutService (Composition).
 */
public class OrdersPanel extends JPanel {

    private final CheckoutService checkoutService;
    private final Customer        customer;

    private JTable           ordersTable;
    private DefaultTableModel tableModel;
    private List<Order>       orders;

    public OrdersPanel(CheckoutService checkoutService, Customer customer) {
        this.checkoutService = checkoutService;
        this.customer        = customer;
        setLayout(new BorderLayout(0, 0));
        setBackground(GuiTheme.BG_MAIN);
        buildUI();
    }

    private void buildUI() {
        add(buildHeader(), BorderLayout.NORTH);
        add(buildTable(),  BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(
            GuiTheme.PAD_LG, GuiTheme.PAD_LG, GuiTheme.PAD, GuiTheme.PAD_LG));
        panel.add(GuiComponents.heading("Order History"), BorderLayout.WEST);

        JButton refreshBtn = GuiComponents.primaryButton("Refresh");
        refreshBtn.addActionListener(e -> refresh());
        panel.add(refreshBtn, BorderLayout.EAST);
        return panel;
    }

    private JScrollPane buildTable() {
        String[] cols = {"Order ID", "Date Placed", "Items", "Total", "Payment", "Status", "Receipt"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 6; }
        };

        ordersTable = new JTable(tableModel);
        styleTable();
        ordersTable.getColumnModel().getColumn(6).setCellRenderer(new CartPanel.ButtonRenderer() {
            { setText("View"); setBackground(GuiTheme.ACCENT); }
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean s, boolean f, int r, int c) {
                setText("View"); return this;
            }
        });
        ordersTable.getColumnModel().getColumn(6).setCellEditor(
            new CartPanel.ButtonEditor(new JCheckBox(), row -> showReceipt(row)));

        refresh();

        JScrollPane sp = GuiComponents.scrollPane(ordersTable);
        sp.setBorder(BorderFactory.createEmptyBorder(
            0, GuiTheme.PAD_LG, GuiTheme.PAD_LG, GuiTheme.PAD_LG));
        return sp;
    }

    private void styleTable() {
        ordersTable.setFont(GuiTheme.FONT_BODY);
        ordersTable.setRowHeight(44);
        ordersTable.setShowGrid(false);
        ordersTable.setIntercellSpacing(new Dimension(0, 0));
        ordersTable.setBackground(Color.WHITE);
        ordersTable.setSelectionBackground(new Color(0xEBF5FB));
        ordersTable.setFillsViewportHeight(true);

        JTableHeader header = ordersTable.getTableHeader();
        header.setFont(GuiTheme.FONT_BODY_BOLD);
        header.setBackground(GuiTheme.TABLE_HEADER);
        header.setForeground(GuiTheme.TEXT_SECONDARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, GuiTheme.BORDER));
        header.setReorderingAllowed(false);

        int[] widths = {80, 160, 60, 100, 140, 100, 80};
        for (int i = 0; i < widths.length; i++)
            ordersTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        ordersTable.setDefaultRenderer(Object.class, new CartPanel.AlternatingRowRenderer());
    }

    /** Reloads data from CheckoutService. */
    public void refresh() {
        orders = checkoutService.getOrderHistory(customer);
        tableModel.setRowCount(0);

        if (orders.isEmpty()) {
            tableModel.addRow(new Object[]{
                "—", "No orders yet", "—", "—", "—", "—", "—"
            });
            return;
        }

        for (Order o : orders) {
            tableModel.addRow(new Object[]{
                "#" + o.getOrderId(),
                o.getPlacedAt(),
                o.getItems().size() + " item(s)",
                String.format("$%.2f", o.getGrandTotal()),
                o.getPaymentMethod(),
                o.getStatus().toString(),
                "View"
            });
        }
    }

    private void showReceipt(int row) {
        if (orders == null || row < 0 || row >= orders.size()) return;
        Order order = orders.get(row);
        showReceiptDialog(order, this);
    }

    /** Shows a modal receipt dialog for the given order. */
    public static void showReceiptDialog(Order order, Component parent) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent),
            "Receipt — Order #" + order.getOrderId(), Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(520, 540);
        dialog.setLocationRelativeTo(parent);
        dialog.setBackground(GuiTheme.BG_MAIN);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        // Title
        JLabel title = new JLabel("ORDER RECEIPT");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(GuiTheme.PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel orderIdLbl = GuiComponents.secondaryLabel("Order #" + order.getOrderId());
        orderIdLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(title);
        content.add(Box.createVerticalStrut(4));
        content.add(orderIdLbl);
        content.add(Box.createVerticalStrut(16));
        content.add(GuiComponents.separator());
        content.add(Box.createVerticalStrut(12));

        // Info rows
        content.add(infoRow("Customer",   order.getCustomer().getUsername()));
        content.add(infoRow("Email",      order.getCustomer().getEmail()));
        content.add(infoRow("Address",    order.getCustomer().getDeliveryAddress()));
        content.add(infoRow("Date",       order.getPlacedAt()));
        content.add(infoRow("Payment",    order.getPaymentMethod()));
        content.add(infoRow("Status",     order.getStatus().toString()));
        content.add(Box.createVerticalStrut(12));
        content.add(GuiComponents.separator());
        content.add(Box.createVerticalStrut(12));

        // Items table
        String[] cols = {"Product", "Qty", "Unit", "Subtotal"};
        Object[][] data = new Object[order.getItems().size()][4];
        for (int i = 0; i < order.getItems().size(); i++) {
            var item = order.getItems().get(i);
            data[i][0] = item.getProduct().getName();
            data[i][1] = item.getQuantity();
            data[i][2] = String.format("$%.2f", item.getProduct().getPrice());
            data[i][3] = String.format("$%.2f", item.getSubtotal());
        }
        JTable itemsTable = new JTable(data, cols);
        itemsTable.setFont(GuiTheme.FONT_SMALL);
        itemsTable.setRowHeight(28);
        itemsTable.setEnabled(false);
        itemsTable.getTableHeader().setFont(GuiTheme.FONT_SMALL);
        itemsTable.getTableHeader().setBackground(GuiTheme.TABLE_HEADER);
        JScrollPane sp = new JScrollPane(itemsTable);
        sp.setPreferredSize(new Dimension(440, 120));
        sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        sp.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(sp);
        content.add(Box.createVerticalStrut(12));
        content.add(GuiComponents.separator());
        content.add(Box.createVerticalStrut(8));

        // Totals
        if (order.getDiscountAmount() > 0) {
            JPanel discRow = infoRow("Discount",
                "-$" + String.format("%.2f", order.getDiscountAmount()));
            discRow.getComponent(1); // label
            content.add(discRow);
        }

        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        JLabel tLabel = GuiComponents.subheading("TOTAL");
        JLabel tValue = GuiComponents.subheading(
            String.format("$%.2f", order.getGrandTotal()));
        tValue.setForeground(GuiTheme.ACCENT);
        totalRow.add(tLabel, BorderLayout.WEST);
        totalRow.add(tValue, BorderLayout.EAST);
        content.add(totalRow);
        content.add(Box.createVerticalStrut(20));

        JButton closeBtn = GuiComponents.primaryButton("Close");
        closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeBtn.addActionListener(e -> dialog.dispose());
        content.add(closeBtn);

        dialog.add(GuiComponents.scrollPane(content));
        dialog.setVisible(true);
    }

    private static JPanel infoRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        row.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
        row.add(GuiComponents.secondaryLabel(label + ":"), BorderLayout.WEST);
        JLabel val = GuiComponents.bodyLabel(value);
        val.setHorizontalAlignment(SwingConstants.RIGHT);
        row.add(val, BorderLayout.EAST);
        return row;
    }
}
