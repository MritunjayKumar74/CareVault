import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List;

public class PharmacyOrderPanel extends JPanel {

    // ── Girlie Pop Colour Palette ────────────────────────────────────────────
    private static final Color BG_MAIN      = new Color(255, 240, 245);   // soft blush
    private static final Color BG_CARD      = new Color(255, 255, 255);   // white cards
    private static final Color BG_INPUT     = new Color(255, 245, 250);   // lightest pink
    private static final Color PINK_HOT     = new Color(255, 105, 180);   // hot pink accent
    private static final Color PINK_SOFT    = new Color(255, 182, 213);   // soft pink
    private static final Color PINK_PALE    = new Color(255, 218, 235);   // pale pink border
    private static final Color PINK_GLOW    = new Color(255, 105, 180, 50);
    private static final Color LILAC        = new Color(200, 162, 255);   // lavender accent
    private static final Color MINT         = new Color(152, 245, 200);   // mint for success
    private static final Color TEXT_DARK    = new Color(80,  40,  60);    // deep berry text
    private static final Color TEXT_MID     = new Color(180, 100, 140);   // mid pink text
    private static final Color TEXT_MUTED   = new Color(210, 160, 185);   // muted pink
    private static final Color ERROR_COLOR  = new Color(255, 80,  100);

    // ── Fonts ────────────────────────────────────────────────────────────────
    private static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_LABEL   = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_INPUT   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_MONO    = new Font("Consolas", Font.PLAIN, 12);
    private static final Font FONT_SECTION = new Font("Segoe UI", Font.BOLD, 11);
    private static final Font FONT_BTN     = new Font("Segoe UI", Font.BOLD, 13);

    // ── Fields ───────────────────────────────────────────────────────────────
    private StyledField txtPatientId  = new StyledField("e.g. 1");
    private StyledField txtPrescId    = new StyledField("e.g. 1");
    private StyledField txtPharmacyId = new StyledField("e.g. 5");
    private StyledField txtDrugName   = new StyledField("e.g. Aspirin");
    private StyledField txtQuantity   = new StyledField("e.g. 30");

    private DefaultTableModel drugTableModel;
    private JTable drugTable;
    private JTextArea txtStatus = new JTextArea();

