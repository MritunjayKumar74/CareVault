import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class Main extends JFrame {

    // ── Palette ───────────────────────────────────────────────────────────────
    private static final Color SIDEBAR_BG      = new Color(60,  20,  50);
    private static final Color SIDEBAR_HOVER   = new Color(90,  35,  75);
    private static final Color SIDEBAR_ACTIVE  = new Color(255, 105, 180);
    private static final Color SIDEBAR_SECTION = new Color(120, 70, 100);
    private static final Color CONTENT_BG      = new Color(255, 245, 250);
    private static final Color PINK_HOT        = new Color(255, 105, 180);
    private static final Color PINK_SOFT       = new Color(255, 182, 213);
    private static final Color LILAC           = new Color(200, 162, 255);
    private static final Color TEXT_LIGHT      = new Color(255, 230, 242);
    private static final Color TEXT_MUTED_SIDE = new Color(180, 120, 155);
    private static final Color TEXT_DARK       = new Color(80,  30,  60);
    private static final Color DIVIDER         = new Color(90,  40,  70);

    // ── Fonts ─────────────────────────────────────────────────────────────────
    private static final Font F_LOGO    = new Font("Segoe UI Emoji", Font.BOLD,  18);
    private static final Font F_ROLE    = new Font("Segoe UI Emoji", Font.PLAIN, 10);
    private static final Font F_USER    = new Font("Segoe UI Emoji", Font.BOLD,  12);
    private static final Font F_SECTION = new Font("Segoe UI Emoji", Font.BOLD,   9);
    private static final Font F_MENU    = new Font("Segoe UI Emoji", Font.PLAIN, 13);
    private static final Font F_LOGOUT  = new Font("Segoe UI Emoji", Font.BOLD,  12);

    // ── State ─────────────────────────────────────────────────────────────────
    private JPanel         contentArea;
    private SidebarButton  activeButton;
    private final String   role = LoginScreen.loggedInRole;

    // ── Menu definition: [emoji, label, panelKey] ────────────────────────────
    // panelKey is used to instantiate the right panel
    private static final String[][] ALL_MENU = {
            // section, emoji, label, panelKey, roles-allowed
            {"", "🏠", "Dashboard",       "DASHBOARD",   "PATIENT,DOCTOR,LAB,PHARMACY,TPA,ADMIN"},
            {"", "👤", "My Profile",      "PROFILE",     "PATIENT,DOCTOR,LAB,PHARMACY,TPA,ADMIN"},
            {"CLINICAL", "🏥", "Encounters",     "ENCOUNTERS",  "PATIENT,DOCTOR"},
            {"", "💊", "Prescriptions",   "PRESCRIPTIONS","PATIENT,DOCTOR,PHARMACY"},
            {"", "🏪", "Pharmacy Order",  "PHARMACY",    "PATIENT,PHARMACY"},
            {"", "🧪", "Lab Workflow",    "LAB",         "PATIENT,DOCTOR,LAB"},
            {"", "📄", "Documents",       "DOCUMENTS",   "PATIENT,DOCTOR,LAB,ADMIN"},
            {"INSURANCE", "🏦", "Insurance & Claims","INSURANCE","PATIENT,TPA"},
            {"PERSONAL", "🩺", "Symptom Logger",  "SYMPTOMS",    "PATIENT"},
            {"", "🤖", "LLM Assistant",   "LLM",         "PATIENT"},
            {"", "🔐", "Consent",         "CONSENT",     "PATIENT,DOCTOR"},
            {"ADMIN", "📊", "Audit Log",       "AUDIT",       "ADMIN"},
            {"", "⚙️",  "Admin Panel",     "ADMIN_PANEL", "ADMIN"},
    };

    public Main() {
        super("CareVault");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.add(buildSidebar(),     BorderLayout.WEST);
        root.add(buildContentArea(), BorderLayout.CENTER);
        setContentPane(root);

        // Load dashboard by default
        loadPanel("DASHBOARD");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // SIDEBAR
    // ═══════════════════════════════════════════════════════════════════════════
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout(0, 0));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(220, 0));

        sidebar.add(buildSidebarTop(),    BorderLayout.NORTH);
        sidebar.add(buildSidebarMenu(),   BorderLayout.CENTER);
        sidebar.add(buildSidebarBottom(), BorderLayout.SOUTH);

        return sidebar;
    }

    // ── Top: logo + user info ─────────────────────────────────────────────────
    private JPanel buildSidebarTop() {
        JPanel top = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(45, 12, 38));
                g2.fillRect(0, 0, getWidth(), getHeight());
                // bottom divider
                g2.setColor(DIVIDER);
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
            }
        };
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBorder(new EmptyBorder(20, 16, 16, 16));
        top.setOpaque(false);

        // Logo row
        JPanel logoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        logoRow.setOpaque(false);

        JLabel logoIcon = new JLabel("💊");
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));

        JLabel logoText = new JLabel("CareVault");
        logoText.setFont(F_LOGO);
        logoText.setForeground(Color.WHITE);

        logoRow.add(logoIcon);
        logoRow.add(logoText);
        logoRow.setAlignmentX(LEFT_ALIGNMENT);

        // Divider
        JSeparator sep = makeSidebarDivider();

        // User info card
        JPanel userCard = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 15));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        userCard.setLayout(new BoxLayout(userCard, BoxLayout.Y_AXIS));
        userCard.setOpaque(false);
        userCard.setBorder(new EmptyBorder(10, 12, 10, 12));
        userCard.setAlignmentX(LEFT_ALIGNMENT);
        userCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        // Avatar circle + email
        JPanel avatarRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        avatarRow.setOpaque(false);

        JLabel avatar = new JLabel(getAvatarEmoji()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PINK_HOT);
                g2.fillOval(0, 0, 32, 32);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        avatar.setForeground(Color.WHITE);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(32, 32));

        JPanel infoCol = new JPanel();
        infoCol.setLayout(new BoxLayout(infoCol, BoxLayout.Y_AXIS));
        infoCol.setOpaque(false);

        // Truncate email if too long
        String email = LoginScreen.loggedInEmail;
        if (email.length() > 18) email = email.substring(0, 15) + "...";

        JLabel emailLbl = new JLabel(email);
        emailLbl.setFont(F_USER);
        emailLbl.setForeground(TEXT_LIGHT);

        JLabel roleLbl = new JLabel(buildRoleBadgeText());
        roleLbl.setFont(F_ROLE);
        roleLbl.setForeground(getRoleColor());

        infoCol.add(emailLbl);
        infoCol.add(Box.createVerticalStrut(2));
        infoCol.add(roleLbl);

        avatarRow.add(avatar);
        avatarRow.add(infoCol);
        userCard.add(avatarRow);

        top.add(logoRow);
        top.add(Box.createVerticalStrut(14));
        top.add(sep);
        top.add(Box.createVerticalStrut(12));
        top.add(userCard);

        return top;
    }

    // ── Menu ──────────────────────────────────────────────────────────────────
    private JScrollPane buildSidebarMenu() {
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBackground(SIDEBAR_BG);
        menu.setBorder(new EmptyBorder(8, 0, 8, 0));

        String lastSection = "";

        for (String[] item : ALL_MENU) {
            String section   = item[0];
            String emoji     = item[1];
            String label     = item[2];
            String panelKey  = item[3];
            String rolesStr  = item[4];

            // Skip if role not allowed
            if (!isRoleAllowed(rolesStr)) continue;

            // Section header
            if (!section.isEmpty() && !section.equals(lastSection)) {
                menu.add(buildSectionHeader(section));
                lastSection = section;
            }

            // Menu button
            SidebarButton btn = new SidebarButton(emoji, label);
            btn.addActionListener(e -> {
                setActiveButton(btn);
                loadPanel(panelKey);
            });
            menu.add(btn);

            // Set dashboard as default active
            if (panelKey.equals("DASHBOARD")) {
                activeButton = btn;
                btn.setActive(true);
            }
        }

        menu.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(menu);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBackground(SIDEBAR_BG);
        scroll.getViewport().setBackground(SIDEBAR_BG);
        return scroll;
    }

    // ── Bottom: logout ────────────────────────────────────────────────────────
    private JPanel buildSidebarBottom() {
        JPanel bottom = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(45, 12, 38));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(DIVIDER);
                g2.drawLine(0, 0, getWidth(), 0);
            }
        };
        bottom.setLayout(new BorderLayout());
        bottom.setBorder(new EmptyBorder(10, 12, 16, 12));
        bottom.setOpaque(false);

        JButton btnLogout = new JButton("🚪  Logout") {
            private boolean hovered = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? new Color(255, 80, 120) : new Color(180, 40, 80));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnLogout.setFont(F_LOGOUT);
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setContentAreaFilled(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setPreferredSize(new Dimension(0, 38));
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> logout());

        bottom.add(btnLogout, BorderLayout.CENTER);
        return bottom;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // CONTENT AREA
    // ═══════════════════════════════════════════════════════════════════════════
    private JPanel buildContentArea() {
        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(CONTENT_BG);
        return contentArea;
    }

    private void loadPanel(String panelKey) {
        contentArea.removeAll();

        JPanel panel = switch (panelKey) {
            case "PHARMACY"    -> new PharmacyOrderPanel();
            case "DASHBOARD"   -> new DashboardPanel();
            default            -> buildPlaceholderPanel(panelKey);
        };

        contentArea.add(panel, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    // ── Placeholder for panels not yet built ──────────────────────────────────
    private JPanel buildPlaceholderPanel(String panelKey) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(CONTENT_BG);

        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255,255,255, 200));
                g2.fillRoundRect(0, 0, getWidth()-4, getHeight()-4, 20, 20);
                g2.setColor(new Color(255, 218, 235));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth()-5, getHeight()-5, 20, 20);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(40, 60, 40, 60));

        JLabel emoji = new JLabel("🚧", SwingConstants.CENTER);
        emoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        emoji.setAlignmentX(CENTER_ALIGNMENT);

        JLabel title = new JLabel("Coming Soon!", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI Emoji", Font.BOLD, 22));
        title.setForeground(TEXT_DARK);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel sub = new JLabel(panelKey + " panel is under construction", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        sub.setForeground(new Color(180, 100, 140));
        sub.setAlignmentX(CENTER_ALIGNMENT);

        card.add(emoji);
        card.add(Box.createVerticalStrut(12));
        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(sub);

        p.add(card);
        return p;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // SIDEBAR BUTTON INNER CLASS
    // ═══════════════════════════════════════════════════════════════════════════
    class SidebarButton extends JPanel {
        private boolean hovered = false;
        private boolean active  = false;
        private final JLabel emojiLbl;
        private final JLabel textLbl;
        private final List<ActionListener> listeners = new ArrayList<>();

        SidebarButton(String emoji, String label) {
            setLayout(new FlowLayout(FlowLayout.LEFT, 12, 0));
            setOpaque(false);
            setPreferredSize(new Dimension(220, 42));
            setMaximumSize(new Dimension(220, 42));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            emojiLbl = new JLabel(emoji);
            emojiLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            emojiLbl.setForeground(TEXT_LIGHT);
            emojiLbl.setPreferredSize(new Dimension(22, 42));
            emojiLbl.setVerticalAlignment(SwingConstants.CENTER);

            textLbl = new JLabel(label);
            textLbl.setFont(F_MENU);
            textLbl.setForeground(TEXT_LIGHT);
            textLbl.setVerticalAlignment(SwingConstants.CENTER);
            textLbl.setPreferredSize(new Dimension(150, 42));

            add(emojiLbl);
            add(textLbl);

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
                public void mouseClicked(MouseEvent e) {
                    for (ActionListener l : listeners)
                        l.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""));
                }
            });
        }

        public void setActive(boolean a) {
            this.active = a;
            textLbl.setForeground(a ? Color.WHITE : TEXT_LIGHT);
            textLbl.setFont(a ? F_MENU.deriveFont(Font.BOLD) : F_MENU);
            repaint();
        }

        public void addActionListener(ActionListener l) { listeners.add(l); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (active) {
                // Active: pink left bar + background
                g2.setColor(new Color(255, 105, 180, 30));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(PINK_HOT);
                g2.fillRect(0, 0, 4, getHeight());
            } else if (hovered) {
                g2.setColor(new Color(255, 255, 255, 15));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // HELPERS
    // ═══════════════════════════════════════════════════════════════════════════
    private void setActiveButton(SidebarButton btn) {
        if (activeButton != null) activeButton.setActive(false);
        activeButton = btn;
        btn.setActive(true);
    }

    private boolean isRoleAllowed(String rolesStr) {
        return Arrays.asList(rolesStr.split(",")).contains(role);
    }

    private JPanel buildSectionHeader(String title) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 6));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(220, 30));

        JLabel lbl = new JLabel(title.toUpperCase());
        lbl.setFont(F_SECTION);
        lbl.setForeground(TEXT_MUTED_SIDE);
        p.add(lbl);
        return p;
    }

    private JSeparator makeSidebarDivider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(DIVIDER);
        sep.setBackground(DIVIDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(LEFT_ALIGNMENT);
        return sep;
    }

    private String getAvatarEmoji() {
        return switch (role) {
            case "DOCTOR"   -> "👨‍⚕️";
            case "LAB"      -> "🔬";
            case "PHARMACY" -> "💊";
            case "TPA"      -> "🏦";
            case "ADMIN"    -> "⚙️";
            default         -> "👤";
        };
    }

    private String buildRoleBadgeText() {
        return "● " + role;
    }

    private Color getRoleColor() {
        return switch (role) {
            case "DOCTOR"   -> new Color(130, 220, 255);
            case "LAB"      -> new Color(130, 255, 180);
            case "PHARMACY" -> new Color(255, 200, 100);
            case "TPA"      -> new Color(200, 160, 255);
            case "ADMIN"    -> new Color(255, 140, 140);
            default         -> PINK_SOFT;
        };
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Logout",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            LoginScreen.loggedInUserId = -1;
            LoginScreen.loggedInRole   = "";
            LoginScreen.loggedInEmail  = "";
            dispose();
            SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
        }
    }

    // ── Entry point ───────────────────────────────────────────────────────────
    public static void main(String[] args) {
        Font emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, 13);
        UIManager.put("Label.font",      emojiFont);
        UIManager.put("Button.font",     new Font("Segoe UI Emoji", Font.BOLD, 13));
        UIManager.put("TextField.font",  emojiFont);
        UIManager.put("TextArea.font",   new Font("Consolas", Font.PLAIN, 12));
        UIManager.put("TabbedPane.font", new Font("Segoe UI Emoji", Font.BOLD, 12));

        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }
}