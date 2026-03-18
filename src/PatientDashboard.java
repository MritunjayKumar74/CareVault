import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class PatientDashboard extends JPanel {

    // ── Palette ───────────────────────────────────────────────────────────────
    private static final Color BG          = new Color(255, 245, 250);
    private static final Color CARD_WHITE  = Color.WHITE;
    private static final Color PINK_HOT    = new Color(255, 105, 180);
    private static final Color PINK_SOFT   = new Color(255, 182, 213);
    private static final Color PINK_PALE   = new Color(255, 218, 235);
    private static final Color LILAC       = new Color(200, 162, 255);
    private static final Color LILAC_SOFT  = new Color(230, 210, 255);
    private static final Color MINT        = new Color(100, 220, 170);
    private static final Color MINT_SOFT   = new Color(200, 245, 225);
    private static final Color PEACH       = new Color(255, 180, 120);
    private static final Color PEACH_SOFT  = new Color(255, 225, 195);
    private static final Color SKY         = new Color(120, 200, 255);
    private static final Color SKY_SOFT    = new Color(200, 235, 255);
    private static final Color TEXT_DARK   = new Color(80,  30,  60);
    private static final Color TEXT_MID    = new Color(180, 80, 130);
    private static final Color TEXT_MUTED  = new Color(200, 150, 175);

    // ── Fonts ─────────────────────────────────────────────────────────────────
    private static final Font F_HEADER  = new Font("Segoe UI Emoji", Font.BOLD,  17);
    private static final Font F_SECTION = new Font("Segoe UI Emoji", Font.BOLD,  13);
    private static final Font F_STAT    = new Font("Segoe UI Emoji", Font.BOLD,  32);
    private static final Font F_LABEL   = new Font("Segoe UI Emoji", Font.PLAIN, 11);
    private static final Font F_SMALL   = new Font("Segoe UI Emoji", Font.PLAIN, 11);
    private static final Font F_BTN     = new Font("Segoe UI Emoji", Font.BOLD,  12);
    private static final Font F_TABLE   = new Font("Segoe UI Emoji", Font.PLAIN, 12);

    // ── Stat labels ───────────────────────────────────────────────────────────
    private JLabel lblEncounters    = makeStatValue("...");
    private JLabel lblPrescriptions = makeStatValue("...");
    private JLabel lblLabOrders     = makeStatValue("...");
    private JLabel lblClaims        = makeStatValue("...");

    // ── Activity table ────────────────────────────────────────────────────────
    private DefaultTableModel activityModel;

    // ── Session ───────────────────────────────────────────────────────────────
    private final int userId = LoginScreen.loggedInUserId;

    public PatientDashboard() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG);
        add(buildHeader(),  BorderLayout.NORTH);
        add(buildBody(),    BorderLayout.CENTER);
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
                g2.setColor(PINK_PALE);
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
            }
        };
        p.setPreferredSize(new Dimension(0, 65));
        p.setLayout(new BorderLayout());
        p.setBorder(new EmptyBorder(0, 24, 0, 24));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        JLabel icon = new JLabel("🏠");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setOpaque(false);

        JLabel title = new JLabel("Patient Dashboard");
        title.setFont(F_HEADER);
        title.setForeground(TEXT_DARK);

        JLabel sub = new JLabel("Welcome back, " + LoginScreen.loggedInEmail + "  •  Your health summary");
        sub.setFont(F_SMALL);
        sub.setForeground(TEXT_MID);

        col.add(Box.createVerticalStrut(12));
        col.add(title);
        col.add(Box.createVerticalStrut(2));
        col.add(sub);

        left.add(icon);
        left.add(col);
        p.add(left, BorderLayout.WEST);

        // Date top right
        JLabel date = new JLabel(new java.util.Date().toString().substring(0, 10));
        date.setFont(F_SMALL);
        date.setForeground(TEXT_MID);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        right.add(date);
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
        body.add(buildStatCards(),     BorderLayout.NORTH);
        body.add(buildBottomSection(), BorderLayout.CENTER);
        return body;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // STAT CARDS
    // ═══════════════════════════════════════════════════════════════════════════
    private JPanel buildStatCards() {
        JPanel row = new JPanel(new GridLayout(1, 4, 14, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(0, 120));

        row.add(buildStatCard("🏥", "Total Visits",         lblEncounters,    PINK_HOT, PINK_PALE));
        row.add(buildStatCard("💊", "Active Prescriptions", lblPrescriptions, LILAC,    LILAC_SOFT));
        row.add(buildStatCard("🧪", "Lab Orders",           lblLabOrders,     MINT,     MINT_SOFT));
        row.add(buildStatCard("🏦", "Insurance Claims",     lblClaims,        PEACH,    PEACH_SOFT));

        return row;
    }

    private JPanel buildStatCard(String emoji, String label,
                                 JLabel valueLbl, Color accent, Color bg) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0,0,0,12));
                g2.fillRoundRect(3, 3, getWidth()-3, getHeight()-3, 16, 16);
                g2.setColor(CARD_WHITE);
                g2.fillRoundRect(0, 0, getWidth()-3, getHeight()-3, 16, 16);
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, 5, getHeight()-3, 4, 4);
                g2.setColor(bg);
                g2.fillOval(getWidth()-55, -15, 70, 70);
                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(14, 18, 14, 14));

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

        JLabel emojiLbl = new JLabel(emoji, SwingConstants.CENTER);
        emojiLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        emojiLbl.setForeground(accent);
        emojiLbl.setPreferredSize(new Dimension(44, 44));

        card.add(left,     BorderLayout.CENTER);
        card.add(emojiLbl, BorderLayout.EAST);
        return card;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // BOTTOM: activity + quick actions
    // ═══════════════════════════════════════════════════════════════════════════
    private JPanel buildBottomSection() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0; gbc.weighty = 1.0;

        gbc.gridx = 0; gbc.weightx = 0.65;
        gbc.insets = new Insets(0, 0, 0, 14);
        p.add(buildRecentActivity(), gbc);

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
        table.setBackground(CARD_WHITE);
        table.getTableHeader().setBackground(new Color(255, 235, 245));
        table.getTableHeader().setForeground(TEXT_MID);
        table.getTableHeader().setFont(new Font("Segoe UI Emoji", Font.BOLD, 11));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, PINK_PALE));
        table.getTableHeader().setReorderingAllowed(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(220);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
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

        card.add(makeActionBtn("💊  Place Pharmacy Order",  PINK_HOT, PINK_PALE));
        card.add(Box.createVerticalStrut(10));
        card.add(makeActionBtn("🏥  View My Encounters",    LILAC,    LILAC_SOFT));
        card.add(Box.createVerticalStrut(10));
        card.add(makeActionBtn("📋  My Prescriptions",      MINT,     MINT_SOFT));
        card.add(Box.createVerticalStrut(10));
        card.add(makeActionBtn("🧪  Lab Results",           SKY,      SKY_SOFT));
        card.add(Box.createVerticalStrut(10));
        card.add(makeActionBtn("🏦  Insurance Claims",      PEACH,    PEACH_SOFT));
        card.add(Box.createVerticalStrut(10));
        card.add(makeActionBtn("🩺  Log Symptoms",          new Color(255,150,150), new Color(255,220,220)));

        card.add(Box.createVerticalGlue());

        // Divider
        card.add(Box.createVerticalStrut(10));
        JSeparator sep = new JSeparator();
        sep.setForeground(PINK_PALE);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(LEFT_ALIGNMENT);
        card.add(sep);
        card.add(Box.createVerticalStrut(10));

        // Refresh
        JButton refresh = makeRefreshBtn();
        refresh.addActionListener(e -> loadStats());
        card.add(refresh);

        return card;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // DB LOADING
    // ═══════════════════════════════════════════════════════════════════════════
    private void loadStats() {
        lblEncounters.setText("...");
        lblPrescriptions.setText("...");
        lblLabOrders.setText("...");
        lblClaims.setText("...");
        activityModel.setRowCount(0);

        new SwingWorker<int[], Void>() {
            int[] counts = new int[4];

            @Override protected int[] doInBackground() {
                try (Connection conn = DBConnection.getConnection()) {

                    // Encounters
                    try (PreparedStatement ps = conn.prepareStatement(
                            "SELECT COUNT(*) FROM encounter WHERE patient_id = ?")) {
                        ps.setInt(1, userId); ResultSet rs = ps.executeQuery();
                        if (rs.next()) counts[0] = rs.getInt(1);
                    }

                    // Active Prescriptions
                    try (PreparedStatement ps = conn.prepareStatement(
                            "SELECT COUNT(*) FROM prescription WHERE patient_id = ? AND status = 'ACTIVE'")) {
                        ps.setInt(1, userId); ResultSet rs = ps.executeQuery();
                        if (rs.next()) counts[1] = rs.getInt(1);
                    }

                    // Lab Orders
                    try (PreparedStatement ps = conn.prepareStatement(
                            "SELECT COUNT(*) FROM lab_order WHERE patient_id = ?")) {
                        ps.setInt(1, userId); ResultSet rs = ps.executeQuery();
                        if (rs.next()) counts[2] = rs.getInt(1);
                    }

                    // Claims
                    try (PreparedStatement ps = conn.prepareStatement(
                            "SELECT COUNT(*) FROM claim WHERE patient_id = ?")) {
                        ps.setInt(1, userId); ResultSet rs = ps.executeQuery();
                        if (rs.next()) counts[3] = rs.getInt(1);
                    }

                    // Recent activity
                    loadRecentActivity(conn);

                } catch (SQLException e) {
                    SwingUtilities.invokeLater(() ->
                            activityModel.addRow(new Object[]{"Error", e.getMessage(), "-", "-"}));
                }
                return counts;
            }

            @Override protected void done() {
                try {
                    int[] c = get();
                    lblEncounters.setText(String.valueOf(c[0]));
                    lblPrescriptions.setText(String.valueOf(c[1]));
                    lblLabOrders.setText(String.valueOf(c[2]));
                    lblClaims.setText(String.valueOf(c[3]));
                } catch (Exception ignored) {}
            }
        }.execute();
    }

    private void loadRecentActivity(Connection conn) throws SQLException {
        // Recent encounters
        try (PreparedStatement ps = conn.prepareStatement("""
            SELECT COALESCE(diagnosis_summary, complaint_summary, 'Visit') AS detail,
                   DATE(visit_datetime) AS dt
            FROM   encounter WHERE patient_id = ?
            ORDER  BY visit_datetime DESC LIMIT 3
        """)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String detail = rs.getString("detail");
                if (detail != null && detail.length() > 35) detail = detail.substring(0, 32) + "...";
                final String[] row = {"🏥 Encounter", detail, rs.getString("dt"), "Completed"};
                SwingUtilities.invokeLater(() -> activityModel.addRow(row));
            }
        }

        // Recent prescriptions
        try (PreparedStatement ps = conn.prepareStatement("""
            SELECT prescription_id, issue_date, status
            FROM   prescription WHERE patient_id = ?
            ORDER  BY issue_date DESC LIMIT 3
        """)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String status = rs.getString("status");
                String badge  = status.equals("ACTIVE") ? "Active" : status;
                final String[] row = {"💊 Prescription",
                        "Rx #" + rs.getInt("prescription_id"),
                        rs.getString("issue_date"), badge};
                SwingUtilities.invokeLater(() -> activityModel.addRow(row));
            }
        }

        // Recent pharmacy orders
        try (PreparedStatement ps = conn.prepareStatement("""
            SELECT order_id, DATE(order_time) AS dt, status
            FROM   pharmacy_order WHERE patient_id = ?
            ORDER  BY order_time DESC LIMIT 2
        """)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                final String[] row = {"🏪 Pharmacy",
                        "Order #" + rs.getInt("order_id"),
                        rs.getString("dt"), rs.getString("status")};
                SwingUtilities.invokeLater(() -> activityModel.addRow(row));
            }
        }

        // Recent lab orders
        try (PreparedStatement ps = conn.prepareStatement("""
            SELECT lab_order_id, order_date, status
            FROM   lab_order WHERE patient_id = ?
            ORDER  BY order_date DESC LIMIT 2
        """)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                final String[] row = {"🧪 Lab Order",
                        "Lab #" + rs.getInt("lab_order_id"),
                        rs.getString("order_date"), rs.getString("status")};
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
                g2.setColor(new Color(0,0,0,12));
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

    private JButton makeActionBtn(String text, Color accent, Color bg) {
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

    private JButton makeRefreshBtn() {
        JButton btn = new JButton("🔄  Refresh Stats") {
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
        btn.setFont(F_BTN);
        btn.setForeground(PINK_HOT);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── Standalone test ───────────────────────────────────────────────────────
    public static void main(String[] args) {
        Font ef = new Font("Segoe UI Emoji", Font.PLAIN, 13);
        UIManager.put("Label.font",     ef);
        UIManager.put("Button.font",    new Font("Segoe UI Emoji", Font.BOLD, 13));
        UIManager.put("TextField.font", ef);
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        LoginScreen.loggedInUserId = 1;
        LoginScreen.loggedInEmail  = "patient@carevault.com";
        LoginScreen.loggedInRole   = "PATIENT";

        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Patient Dashboard Test");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(950, 660);
            f.setLocationRelativeTo(null);
            f.setContentPane(new PatientDashboard());
            f.setVisible(true);
        });
    }
}