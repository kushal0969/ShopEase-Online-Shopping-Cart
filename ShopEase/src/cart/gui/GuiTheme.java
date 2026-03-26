package cart.gui;

import java.awt.*;

/**
 * Centralised design-system constants for the ShopEase GUI.
 *
 * All colours, fonts, sizes, and spacing values are defined here so that
 * changing the visual theme only requires edits in one place.
 *
 * OOP: utility class (all static constants, no instantiation).
 */
public final class GuiTheme {

    // ── Prevent instantiation ─────────────────────────────────────
    private GuiTheme() {}

    // ── Colour Palette ────────────────────────────────────────────
    public static final Color PRIMARY        = new Color(0x2C3E50);   // dark navy
    public static final Color PRIMARY_DARK   = new Color(0x1A252F);   // darker navy
    public static final Color ACCENT         = new Color(0x3498DB);   // blue
    public static final Color ACCENT_HOVER   = new Color(0x2980B9);   // darker blue
    public static final Color SUCCESS        = new Color(0x27AE60);   // green
    public static final Color DANGER         = new Color(0xE74C3C);   // red
    public static final Color WARNING        = new Color(0xF39C12);   // orange
    public static final Color BG_MAIN        = new Color(0xF5F6FA);   // light grey page bg
    public static final Color BG_CARD        = Color.WHITE;
    public static final Color BG_SIDEBAR     = new Color(0x2C3E50);
    public static final Color TEXT_PRIMARY   = new Color(0x2C3E50);
    public static final Color TEXT_SECONDARY = new Color(0x7F8C8D);
    public static final Color TEXT_LIGHT     = Color.WHITE;
    public static final Color BORDER         = new Color(0xDCDCE4);
    public static final Color TABLE_HEADER   = new Color(0xECF0F1);
    public static final Color TABLE_STRIPE   = new Color(0xFAFAFC);
    public static final Color SIDEBAR_ACTIVE = new Color(0x3498DB);
    public static final Color SIDEBAR_HOVER  = new Color(0x34495E);

    // ── Typography ────────────────────────────────────────────────
    public static final Font FONT_HEADING    = new Font("Segoe UI", Font.BOLD,  22);
    public static final Font FONT_SUBHEADING = new Font("Segoe UI", Font.BOLD,  15);
    public static final Font FONT_BODY       = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD  = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font FONT_SMALL      = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BUTTON     = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font FONT_NAV        = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font FONT_PRICE      = new Font("Segoe UI", Font.BOLD,  16);
    public static final Font FONT_LOGO       = new Font("Segoe UI", Font.BOLD,  20);

    // ── Dimensions ────────────────────────────────────────────────
    public static final int SIDEBAR_WIDTH    = 200;
    public static final int HEADER_HEIGHT    = 60;
    public static final int BUTTON_HEIGHT    = 36;
    public static final int FIELD_HEIGHT     = 36;
    public static final int CARD_ARC         = 12;       // rounded corner radius
    public static final int PAD              = 16;       // standard padding
    public static final int PAD_SM           = 8;
    public static final int PAD_LG           = 24;

    // ── Window size ───────────────────────────────────────────────
    public static final int WINDOW_WIDTH     = 1100;
    public static final int WINDOW_HEIGHT    = 720;
}
