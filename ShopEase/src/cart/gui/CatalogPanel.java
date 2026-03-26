package cart.gui;

import cart.cart.ShoppingCart;
import cart.exceptions.InvalidQuantityException;
import cart.exceptions.OutOfStockException;
import cart.products.Product;
import cart.products.ProductCatalog;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Displays the full product catalog with search, category filter, and add-to-cart.
 *
 * Layout:
 *   TOP    — page heading + search bar + category filter buttons
 *   CENTER — scrollable grid of product cards (3 columns)
 *
 * OOP: extends JPanel, references ShoppingCart and ProductCatalog (Composition).
 *      Demonstrates Polymorphism: product cards call p.getCategory() and p.getDetails(),
 *      which dispatch to the correct subclass method at runtime.
 */
public class CatalogPanel extends JPanel {

    public interface CartUpdateListener {
        void onCartUpdated();
    }

    private final ProductCatalog      catalog;
    private final ShoppingCart        cart;
    private final CartUpdateListener  cartListener;

    private JTextField searchField;
    private JPanel     gridPanel;
    private String     activeCategory = "All";

    public CatalogPanel(ProductCatalog catalog, ShoppingCart cart,
                        CartUpdateListener listener) {
        this.catalog      = catalog;
        this.cart         = cart;
        this.cartListener = listener;
        setLayout(new BorderLayout(0, 0));
        setBackground(GuiTheme.BG_MAIN);
        buildUI();
    }

    private void buildUI() {
        add(buildTopBar(),      BorderLayout.NORTH);
        add(buildGrid(),        BorderLayout.CENTER);
    }

    // ── Top bar: heading + search + filter ────────────────────────

    private JPanel buildTopBar() {
        JPanel topBar = new JPanel(new BorderLayout(0, 12));
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(
            GuiTheme.PAD_LG, GuiTheme.PAD_LG, GuiTheme.PAD, GuiTheme.PAD_LG));

        // Row 1: heading
        JLabel heading = GuiComponents.heading("Product Catalog");
        topBar.add(heading, BorderLayout.NORTH);

        // Row 2: search + filter
        JPanel controls = new JPanel(new BorderLayout(12, 0));
        controls.setOpaque(false);

        // Search field
        searchField = GuiComponents.createTextField("🔍  Search products...");
        searchField.setPreferredSize(new Dimension(280, GuiTheme.FIELD_HEIGHT));
        searchField.addActionListener(e -> refreshGrid());
        JButton searchBtn = GuiComponents.primaryButton("Search");
        searchBtn.addActionListener(e -> refreshGrid());
        JPanel searchRow = new JPanel(new BorderLayout(8, 0));
        searchRow.setOpaque(false);
        searchRow.add(searchField, BorderLayout.CENTER);
        searchRow.add(searchBtn, BorderLayout.EAST);
        controls.add(searchRow, BorderLayout.WEST);

        // Category filter buttons
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        filterRow.setOpaque(false);
        for (String cat : new String[]{"All", "Electronics", "Clothing", "Food & Grocery"}) {
            filterRow.add(filterChip(cat));
        }
        controls.add(filterRow, BorderLayout.EAST);

