import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.*;

public class LoginScreen extends JFrame {

    // ── Palette ──────────────────────────────────────────────────────────────
    static final Color BG_LEFT       = new Color(255, 192, 220);
    static final Color BG_LEFT2      = new Color(230, 160, 255);
    static final Color BG_RIGHT      = new Color(255, 245, 250);
    static final Color PINK_HOT      = new Color(255, 105, 180);
    static final Color PINK_SOFT     = new Color(255, 182, 213);
    static final Color PINK_PALE     = new Color(255, 218, 235);
    static final Color LILAC         = new Color(200, 162, 255);
    static final Color LILAC_SOFT    = new Color(230, 210, 255);
    static final Color TEXT_DARK     = new Color(80,  30,  60);
    static final Color TEXT_MID      = new Color(180, 80, 130);
    static final Color TEXT_MUTED    = new Color(200, 150, 175);
    static final Color MINT          = new Color(100, 220, 170);
    static final Color ERROR_RED     = new Color(255, 80, 100);
    static final Color WHITE_GLASS   = new Color(255, 255, 255, 200);
    static final Color WHITE_GLASS2  = new Color(255, 255, 255, 100);

    // ── Fonts ─────────────────────────────────────────────────────────────────
    static final Font F_BRAND    = new Font("Segoe UI Emoji", Font.BOLD,  32);
    static final Font F_TAGLINE  = new Font("Segoe UI Emoji", Font.ITALIC, 13);
    static final Font F_LABEL    = new Font("Segoe UI Emoji", Font.BOLD,  11);
    static final Font F_INPUT    = new Font("Segoe UI Emoji", Font.PLAIN, 13);
    static final Font F_BTN      = new Font("Segoe UI Emoji", Font.BOLD,  14);
    static final Font F_SMALL    = new Font("Segoe UI Emoji", Font.PLAIN, 11);
    static final Font F_DECO     = new Font("Segoe UI Emoji", Font.BOLD,  28);

    // ── Fields ────────────────────────────────────────────────────────────────
    private PillField txtEmail    = new PillField("your@email.com", false);
    private PillField txtPassword = new PillField("password", true);
    private JLabel    lblError    = new JLabel(" ");
    private JButton   btnLogin;

    // ── Session (set after login) ─────────────────────────────────────────────
    public static int    loggedInUserId   = -1;
    public static String loggedInRole     = "";
    public static String loggedInEmail    = "";
    private static java.util.Map<String, Integer> failedAttemptsMap = new java.util.HashMap<>();
    private static java.util.Map<String, Long> lockoutEndTimeMap = new java.util.HashMap<>();

