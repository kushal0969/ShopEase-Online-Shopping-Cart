package cart.gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Factory for styled, reusable Swing components.
 *
 * Every widget that appears in more than one panel is created here,
 * so visual consistency is guaranteed across the whole application.
 *
 * OOP: static factory methods — caller never instantiates this class.
 */
public final class GuiComponents {

    private GuiComponents() {}

    // ── Buttons ───────────────────────────────────────────────────

    /**
     * Creates a filled, rounded primary action button.
     *
     * @param text   button label
     * @param bg     background colour
     * @param fg     text colour
     */
    public static JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.darker() : bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(GuiTheme.FONT_BUTTON);
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setPreferredSize(new Dimension(120, GuiTheme.BUTTON_HEIGHT));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(false);
        return btn;
    }

    /** Shortcut: primary blue button. */
    public static JButton primaryButton(String text) {
        return createButton(text, GuiTheme.ACCENT, GuiTheme.TEXT_LIGHT);
    }

    /** Shortcut: green success button. */
    public static JButton successButton(String text) {
        return createButton(text, GuiTheme.SUCCESS, GuiTheme.TEXT_LIGHT);
    }

    /** Shortcut: red danger button. */
    public static JButton dangerButton(String text) {
        return createButton(text, GuiTheme.DANGER, GuiTheme.TEXT_LIGHT);
    }

    /** Shortcut: dark button (sidebar actions). */
    public static JButton darkButton(String text) {
        return createButton(text, GuiTheme.PRIMARY, GuiTheme.TEXT_LIGHT);
    }

    // ── Text Fields ───────────────────────────────────────────────

    /**
     * Creates a rounded, styled text field with placeholder support.
     */
    public static JTextField createTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        field.setFont(GuiTheme.FONT_BODY);
        field.setForeground(GuiTheme.TEXT_PRIMARY);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(GuiTheme.BORDER, 1, true),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        field.setPreferredSize(new Dimension(200, GuiTheme.FIELD_HEIGHT));
        field.setOpaque(false);

        // Placeholder text
        if (placeholder != null && !placeholder.isEmpty()) {
            field.setText(placeholder);
            field.setForeground(GuiTheme.TEXT_SECONDARY);
            field.addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) {
                    if (field.getText().equals(placeholder)) {
                        field.setText("");
                        field.setForeground(GuiTheme.TEXT_PRIMARY);
                    }
                }
                @Override public void focusLost(FocusEvent e) {
                    if (field.getText().isEmpty()) {
                        field.setText(placeholder);
                        field.setForeground(GuiTheme.TEXT_SECONDARY);
                    }
                }
            });
        }
        return field;
    }

    /** Creates a password field with the same styling as createTextField(). */
    public static JPasswordField createPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField();
        field.setFont(GuiTheme.FONT_BODY);
        field.setForeground(GuiTheme.TEXT_PRIMARY);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(GuiTheme.BORDER, 1, true),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        field.setPreferredSize(new Dimension(200, GuiTheme.FIELD_HEIGHT));
        field.setEchoChar('●');
        return field;
    }

    // ── Labels ────────────────────────────────────────────────────

    public static JLabel heading(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(GuiTheme.FONT_HEADING);
        lbl.setForeground(GuiTheme.TEXT_PRIMARY);
        return lbl;
    }

    public static JLabel subheading(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(GuiTheme.FONT_SUBHEADING);
        lbl.setForeground(GuiTheme.TEXT_PRIMARY);
        return lbl;
    }

    public static JLabel bodyLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(GuiTheme.FONT_BODY);
        lbl.setForeground(GuiTheme.TEXT_PRIMARY);
        return lbl;
    }

    public static JLabel secondaryLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(GuiTheme.FONT_SMALL);
        lbl.setForeground(GuiTheme.TEXT_SECONDARY);
        return lbl;
    }

    // ── Card Panel ────────────────────────────────────────────────

    /**
     * Creates a white, rounded-corner card panel.
     * Cards are used throughout the UI to group related content.
     */
    public static JPanel createCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                // Drop shadow effect
                g2.setColor(new Color(0, 0, 0, 18));
                g2.fill(new RoundRectangle2D.Float(3, 3, getWidth() - 3, getHeight() - 3,
                                                   GuiTheme.CARD_ARC, GuiTheme.CARD_ARC));
                // Card background
                g2.setColor(GuiTheme.BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 3, getHeight() - 3,
                                                   GuiTheme.CARD_ARC, GuiTheme.CARD_ARC));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(
            GuiTheme.PAD, GuiTheme.PAD, GuiTheme.PAD, GuiTheme.PAD));
        return card;
    }

    // ── Separator ─────────────────────────────────────────────────

    public static JSeparator separator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(GuiTheme.BORDER);
        return sep;
    }

    // ── Scroll Pane ───────────────────────────────────────────────

    public static JScrollPane scrollPane(Component view) {
        JScrollPane sp = new JScrollPane(view);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(GuiTheme.BG_MAIN);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return sp;
    }

    // ── Badge / Chip ──────────────────────────────────────────────

    /** A small coloured tag label (e.g., for product categories). */
    public static JLabel badge(String text, Color bg) {
        JLabel badge = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(GuiTheme.FONT_SMALL);
        badge.setForeground(Color.WHITE);
        badge.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        badge.setOpaque(false);
        return badge;
    }
}