        topBar.add(controls, BorderLayout.SOUTH);
        return topBar;
    }

    private JButton filterChip(String category) {
        JButton chip = new JButton(category) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                boolean active = activeCategory.equals(category);
                g2.setColor(active ? GuiTheme.ACCENT : GuiTheme.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                if (!active) {
                    g2.setColor(GuiTheme.BORDER);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        chip.setFont(GuiTheme.FONT_SMALL);
        chip.setForeground(activeCategory.equals(category) ? Color.WHITE : GuiTheme.TEXT_PRIMARY);
        chip.setContentAreaFilled(false);
        chip.setBorderPainted(false);
        chip.setFocusPainted(false);
        chip.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        chip.setBorder(BorderFactory.createEmptyBorder(5, 14, 5, 14));
        chip.addActionListener(e -> {
            activeCategory = category;
            refreshGrid();
            // Repaint all chips in the parent container
            Container parent = chip.getParent();
            if (parent != null) for (Component c : parent.getComponents()) c.repaint();
        });
        return chip;
    }

    // ── Product grid ──────────────────────────────────────────────

    private JScrollPane buildGrid() {
        gridPanel = new JPanel(new GridLayout(0, 3, 14, 14));
        gridPanel.setOpaque(false);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(
            0, GuiTheme.PAD_LG, GuiTheme.PAD_LG, GuiTheme.PAD_LG));
        refreshGrid();
        return GuiComponents.scrollPane(gridPanel);
    }

    /** Rebuilds the product grid based on current search text and category filter. */
    public void refreshGrid() {
        gridPanel.removeAll();
        String keyword = searchField.getText().trim();
        // Treat placeholder text as empty search
        if (keyword.equals("🔍  Search products...")) keyword = "";

        List<Product> products = keyword.isEmpty()
            ? catalog.getAllProducts()
            : catalog.searchByName(keyword);

        final String kw = keyword; // effectively final for lambda
        if (!activeCategory.equals("All")) {
            String cat = activeCategory;
            products = products.stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(cat))
                .collect(java.util.stream.Collectors.toList());
        }

        if (products.isEmpty()) {
            JLabel empty = GuiComponents.secondaryLabel("No products found.");
            empty.setHorizontalAlignment(SwingConstants.CENTER);
            gridPanel.setLayout(new BorderLayout());
            gridPanel.add(empty, BorderLayout.CENTER);
        } else {
            if (!(gridPanel.getLayout() instanceof GridLayout)) {
                gridPanel.setLayout(new GridLayout(0, 3, 14, 14));
            }
            for (Product p : products) {
                gridPanel.add(buildProductCard(p));
            }
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    // ── Individual product card ────────────────────────────────────

    /**
     * Builds one product card. This is where runtime Polymorphism is visible:
     * p.getCategory() and p.getDetails() dispatch to the actual subclass
     * (Electronics / Clothing / Food) without any instanceof checks.
     */
    private JPanel buildProductCard(Product p) {
        JPanel card = GuiComponents.createCard();
        card.setLayout(new BorderLayout(0, 8));
        card.setPreferredSize(new Dimension(0, 220));

        // Category colour
        Color catColor = categoryColor(p.getCategory());

        // Top: category badge + name
        JPanel top = new JPanel(new BorderLayout(0, 6));
        top.setOpaque(false);
        JLabel categoryBadge = GuiComponents.badge(p.getCategory(), catColor);
        JLabel nameLabel = GuiComponents.subheading(
            "<html><body style='width:160px'>" + p.getName() + "</body></html>");

        top.add(categoryBadge, BorderLayout.NORTH);
        top.add(nameLabel,     BorderLayout.CENTER);

        // Middle: details (Polymorphism — getDetails() returns subclass-specific string)
        JLabel detailLabel = GuiComponents.secondaryLabel(
            "<html><body style='width:160px'>" + p.getDetails() + "</body></html>");

        // Bottom: price + add button
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);

        JLabel priceLabel = new JLabel(String.format("$%.2f", p.getPrice()));
        priceLabel.setFont(GuiTheme.FONT_PRICE);
        priceLabel.setForeground(GuiTheme.ACCENT);

        JLabel stockLabel = GuiComponents.secondaryLabel(
            p.getStockQuantity() > 0
                ? "In stock: " + p.getStockQuantity()
                : "Out of stock");
        stockLabel.setForeground(p.getStockQuantity() > 0 ? GuiTheme.SUCCESS : GuiTheme.DANGER);

        JPanel priceBlock = new JPanel();
        priceBlock.setOpaque(false);
        priceBlock.setLayout(new BoxLayout(priceBlock, BoxLayout.Y_AXIS));
        priceBlock.add(priceLabel);
        priceBlock.add(stockLabel);

        JButton addBtn = GuiComponents.successButton("+ Add");
        addBtn.setPreferredSize(new Dimension(80, 32));
        addBtn.setEnabled(p.getStockQuantity() > 0);
        addBtn.addActionListener(e -> handleAddToCart(p, addBtn));

        bottom.add(priceBlock, BorderLayout.WEST);
        bottom.add(addBtn,     BorderLayout.EAST);

        card.add(top,         BorderLayout.NORTH);
        card.add(detailLabel, BorderLayout.CENTER);
        card.add(bottom,      BorderLayout.SOUTH);

        return card;
    }

    private void handleAddToCart(Product p, JButton addBtn) {
        // Prompt quantity via dialog
        String input = JOptionPane.showInputDialog(this,
            "How many \"" + p.getName() + "\" would you like?",
            "Add to Cart", JOptionPane.QUESTION_MESSAGE);
        if (input == null) return; // user cancelled
        try {
            int qty = Integer.parseInt(input.trim());
            cart.addItem(p, qty);
            cartListener.onCartUpdated();
            JOptionPane.showMessageDialog(this,
                qty + "x \"" + p.getName() + "\" added to cart!",
                "Added", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number.",
                "Invalid Input", JOptionPane.WARNING_MESSAGE);
        } catch (InvalidQuantityException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Invalid Quantity", JOptionPane.WARNING_MESSAGE);
        } catch (OutOfStockException ex) {
            JOptionPane.showMessageDialog(this,
                ex.getMessage() + "\nOnly " + ex.getAvailable() + " unit(s) available.",
                "Out of Stock", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────

    private Color categoryColor(String category) {
        return switch (category) {
            case "Electronics"  -> new Color(0x3498DB);
            case "Clothing"     -> new Color(0x9B59B6);
            case "Food & Grocery" -> new Color(0x27AE60);
            default             -> GuiTheme.TEXT_SECONDARY;
        };
    }
}