    // ── Constructor ──────────────────────────────────────────────────────────
    public PharmacyOrderPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_MAIN);
        add(buildHeader(),   BorderLayout.NORTH);
        add(buildMainBody(), BorderLayout.CENTER);
    }

    // ── Header ───────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // dreamy pink-to-lilac gradient
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(255, 182, 213),
                        getWidth(), 0, new Color(200, 162, 255));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // sparkle dots decoration
                g2.setColor(new Color(255, 255, 255, 80));
                int[] sx = {30, 80, 140, 200, 260, 320, 400, 480, 560, 640, 720};
                int[] sy = {10, 25, 12,  30,  8,   22,  15,  28,  10,  20,  14};
                for (int i = 0; i < sx.length; i++) {
                    g2.fillOval(sx[i], sy[i], 4, 4);
                }
                // bottom soft shadow line
                g2.setColor(PINK_SOFT);
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            }
        };
        header.setPreferredSize(new Dimension(0, 75));
        header.setBorder(new EmptyBorder(0, 24, 0, 24));

        // Left: emoji + title
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        JLabel icon = new JLabel("💊✨");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);

        JLabel title = new JLabel("Pharmacy Dispensing");
        title.setFont(FONT_TITLE);
        title.setForeground(new Color(100, 30, 70));

        JLabel subtitle = new JLabel("✿  Prescription-locked order fulfilment  ✿");
        subtitle.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        subtitle.setForeground(new Color(160, 80, 120));

        titleBlock.add(title);
        titleBlock.add(subtitle);
        left.add(icon);
        left.add(titleBlock);

        // Right: cute status badge
        JPanel badge = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 180));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(PINK_SOFT);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setOpaque(false);
        badge.setBorder(new EmptyBorder(5, 14, 5, 14));
        JLabel badgeLbl = new JLabel("🌸 System Active");
        badgeLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badgeLbl.setForeground(new Color(200, 60, 120));
        badge.add(badgeLbl);

        JPanel rightWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightWrapper.setOpaque(false);
        rightWrapper.add(badge);

        header.add(left, BorderLayout.WEST);
        header.add(rightWrapper, BorderLayout.EAST);
        return header;
    }

    // ── Main body ────────────────────────────────────────────────────────────
    private JPanel buildMainBody() {
        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(BG_MAIN);
        body.setBorder(new EmptyBorder(18, 20, 18, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.45; gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 0, 12);
        body.add(buildLeftColumn(), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.55; gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 0, 0);
        body.add(buildRightColumn(), gbc);

        return body;
    }

    // ── Left column ──────────────────────────────────────────────────────────
    private JPanel buildLeftColumn() {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setOpaque(false);
        col.add(buildOrderDetailsCard());
        col.add(Box.createVerticalStrut(12));
        col.add(buildDrugEntryCard());
        col.add(Box.createVerticalStrut(12));
        col.add(buildPlaceOrderButton());
        return col;
    }

    private JPanel buildOrderDetailsCard() {
        JPanel card = createCard("🎀  Order Details", PINK_HOT);
        String[] labels = {"Patient ID", "Prescription ID", "Pharmacy ID"};
        String[] tags   = {"👤", "📋", "🏪"};
        StyledField[] inputs = {txtPatientId, txtPrescId, txtPharmacyId};
        for (int i = 0; i < labels.length; i++) {
            card.add(buildFieldRow(labels[i], tags[i], inputs[i]));
            if (i < labels.length - 1) card.add(Box.createVerticalStrut(10));
        }
        return card;
    }

    private JPanel buildDrugEntryCard() {
        JPanel card = createCard("💊  Add Drug", LILAC);
        card.add(buildFieldRow("Drug Name", "💉", txtDrugName));
        card.add(Box.createVerticalStrut(10));
        card.add(buildFieldRow("Quantity", "🔢", txtQuantity));
        card.add(Box.createVerticalStrut(14));

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JButton btnAdd    = createSecondaryButton("＋  Add Drug", PINK_HOT);
        JButton btnRemove = createSecondaryButton("－  Remove",   ERROR_COLOR);
        btnRow.add(btnAdd);
        btnRow.add(btnRemove);
        card.add(btnRow);

        btnAdd.addActionListener(e -> addDrugRow());
        btnRemove.addActionListener(e -> removeSelectedDrug());
        return card;
    }

    private JPanel buildPlaceOrderButton() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        JButton btn = new JButton("🌸  Place Pharmacy Order  🌸") {
            private boolean hovered = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c1 = hovered ? new Color(255, 80, 160) : PINK_HOT;
                Color c2 = hovered ? new Color(200, 80, 255) : LILAC;
                g2.setPaint(new GradientPaint(0, 0, c1, getWidth(), 0, c2));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                if (hovered) {
                    g2.setColor(PINK_GLOW);
                    g2.setStroke(new BasicStroke(4f));
                    g2.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 25, 25);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(0, 48));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> placeOrder());
        wrapper.add(btn, BorderLayout.CENTER);
        return wrapper;
    }

    // ── Right column ─────────────────────────────────────────────────────────
    private JPanel buildRightColumn() {
        JPanel col = new JPanel(new GridBagLayout());
        col.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH; gbc.gridx = 0; gbc.weightx = 1.0;

        gbc.gridy = 0; gbc.weighty = 0.40; gbc.insets = new Insets(0, 0, 12, 0);
        col.add(buildDrugTableCard(), gbc);

        gbc.gridy = 1; gbc.weighty = 0.60; gbc.insets = new Insets(0, 0, 0, 0);
        col.add(buildLogCard(), gbc);

        return col;
    }

    private JPanel buildDrugTableCard() {
        JPanel card = createCard("✨  Drugs to Dispense", PINK_SOFT);

        drugTableModel = new DefaultTableModel(new String[]{"Drug Name", "Quantity"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        drugTable = new JTable(drugTableModel) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (isRowSelected(row)) {
                    c.setBackground(PINK_HOT); c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(row % 2 == 0 ? BG_CARD : new Color(255, 245, 252));
                    c.setForeground(TEXT_DARK);
                }
                if (c instanceof JLabel) ((JLabel) c).setBorder(new EmptyBorder(6, 12, 6, 12));
                return c;
            }
        };
        drugTable.setBackground(BG_CARD);
        drugTable.setForeground(TEXT_DARK);
        drugTable.setFont(FONT_INPUT);
        drugTable.setRowHeight(34);
        drugTable.setShowGrid(false);
        drugTable.setIntercellSpacing(new Dimension(0, 1));
        drugTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        drugTable.getTableHeader().setReorderingAllowed(false);

        JTableHeader th = drugTable.getTableHeader();
        th.setBackground(new Color(255, 220, 235));
        th.setForeground(new Color(180, 40, 100));
        th.setFont(new Font("Segoe UI", Font.BOLD, 11));
        th.setPreferredSize(new Dimension(0, 32));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PINK_SOFT));

        JScrollPane scroll = new JScrollPane(drugTable);
        scroll.setBackground(BG_CARD);
        scroll.getViewport().setBackground(BG_CARD);
        scroll.setBorder(BorderFactory.createLineBorder(PINK_PALE, 1));
        card.add(scroll);
        return card;
    }

    private JPanel buildLogCard() {
        JPanel card = createCard("📋  Transaction Log", new Color(255, 200, 220));

        txtStatus.setEditable(false);
        txtStatus.setFont(FONT_MONO);
        txtStatus.setBackground(new Color(255, 248, 252));
        txtStatus.setForeground(new Color(180, 40, 100));
        txtStatus.setCaretColor(PINK_HOT);
        txtStatus.setBorder(new EmptyBorder(10, 12, 10, 12));
        txtStatus.setLineWrap(true);

        JScrollPane scroll = new JScrollPane(txtStatus);
        scroll.setBorder(BorderFactory.createLineBorder(PINK_PALE, 1));
        scroll.getViewport().setBackground(new Color(255, 248, 252));
        card.add(scroll);
        return card;
    }

    // ── Card + field helpers ──────────────────────────────────────────────────
    private JPanel createCard(String sectionTitle, Color accentColor) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // white card with very subtle pink shadow
                g2.setColor(new Color(255, 200, 220, 40));
                g2.fillRoundRect(3, 3, getWidth()-2, getHeight()-2, 16, 16);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth()-2, getHeight()-2, 16, 16);
                g2.setColor(PINK_PALE);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-2, getHeight()-2, 16, 16);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 16, 16, 16));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel lbl = new JLabel(sectionTitle);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(accentColor);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(0, 0, 10, 0));
        card.add(lbl);

        // dotted pink divider
        JSeparator sep = new JSeparator() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(PINK_PALE);
                g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                        0, new float[]{4, 4}, 0));
                g2.drawLine(0, 0, getWidth(), 0);
                g2.dispose();
            }
        };
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        sep.setAlignmentX(LEFT_ALIGNMENT);
        card.add(sep);
        card.add(Box.createVerticalStrut(12));
        return card;
    }

    private JPanel buildFieldRow(String labelText, String emoji, StyledField field) {
        JPanel row = new JPanel(new BorderLayout(0, 4));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row.setAlignmentX(LEFT_ALIGNMENT);

        JPanel labelRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        labelRow.setOpaque(false);

        JLabel emojiLbl = new JLabel(emoji + " ");
        emojiLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(TEXT_MID);

        labelRow.add(emojiLbl);
        labelRow.add(lbl);
        row.add(labelRow, BorderLayout.NORTH);
        row.add(field,    BorderLayout.CENTER);
        return row;
    }

    private JButton createSecondaryButton(String text, Color fg) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? new Color(255, 230, 240) : BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(hovered ? fg : PINK_PALE);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(fg);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(0, 36));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── Styled input field ────────────────────────────────────────────────────
    static class StyledField extends JTextField {
        private final String placeholder;

        StyledField(String placeholder) {
            this.placeholder = placeholder;
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(new Color(80, 40, 60));
            setBackground(new Color(255, 245, 250));
            setCaretColor(PINK_HOT);
            setOpaque(false);
            setBorder(new CompoundBorder(
                    new RoundedBorder(20, new Color(255, 182, 213)),
                    new EmptyBorder(7, 14, 7, 14)));

            addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    setBorder(new CompoundBorder(
                            new RoundedBorder(20, PINK_HOT),
                            new EmptyBorder(7, 14, 7, 14)));
                    repaint();
                }
                public void focusLost(FocusEvent e) {
                    setBorder(new CompoundBorder(
                            new RoundedBorder(20, new Color(255, 182, 213)),
                            new EmptyBorder(7, 14, 7, 14)));
                    repaint();
                }
            });
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(255, 245, 250));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            g2.dispose();
            super.paintComponent(g);
            if (getText().isEmpty() && !isFocusOwner()) {
                Graphics2D g3 = (Graphics2D) g.create();
                g3.setFont(new Font("Segoe UI", Font.ITALIC, 12));
                g3.setColor(new Color(210, 160, 185));
                Insets ins = getInsets();
                g3.drawString(placeholder, ins.left, getHeight() / 2 + 5);
                g3.dispose();
            }
        }
    }

    // ── Rounded border ────────────────────────────────────────────────────────
    static class RoundedBorder extends AbstractBorder {
        private final int radius; private final Color color;
        RoundedBorder(int radius, Color color) { this.radius = radius; this.color = color; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color); g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(x, y, w-1, h-1, radius, radius);
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(4,4,4,4); }
    }

    // ── Business logic (unchanged) ────────────────────────────────────────────
    private void addDrugRow() {
        String drug = txtDrugName.getText().trim();
        String qtyStr = txtQuantity.getText().trim();
        if (drug.isEmpty() || qtyStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "💊 Enter both drug name and quantity!"); return;
        }
        try {
            int qty = Integer.parseInt(qtyStr);
            if (qty <= 0) throw new NumberFormatException();
            drugTableModel.addRow(new Object[]{drug, qty});
            txtDrugName.setText(""); txtQuantity.setText(""); txtDrugName.requestFocus();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "🔢 Quantity must be a positive integer!");
        }
    }

    private void removeSelectedDrug() {
        int row = drugTable.getSelectedRow();
        if (row >= 0) drugTableModel.removeRow(row);
    }

    private void placeOrder() {
        txtStatus.setText("");
        int patientId, prescriptionId, pharmacyId;
        try {
            patientId      = Integer.parseInt(txtPatientId.getText().trim());
            prescriptionId = Integer.parseInt(txtPrescId.getText().trim());
            pharmacyId     = Integer.parseInt(txtPharmacyId.getText().trim());
        } catch (NumberFormatException ex) {
            log("✗ Patient ID, Prescription ID, and Pharmacy ID must be integers."); return;
        }
        if (drugTableModel.getRowCount() == 0) {
            log("✗ Add at least one drug before placing an order! 💊"); return;
        }
        List<String[]> items = new ArrayList<>();
        for (int i = 0; i < drugTableModel.getRowCount(); i++)
            items.add(new String[]{
                    (String) drugTableModel.getValueAt(i, 0),
                    drugTableModel.getValueAt(i, 1).toString()});

        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() {
                runPlaceOrder(patientId, prescriptionId, pharmacyId, items); return null;
            }
        }.execute();
    }

    private void runPlaceOrder(int patientId, int prescriptionId,
                               int pharmacyId, List<String[]> items) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            log("🌸 Verifying prescription #" + prescriptionId + " ...");
            try (PreparedStatement ps = conn.prepareStatement("""
                SELECT p.status, p.expiry_date, pr.refills_allowed, pr.refills_used
                FROM   prescription p
                JOIN   prescription_rules pr ON p.prescription_id = pr.prescription_id
                WHERE  p.prescription_id = ? AND p.patient_id = ?
                FOR UPDATE
            """)) {
                ps.setInt(1, prescriptionId); ps.setInt(2, patientId);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) throw new Exception("Prescription #" + prescriptionId + " not found for patient #" + patientId + ".");
                String status       = rs.getString("status");
                java.sql.Date expiry = rs.getDate("expiry_date");
                int allowed         = rs.getInt("refills_allowed");
                int used            = rs.getInt("refills_used");
                log("  Status       : " + status);
                log("  Expiry Date  : " + expiry);
                log("  Refills Used : " + used + " / " + allowed);
                if (!"ACTIVE".equals(status)) throw new Exception("Prescription is not ACTIVE (current: " + status + ").");
                if (expiry != null && expiry.before(new java.util.Date())) throw new Exception("Prescription expired on " + expiry + ".");
                if (used >= allowed) throw new Exception("Refill limit reached (" + used + "/" + allowed + ").");
            }

            log("✨ Validating drugs against prescription items ...");
            Set<String> validDrugs = new HashSet<>();
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT drug_name FROM prescription_item WHERE prescription_id = ?")) {
                ps.setInt(1, prescriptionId);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) validDrugs.add(rs.getString("drug_name").toLowerCase());
            }
            for (String[] item : items)
                if (!validDrugs.contains(item[0].toLowerCase()))
                    throw new Exception("Drug '" + item[0] + "' is NOT in this prescription. Blocked! 🚫");
            log("  All drugs verified ✓");

            log("💌 Creating pharmacy order ...");
            int orderId;
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO pharmacy_order (prescription_id, patient_id, pharmacy_id, order_time, status) VALUES (?, ?, ?, NOW(), 'PLACED')",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, prescriptionId); ps.setInt(2, patientId); ps.setInt(3, pharmacyId);
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys(); keys.next(); orderId = keys.getInt(1);
            }
            log("  pharmacy_order #" + orderId + " created ✓");

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO pharmacy_order_item (order_id, drug_name, quantity, dispensed_time) VALUES (?, ?, ?, NOW())")) {
                for (String[] item : items) {
                    ps.setInt(1, orderId); ps.setString(2, item[0]); ps.setInt(3, Integer.parseInt(item[1]));
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            log("  " + items.size() + " item(s) inserted ✓");

            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE prescription_rules SET refills_used = refills_used + 1 WHERE prescription_id = ?")) {
                ps.setInt(1, prescriptionId);
                int rows = ps.executeUpdate();

                if (rows == 0){
                    throw new Exception("Rate Limit Exceeded!");
                }
            }
            log("  Refill counter incremented ✓");
            conn.commit();
            log("\n🎀 Order #" + orderId + " placed and committed successfully! 🌸");

        } catch (Exception e) {
            log("\n💔 Error: " + e.getMessage());
            log("  → Rolling back transaction ...");
            if (conn != null) try { conn.rollback(); log("  → Rollback complete."); }
            catch (SQLException ex2) { log("  → Rollback failed: " + ex2.getMessage()); }
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); }
            catch (SQLException ignored) {}
        }
    }

    private void log(String msg) {
        SwingUtilities.invokeLater(() -> txtStatus.append(msg + "\n"));
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Pharmacy Order Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.add(new PharmacyOrderPanel());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