    public LoginScreen() {
        super("CareVault — Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new GridLayout(1, 2, 0, 0));
        root.add(buildLeftPanel());
        root.add(buildRightPanel());
        setContentPane(root);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // LEFT PANEL — decorative branding side
    // ═══════════════════════════════════════════════════════════════════════════
    private JPanel buildLeftPanel() {
        JPanel p = new JPanel(null) {  // null layout for free positioning
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Main gradient: pink → lilac
                GradientPaint gp = new GradientPaint(
                        0, 0, BG_LEFT, getWidth(), getHeight(), BG_LEFT2);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Large decorative blobs
                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillOval(-40, -40, 220, 220);
                g2.fillOval(getWidth() - 100, getHeight() - 120, 200, 200);

                g2.setColor(new Color(255, 255, 255, 25));
                g2.fillOval(50, getHeight() - 160, 160, 160);
                g2.fillOval(getWidth() - 60, 30, 120, 120);

                // Floating circles (decorative)
                drawFloatingCircle(g2, 60,  80,  18, new Color(255,255,255,80));
                drawFloatingCircle(g2, 310, 50,  10, new Color(255,255,255,100));
                drawFloatingCircle(g2, 280, 350, 14, new Color(255,255,255,70));
                drawFloatingCircle(g2, 30,  290, 22, new Color(255,255,255,60));
                drawFloatingCircle(g2, 180, 420, 8,  new Color(255,255,255,90));

                // Sparkle dots
                g2.setColor(new Color(255,255,255,150));
                int[][] sparks = {{90,140},{200,80},{320,200},{40,380},{260,460},{150,300},{310,130}};
                for (int[] s : sparks) {
                    g2.fillOval(s[0]-2, s[1]-2, 5, 5);
                    g2.fillOval(s[0]-1, s[1]-8, 2, 6);
                    g2.fillOval(s[0]-8, s[1]-1, 6, 2);
                }
            }
            private void drawFloatingCircle(Graphics2D g2, int x, int y, int r, Color c) {
                g2.setColor(c);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(x, y, r*2, r*2);
            }
        };
        p.setPreferredSize(new Dimension(430, 540));

        // ── Logo card (glass morphism) ──
        JPanel logoCard = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(WHITE_GLASS);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.setColor(new Color(255,255,255,180));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 24, 24);
                g2.dispose();
            }
        };
        logoCard.setOpaque(false);
        logoCard.setBorder(new EmptyBorder(20, 32, 20, 32));
        logoCard.setLayout(new BoxLayout(logoCard, BoxLayout.Y_AXIS));
        logoCard.setAlignmentY(CENTER_ALIGNMENT);



        JLabel iconLbl = new JLabel("🏥", SwingConstants.CENTER);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 52));

        JLabel brandLbl = new JLabel("CareVault", SwingConstants.CENTER);
        brandLbl.setFont(F_BRAND);
        brandLbl.setForeground(Color.BLACK);

        JLabel taglineLbl = new JLabel("by Mrisuvas  ", SwingConstants.CENTER);
        taglineLbl.setFont(F_TAGLINE);
        taglineLbl.setForeground(TEXT_MID);

        iconLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        brandLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        taglineLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Change the assembly order inside logoCard to:
        logoCard.add(Box.createVerticalGlue());   // ← pushes content to middle
        logoCard.add(iconLbl);
        logoCard.add(Box.createVerticalStrut(8));
        logoCard.add(brandLbl);
        logoCard.add(Box.createVerticalStrut(4));
        logoCard.add(taglineLbl);
        logoCard.add(Box.createVerticalGlue());   // ← pushes content to middle

        logoCard.setBounds(40, 30, 320, 320);
        p.add(logoCard);

        // ── Feature pills at bottom ──
        String[][] features = {
                {"💊", "e-Prescriptions"},
                {"🧪", "Lab Reports"},
                {"🏦", "Insurance Claims"},
                {"🔐", "Secure & Auditable"}
        };
        int py = 380;
        for (String[] feat : features) {
            JPanel pill = buildFeaturePill(feat[0], feat[1]);
            pill.setBounds(45, py, 310, 32);   // ← fixed x = 40 for all
            p.add(pill);
            py += 36;
        }

        return p;
    }

    private JPanel buildFeaturePill(String emoji, String text) {
        JPanel pill = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(WHITE_GLASS2);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        pill.setOpaque(false);

        JLabel eLbl = new JLabel(emoji);
        eLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        eLbl.setPreferredSize(new Dimension(20, 36));
        eLbl.setVerticalAlignment(SwingConstants.CENTER);

        JLabel tLbl = new JLabel(text);
        tLbl.setFont(new Font("Segoe UI Emoji", Font.BOLD, 11));
        tLbl.setForeground(TEXT_DARK);
        tLbl.setPreferredSize(new Dimension(200, 36));
        tLbl.setVerticalAlignment(SwingConstants.CENTER);

        pill.add(eLbl);
        pill.add(tLbl);
        return pill;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // RIGHT PANEL — login form
    // ═══════════════════════════════════════════════════════════════════════════
    private JPanel buildRightPanel() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(BG_RIGHT);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // subtle top-right blob
                g2.setColor(new Color(255, 210, 230, 60));
                g2.fillOval(getWidth() - 120, -40, 180, 180);
                g2.setColor(new Color(210, 180, 255, 40));
                g2.fillOval(-30, getHeight() - 100, 150, 150);
            }
        };
        p.setLayout(new GridBagLayout());

        JPanel form = buildFormCard();
        p.add(form);
        return p;
    }

    private JPanel buildFormCard() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // drop shadow
                g2.setColor(new Color(255, 182, 213, 60));
                g2.fillRoundRect(4, 4, getWidth()-4, getHeight()-4, 24, 24);
                // white card
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth()-4, getHeight()-4, 24, 24);
                // pink border
                g2.setColor(PINK_PALE);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth()-5, getHeight()-5, 24, 24);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(36, 40, 36, 40));
        card.setPreferredSize(new Dimension(340, 420));

        // ── Welcome text ──
        JLabel welcome = new JLabel("Welcome back!");
        welcome.setFont(new Font("Segoe UI Emoji", Font.BOLD, 24));
        welcome.setForeground(TEXT_DARK);
        welcome.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Sign in to your CareVault account");
        sub.setFont(F_SMALL);
        sub.setForeground(TEXT_MUTED);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        // ── Divider ──
        JSeparator div = makePinkDivider();

        // ── Email field ──
        JLabel emailLbl = makeLabel("Email Address");
        txtEmail.setAlignmentX(LEFT_ALIGNMENT);
        txtEmail.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        // ── Password field ──
        JLabel passLbl = makeLabel("Password");
        txtPassword.setAlignmentX(LEFT_ALIGNMENT);
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        // ── Error label ──
        lblError.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 11));
        lblError.setForeground(ERROR_RED);
        lblError.setAlignmentX(LEFT_ALIGNMENT);

        // ── Login button ──
        btnLogin = buildLoginButton();
        btnLogin.setAlignmentX(LEFT_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        // ── Role hint ──
        JLabel hint = new JLabel("Roles: PATIENT  DOCTOR  LAB  PHARMACY  TPA  ADMIN");
        hint.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 10));
        hint.setForeground(TEXT_MUTED);
        hint.setAlignmentX(LEFT_ALIGNMENT);

        // ── Assemble ──
        card.add(welcome);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(16));
        card.add(div);
        card.add(Box.createVerticalStrut(20));
        card.add(emailLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(txtEmail);
        card.add(Box.createVerticalStrut(14));
        card.add(passLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(txtPassword);
        card.add(Box.createVerticalStrut(8));
        card.add(lblError);
        card.add(Box.createVerticalStrut(6));
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(16));
        card.add(hint);

        // Enter key triggers login
        KeyAdapter enterKey = new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) attemptLogin();
            }
        };
        txtEmail.addKeyListener(enterKey);
        txtPassword.addKeyListener(enterKey);

        return card;
    }

    private JButton buildLoginButton() {
        JButton btn = new JButton("Sign In to CareVault") {
            private boolean hovered = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c1 = hovered ? new Color(255, 80, 160)  : PINK_HOT;
                Color c2 = hovered ? new Color(180, 80, 255)  : LILAC;
                g2.setPaint(new GradientPaint(0, 0, c1, getWidth(), 0, c2));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                // shine overlay
                g2.setColor(new Color(255,255,255,30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight()/2, 25, 25);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(F_BTN);
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> attemptLogin());
        return btn;
    }

    private void startLockoutCountdown(String email) {
        Timer countdown = new Timer(1000, null);
        countdown.addActionListener(e -> {
            long endTime  = lockoutEndTimeMap.getOrDefault(email, -1L);
            long remaining = endTime - System.currentTimeMillis();
            if (remaining <= 0) {
                countdown.stop();
                lockoutEndTimeMap.remove(email);
                failedAttemptsMap.remove(email);
                SwingUtilities.invokeLater(() -> {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Sign In to CareVault");
                    lblError.setText(" ");
                });
            } else {
                long secsLeft = remaining / 1000;
                long minsLeft = secsLeft / 60;
                long secs     = secsLeft % 60;
                SwingUtilities.invokeLater(() -> {
                    btnLogin.setText(String.format("Locked — %d:%02d remaining", minsLeft, secs));
                    showError(String.format("Too many attempts. Try again in %d:%02d", minsLeft, secs));
                });
            }
        });
        countdown.start();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // LOGIN LOGIC
    // ═══════════════════════════════════════════════════════════════════════════
    private void attemptLogin() {
        String email    = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();

        lblError.setText(" ");

        // ── Per-email lockout check ──
        long lockoutEnd = lockoutEndTimeMap.getOrDefault(email, -1L);
        if (lockoutEnd != -1) {
            long remaining = lockoutEnd - System.currentTimeMillis();
            if (remaining > 0) {
                long secsLeft = remaining / 1000;
                long minsLeft = secsLeft / 60;
                long secs     = secsLeft % 60;
                showError(String.format("Locked out. Try again in %d:%02d", minsLeft, secs));
                btnLogin.setEnabled(false);
                btnLogin.setText(String.format("Locked — %d:%02d remaining", minsLeft, secs));
                return;
            } else {
                // Lockout expired — reset this email
                lockoutEndTimeMap.remove(email);
                failedAttemptsMap.remove(email);
                btnLogin.setEnabled(true);
                btnLogin.setText("Sign In to CareVault");
            }
        }

        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter both email and password.");
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Signing in...");

        SwingWorker<String[], Void> worker = new SwingWorker<>() {
            @Override protected String[] doInBackground() {
                return authenticateUser(email, password);
            }
            @Override protected void done() {
                try {
                    String[] result = get();
                    if (result == null) {
                        int attempts = failedAttemptsMap.getOrDefault(email, 0) + 1;
                        failedAttemptsMap.put(email, attempts);
                        int attemptsLeft = 5 - attempts;

                        if (attempts >= 5) {
                            // ── Start 5 min lockout for THIS email only ──
                            long endTime = System.currentTimeMillis() + (2 * 60 * 1000);
                            lockoutEndTimeMap.put(email, endTime);
                            btnLogin.setEnabled(false);
                            startLockoutCountdown(email);
                            JOptionPane.showMessageDialog(
                                    LoginScreen.this,
                                    "5 failed attempts for " + email + "!\nAccount locked for 5 minutes.",
                                    "Account Locked",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        } else {
                            showError("Wrong password. " + attemptsLeft + " attempt(s) remaining.");
                            btnLogin.setEnabled(true);
                            btnLogin.setText("Sign In to CareVault");
                        }
                    } else {
                        // Success — reset only this email's counter
                        failedAttemptsMap.remove(email);
                        lockoutEndTimeMap.remove(email);
                        loggedInUserId = Integer.parseInt(result[0]);
                        loggedInRole   = result[1];
                        loggedInEmail  = result[2];
                        onLoginSuccess();
                    }
                } catch (Exception ex) {
                    showError("Connection error: " + ex.getMessage());
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Sign In to CareVault");
                }
            }
        };
        worker.execute();
    }

    private String[] authenticateUser(String email, String password) {
        // NOTE: In production use bcrypt. For this project we compare directly
        // since your DB stores password_hash as plain or hashed text.
        String sql = """
            SELECT user_id, role, email
            FROM   user
            WHERE  email = ?
              AND  password_hash = ?
              AND  status = 'ACTIVE'
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);   // replace with hash comparison if needed
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new String[]{
                        String.valueOf(rs.getInt("user_id")),
                        rs.getString("role"),
                        rs.getString("email")
                };
            }
        } catch (SQLException e) {
            SwingUtilities.invokeLater(() -> showError("DB Error: " + e.getMessage()));
        }
        return null;
    }

    private void onLoginSuccess() {
        // Show brief success state then open main app
        btnLogin.setText("Welcome! Opening CareVault...");

        Timer t = new Timer(800, e -> {
            dispose();  // close login window
            SwingUtilities.invokeLater(() -> {
                Main mainFrame = new Main();
                mainFrame.setVisible(true);
            });
        });
        t.setRepeats(false);
        t.start();
    }

    private void showError(String msg) {
        lblError.setText("  " + msg);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // HELPERS
    // ═══════════════════════════════════════════════════════════════════════════
    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(F_LABEL);
        lbl.setForeground(TEXT_MID);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    private JSeparator makePinkDivider() {
        JSeparator sep = new JSeparator() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, PINK_SOFT, getWidth(), 0, LILAC_SOFT);
                g2.setPaint(gp);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(0, 0, getWidth(), 0);
                g2.dispose();
            }
        };
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        sep.setAlignmentX(LEFT_ALIGNMENT);
        return sep;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PILL FIELD — inner class
    // ═══════════════════════════════════════════════════════════════════════════
    static class PillField extends JTextField {
        private final String placeholder;
        private final boolean isPassword;
        private char[] passwordChars;

        PillField(String placeholder, boolean isPassword) {
            this.placeholder = placeholder;
            this.isPassword  = isPassword;
            if (isPassword) {
                passwordChars = new char[0];
                // We use a custom password approach to keep consistent styling
                setDocument(new javax.swing.text.PlainDocument());
            }
            setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
            setForeground(TEXT_DARK);
            setBackground(new Color(255, 248, 252));
            setCaretColor(PINK_HOT);
            setOpaque(false);
            setBorder(new CompoundBorder(
                    new RoundedBorder2(22, PINK_SOFT),
                    new EmptyBorder(8, 16, 8, 16)));

            addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    setBorder(new CompoundBorder(new RoundedBorder2(22, PINK_HOT), new EmptyBorder(8, 16, 8, 16)));
                    repaint();
                }
                public void focusLost(FocusEvent e) {
                    setBorder(new CompoundBorder(new RoundedBorder2(22, PINK_SOFT), new EmptyBorder(8, 16, 8, 16)));
                    repaint();
                }
            });

            if (isPassword) {
                addKeyListener(new KeyAdapter() {
                    @Override public void keyTyped(KeyEvent e) {
                        // handled by default document; we just mask display
                    }
                });
            }
        }

        @Override public String getText() {
            if (isPassword) {
                // Return actual text but display dots
                return super.getText();
            }
            return super.getText();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(255, 248, 252));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
            g2.dispose();

            if (isPassword) {
                // Draw dots for password
                String actual = super.getText();
                if (!actual.isEmpty()) {
                    String dots = "●".repeat(actual.length());
                    Graphics2D g3 = (Graphics2D) g.create();
                    g3.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 10));
                    g3.setColor(TEXT_DARK);
                    Insets ins = getInsets();
                    g3.drawString(dots, ins.left, getHeight() / 2 + 4);
                    g3.dispose();
                    return;
                }
            }

            super.paintComponent(g);

            // Placeholder
            if (getText().isEmpty() && !isFocusOwner()) {
                Graphics2D g3 = (Graphics2D) g.create();
                g3.setFont(new Font("Segoe UI Emoji", Font.ITALIC, 12));
                g3.setColor(TEXT_MUTED);
                Insets ins = getInsets();
                g3.drawString(placeholder, ins.left, getHeight() / 2 + 5);
                g3.dispose();
            }
        }
    }

    // ── Rounded border helper ─────────────────────────────────────────────────
    static class RoundedBorder2 extends AbstractBorder {
        private final int r; private final Color c;
        RoundedBorder2(int r, Color c) { this.r = r; this.c = c; }
        @Override public void paintBorder(Component comp, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c); g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(x, y, w-1, h-1, r, r);
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(5,5,5,5); }
    }

    // ── Entry point (for standalone test) ─────────────────────────────────────
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }
}