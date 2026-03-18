import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DashboardPanel extends JPanel {

    // ── Palette ───────────────────────────────────────────────────────────────
    private static final Color BG           = new Color(255, 245, 250);
    private static final Color CARD_WHITE   = Color.WHITE;
    private static final Color PINK_HOT     = new Color(255, 105, 180);
    private static final Color PINK_SOFT    = new Color(255, 182, 213);
    private static final Color PINK_PALE    = new Color(255, 218, 235);
    private static final Color LILAC        = new Color(200, 162, 255);
    private static final Color LILAC_SOFT   = new Color(230, 210, 255);
    private static final Color MINT         = new Color(100, 220, 170);
    private static final Color MINT_SOFT    = new Color(200, 245, 225);
    private static final Color PEACH        = new Color(255, 180, 120);
    private static final Color PEACH_SOFT   = new Color(255, 225, 195);
    private static final Color SKY          = new Color(120, 200, 255);
    private static final Color SKY_SOFT     = new Color(200, 235, 255);
    private static final Color TEXT_DARK    = new Color(80,  30,  60);
    private static final Color TEXT_MID     = new Color(180, 80, 130);
    private static final Color TEXT_MUTED   = new Color(200, 150, 175);

    // ── Fonts ─────────────────────────────────────────────────────────────────
    private static final Font F_HEADER  = new Font("Segoe UI Emoji", Font.BOLD,  17);
    private static final Font F_SECTION = new Font("Segoe UI Emoji", Font.BOLD,  13);
    private static final Font F_LABEL   = new Font("Segoe UI Emoji", Font.PLAIN, 11);
    private static final Font F_STAT    = new Font("Segoe UI Emoji", Font.BOLD,  32);
    private static final Font F_SMALL   = new Font("Segoe UI Emoji", Font.PLAIN, 11);
    private static final Font F_BTN     = new Font("Segoe UI Emoji", Font.BOLD,  12);
    private static final Font F_TABLE   = new Font("Segoe UI Emoji", Font.PLAIN, 12);

    // ── Stat card value labels (updated after DB load) ────────────────────────
    private JLabel lblEncounters    = makeStatValue("...");
    private JLabel lblPrescriptions = makeStatValue("...");
    private JLabel lblLabOrders     = makeStatValue("...");
    private JLabel lblClaims        = makeStatValue("...");

    // ── Recent activity table ─────────────────────────────────────────────────
    private DefaultTableModel activityModel;

    // ── Role ──────────────────────────────────────────────────────────────────
    private final String role    = LoginScreen.loggedInRole;
    private final int    userId  = LoginScreen.loggedInUserId;

    public DashboardPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG);

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildBody(),    BorderLayout.CENTER);

        // Load stats from DB in background
        loadStats();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // HEADER
    // ═══════════════════════════════════════════════════════════════════════════
    private JPanel buildHeader() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, PINK_SOFT, getWidth(), 0, LILAC_SOFT));
                g2.fillRect(0, 0, getWidth(), getHeight());
                // bottom border
                g2.setColor(PINK_PALE);
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
            }
        };
        p.setPreferredSize(new Dimension(0, 65));
        p.setLayout(new BorderLayout());
        p.setBorder(new EmptyBorder(0, 24, 0, 24));

        // Left: welcome text
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);

        JLabel icon = new JLabel("🏠");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));

        JPanel textCol = new JPanel();
        textCol.setLayout(new BoxLayout(textCol, BoxLayout.Y_AXIS));
        textCol.setOpaque(false);

        JLabel welcome = new JLabel("Welcome back, " + LoginScreen.loggedInEmail + "!");
        welcome.setFont(F_HEADER);
        welcome.setForeground(TEXT_DARK);

        JLabel sub = new JLabel("Here's your CareVault overview  •  Role: " + role);
        sub.setFont(F_SMALL);
        sub.setForeground(TEXT_MID);

        textCol.add(Box.createVerticalStrut(12));
        textCol.add(welcome);
        textCol.add(Box.createVerticalStrut(2));
        textCol.add(sub);

        left.add(icon);
        left.add(textCol);
        p.add(left, BorderLayout.WEST);

        // Right: date
        JLabel dateLbl = new JLabel(new java.util.Date().toString().substring(0, 10));
        dateLbl.setFont(F_SMALL);
        dateLbl.setForeground(TEXT_MID);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        right.add(dateLbl);
        p.add(right, BorderLayout.EAST);

        return p;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // BODY
    // ═══════════════════════════════════════════════════════════════════════════
    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setBackground(BG);
        body.setBorder(new EmptyBorder(20, 24, 20, 24));

        body.add(buildStatCards(),      BorderLayout.NORTH);
        body.add(buildBottomSection(),  BorderLayout.CENTER);

        return body;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // STAT CARDS ROW
    // ═══════════════════════════════════════════════════════════════════════════
    private JPanel buildStatCards() {
        JPanel row = new JPanel(new GridLayout(1, 4, 14, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(0, 120));

        row.add(buildStatCard("🏥", "Encounters",      lblEncounters,    PINK_HOT,  PINK_PALE));
        row.add(buildStatCard("💊", "Prescriptions",   lblPrescriptions, LILAC,     LILAC_SOFT));
        row.add(buildStatCard("🧪", "Lab Orders",      lblLabOrders,     MINT,      MINT_SOFT));
        row.add(buildStatCard("🏦", "Insurance Claims",lblClaims,        PEACH,     PEACH_SOFT));

        return row;
    }

    private JPanel buildStatCard(String emoji, String label,
                                 JLabel valueLbl, Color accent, Color bg) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // shadow
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fillRoundRect(3, 3, getWidth()-3, getHeight()-3, 16, 16);
                // card bg
                g2.setColor(CARD_WHITE);
                g2.fillRoundRect(0, 0, getWidth()-3, getHeight()-3, 16, 16);
                // left accent bar
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, 5, getHeight()-3, 4, 4);
                // top-right tinted circle
                g2.setColor(bg);
                g2.fillOval(getWidth()-55, -15, 70, 70);
                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(14, 18, 14, 14));

        // Left: value + label
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        valueLbl.setAlignmentX(LEFT_ALIGNMENT);
        valueLbl.setForeground(TEXT_DARK);

        JLabel labelLbl = new JLabel(label);
        labelLbl.setFont(F_LABEL);
        labelLbl.setForeground(TEXT_MUTED);
        labelLbl.setAlignmentX(LEFT_ALIGNMENT);

        left.add(valueLbl);
        left.add(Box.createVerticalStrut(4));
        left.add(labelLbl);

        // Right: emoji
        JLabel emojiLbl = new JLabel(emoji, SwingConstants.CENTER);
        emojiLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        emojiLbl.setForeground(accent);
        emojiLbl.setPreferredSize(new Dimension(44, 44));

        card.add(left,     BorderLayout.CENTER);
        card.add(emojiLbl, BorderLayout.EAST);

        return card;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // BOTTOM SECTION: recent activity + quick actions
    // ═══════════════════════════════════════════════════════════════════════════
    private JPanel buildBottomSection() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0; gbc.weighty = 1.0;

        // Recent activity (left, wider)
        gbc.gridx = 0; gbc.weightx = 0.65;
        gbc.insets = new Insets(0, 0, 0, 14);
        p.add(buildRecentActivity(), gbc);

        // Quick actions (right)
        gbc.gridx = 1; gbc.weightx = 0.35;
        gbc.insets = new Insets(0, 0, 0, 0);
        p.add(buildQuickActions(), gbc);

        return p;
    }

    // ── Recent Activity ───────────────────────────────────────────────────────
    private JPanel buildRecentActivity() {
        JPanel card = makeCard();
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel title = new JLabel("📋  Recent Activity");
        title.setFont(F_SECTION);
        title.setForeground(TEXT_DARK);

        // Table
        String[] cols = {"Type", "Details", "Date", "Status"};
        activityModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(activityModel) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                c.setBackground(row % 2 == 0 ? CARD_WHITE : new Color(255, 248, 252));
                c.setForeground(TEXT_DARK);
                c.setFont(F_TABLE);
                if (c instanceof JLabel) ((JLabel)c).setBorder(new EmptyBorder(6, 10, 6, 10));
                return c;
            }
        };
        table.setRowHeight(34);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.getTableHeader().setBackground(new Color(255, 235, 245));
        table.getTableHeader().setForeground(TEXT_MID);
        table.getTableHeader().setFont(new Font("Segoe UI Emoji", Font.BOLD, 11));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, PINK_PALE));
        table.getTableHeader().setReorderingAllowed(false);
        table.setBackground(CARD_WHITE);

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(90);
        table.getColumnModel().getColumn(1).setPreferredWidth(220);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(PINK_PALE, 1));
        scroll.getViewport().setBackground(CARD_WHITE);

        card.add(title,  BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    // ── Quick Actions ─────────────────────────────────────────────────────────
    private JPanel buildQuickActions() {
        JPanel card = makeCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel title = new JLabel("⚡  Quick Actions");
        title.setFont(F_SECTION);
        title.setForeground(TEXT_DARK);
        title.setAlignmentX(LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(14));

        // Role-based quick actions
        if (role.equals("PATIENT") || role.equals("PHARMACY")) {
            card.add(makeActionButton("💊  Place Pharmacy Order", PINK_HOT, PINK_PALE));
            card.add(Box.createVerticalStrut(10));
        }
        if (role.equals("PATIENT") || role.equals("DOCTOR")) {
            card.add(makeActionButton("🏥  View Encounters", LILAC, LILAC_SOFT));
            card.add(Box.createVerticalStrut(10));
        }
        if (role.equals("PATIENT") || role.equals("DOCTOR") || role.equals("PHARMACY")) {
            card.add(makeActionButton("📋  View Prescriptions", MINT, MINT_SOFT));
            card.add(Box.createVerticalStrut(10));
        }
        if (role.equals("PATIENT") || role.equals("TPA")) {
            card.add(makeActionButton("🏦  Insurance Claims", PEACH, PEACH_SOFT));
            card.add(Box.createVerticalStrut(10));
        }
        if (role.equals("PATIENT") || role.equals("DOCTOR") || role.equals("LAB")) {
            card.add(makeActionButton("🧪  Lab Results", SKY, SKY_SOFT));
            card.add(Box.createVerticalStrut(10));
        }
        if (role.equals("ADMIN")) {
            card.add(makeActionButton("📊  Audit Log", PINK_HOT, PINK_PALE));
            card.add(Box.createVerticalStrut(10));
            card.add(makeActionButton("⚙️  Admin Panel", LILAC, LILAC_SOFT));
            card.add(Box.createVerticalStrut(10));
        }

        card.add(Box.createVerticalGlue());

        // Refresh button at bottom
        card.add(Box.createVerticalStrut(10));
        JSeparator sep = new JSeparator();
        sep.setForeground(PINK_PALE);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(LEFT_ALIGNMENT);
        card.add(sep);
        card.add(Box.createVerticalStrut(10));

        JButton refresh = new JButton("🔄  Refresh Stats") {
            private boolean hovered = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? PINK_PALE : new Color(255, 240, 248));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(PINK_SOFT);
                g2.setStroke(new java.awt.BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        refresh.setFont(F_BTN);
        refresh.setForeground(PINK_HOT);
        refresh.setContentAreaFilled(false);
        refresh.setBorderPainted(false);
        refresh.setFocusPainted(false);
        refresh.setAlignmentX(LEFT_ALIGNMENT);
        refresh.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        refresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refresh.addActionListener(e -> loadStats());
        card.add(refresh);

        return card;
    }

    private JButton makeActionButton(String text, Color accent, Color bg) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? bg : CARD_WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(hovered ? accent : PINK_PALE);
                g2.setStroke(new java.awt.BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                // left accent dot
                g2.setColor(accent);
                g2.fillOval(10, getHeight()/2 - 4, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(F_BTN);
        btn.setForeground(TEXT_DARK);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(0, 24, 0, 10));
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // DB LOADING
    // ═══════════════════════════════════════════════════════════════════════════
    private void loadStats() {
        // Reset to loading state
        lblEncounters.setText("...");
        lblPrescriptions.setText("...");
        lblLabOrders.setText("...");
        lblClaims.setText("...");
        activityModel.setRowCount(0);

        new SwingWorker<Void, Void>() {
            int encounters = 0, prescriptions = 0, labOrders = 0, claims = 0;

            @Override protected Void doInBackground() {
                try (Connection conn = DBConnection.getConnection()) {

                    // ── Stat 1: Encounters ──
                    String encSQL = role.equals("DOCTOR")
                            ? "SELECT COUNT(*) FROM encounter WHERE doctor_id = ?"
                            : "SELECT COUNT(*) FROM encounter WHERE patient_id = ?";
                    try (PreparedStatement ps = conn.prepareStatement(encSQL)) {
                        ps.setInt(1, userId);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) encounters = rs.getInt(1);
                    }

                    // ── Stat 2: Active Prescriptions ──
                    String rxSQL = role.equals("DOCTOR")
                            ? "SELECT COUNT(*) FROM prescription WHERE doctor_id = ? AND status = 'ACTIVE'"
                            : "SELECT COUNT(*) FROM prescription WHERE patient_id = ? AND status = 'ACTIVE'";
                    try (PreparedStatement ps = conn.prepareStatement(rxSQL)) {
                        ps.setInt(1, userId);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) prescriptions = rs.getInt(1);
                    }

                    // ── Stat 3: Lab Orders ──
                    String labSQL = role.equals("LAB")
                            ? "SELECT COUNT(*) FROM lab_order WHERE lab_id = ?"
                            : role.equals("DOCTOR")
                            ? "SELECT COUNT(*) FROM lab_order WHERE doctor_id = ?"
                            : "SELECT COUNT(*) FROM lab_order WHERE patient_id = ?";
                    try (PreparedStatement ps = conn.prepareStatement(labSQL)) {
                        ps.setInt(1, userId);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) labOrders = rs.getInt(1);
                    }

                    // ── Stat 4: Claims ──
                    if (role.equals("PATIENT") || role.equals("TPA")) {
                        String claimSQL = "SELECT COUNT(*) FROM claim WHERE patient_id = ?";
                        try (PreparedStatement ps = conn.prepareStatement(claimSQL)) {
                            ps.setInt(1, userId);
                            ResultSet rs = ps.executeQuery();
                            if (rs.next()) claims = rs.getInt(1);
                        }
                    }

                    // ── Recent Activity ──
                    loadRecentActivity(conn);

                } catch (SQLException e) {
                    SwingUtilities.invokeLater(() ->
                            activityModel.addRow(new Object[]{"Error", e.getMessage(), "-", "-"}));
                }
                return null;
            }

            @Override protected void done() {
                lblEncounters.setText(String.valueOf(encounters));
                lblPrescriptions.setText(String.valueOf(prescriptions));
                lblLabOrders.setText(String.valueOf(labOrders));
                lblClaims.setText(String.valueOf(claims));
            }
        }.execute();
    }

    private void loadRecentActivity(Connection conn) throws SQLException {
        // Encounters
        String encSQL = """
            SELECT 'Encounter' AS type,
                   COALESCE(diagnosis_summary, complaint_summary) AS detail,
                   DATE(visit_datetime) AS dt,
                   'Completed' AS status
            FROM   encounter
            WHERE  patient_id = ?
            ORDER  BY visit_datetime DESC LIMIT 3
        """;
        try (PreparedStatement ps = conn.prepareStatement(encSQL)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String detail = rs.getString("detail");
                if (detail != null && detail.length() > 35)
                    detail = detail.substring(0, 32) + "...";
                final String[] row = {"🏥 Encounter", detail, rs.getString("dt"), "✓ Done"};
                SwingUtilities.invokeLater(() -> activityModel.addRow(row));
            }
        }

        // Prescriptions
        String rxSQL = """
            SELECT 'Prescription' AS type,
                   CONCAT('Rx #', prescription_id) AS detail,
                   issue_date AS dt,
                   status
            FROM   prescription
            WHERE  patient_id = ?
            ORDER  BY issue_date DESC LIMIT 3
        """;
        try (PreparedStatement ps = conn.prepareStatement(rxSQL)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String status = rs.getString("status");
                String statusIcon = status.equals("ACTIVE") ? "✓ Active" : status;
                final String[] row = {"💊 Prescription", rs.getString("detail"),
                        rs.getString("dt"), statusIcon};
                SwingUtilities.invokeLater(() -> activityModel.addRow(row));
            }
        }

        // Pharmacy Orders
        String orderSQL = """
            SELECT CONCAT('Order #', order_id) AS detail,
                   DATE(order_time) AS dt,
                   status
            FROM   pharmacy_order
            WHERE  patient_id = ?
            ORDER  BY order_time DESC LIMIT 2
        """;
        try (PreparedStatement ps = conn.prepareStatement(orderSQL)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                final String[] row = {"🏪 Pharmacy", rs.getString("detail"),
                        rs.getString("dt"), rs.getString("status")};
                SwingUtilities.invokeLater(() -> activityModel.addRow(row));
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // HELPERS
    // ═══════════════════════════════════════════════════════════════════════════
    private static JLabel makeStatValue(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(F_STAT);
        lbl.setForeground(TEXT_DARK);
        return lbl;
    }

    private JPanel makeCard() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 12));
                g2.fillRoundRect(3, 3, getWidth()-3, getHeight()-3, 16, 16);
                g2.setColor(CARD_WHITE);
                g2.fillRoundRect(0, 0, getWidth()-3, getHeight()-3, 16, 16);
                g2.setColor(PINK_PALE);
                g2.setStroke(new java.awt.BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-4, getHeight()-4, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        return card;
    }

    // ── Standalone test ───────────────────────────────────────────────────────
    public static void main(String[] args) {
        Font ef = new Font("Segoe UI Emoji", Font.PLAIN, 13);
        UIManager.put("Label.font",     ef);
        UIManager.put("Button.font",    new Font("Segoe UI Emoji", Font.BOLD, 13));
        UIManager.put("TextField.font", ef);

        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        // Inject test session
        LoginScreen.loggedInUserId = 1;
        LoginScreen.loggedInEmail  = "alice@example.com";
        LoginScreen.loggedInRole   = "PATIENT";

        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Dashboard Test");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(900, 650);
            f.setLocationRelativeTo(null);
            f.setContentPane(new DashboardPanel());
            f.setVisible(true);
        });
    }
}