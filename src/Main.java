import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class Main extends JFrame {

    private static final Color CONTENT_BG = new Color(255, 245, 250);

    private JPanel contentArea;

    public Main() {
        super("CareVault — " + LoginScreen.loggedInEmail +
                "  [" + LoginScreen.loggedInRole + "]");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(CONTENT_BG);

        Sidebar sidebar = new Sidebar(
                LoginScreen.loggedInRole,
                LoginScreen.loggedInEmail,
                this::loadPanel
        );

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.add(sidebar,      BorderLayout.WEST);
        root.add(contentArea,  BorderLayout.CENTER);
        setContentPane(root);

        loadPanel("DASHBOARD");
    }

    private void loadPanel(String panelKey) {
        contentArea.removeAll();

        JPanel panel = switch (panelKey) {
            case "PHARMACY"  -> new PharmacyOrderPanel();
            case "DASHBOARD" -> new DashboardPanel();
            default          -> buildPlaceholder(panelKey);
        };

        contentArea.add(panel, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    private JPanel buildPlaceholder(String panelKey) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(CONTENT_BG);

        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 200));
                g2.fillRoundRect(0, 0, getWidth()-4, getHeight()-4, 20, 20);
                g2.setColor(new Color(255, 218, 235));
                g2.setStroke(new java.awt.BasicStroke(1.5f));
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
        title.setForeground(new Color(80, 30, 60));
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