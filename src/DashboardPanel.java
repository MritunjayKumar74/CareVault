import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class DashboardPanel extends JPanel {

    private static final Color BG           = new Color(255, 245, 250);
    private static final Color PINK_HOT     = new Color(255, 105, 180);
    private static final Color LILAC        = new Color(200, 162, 255);
    private static final Color TEXT_DARK    = new Color(80,  30,  60);
    private static final Color TEXT_MID     = new Color(180, 80, 130);

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(BG);
        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(),   BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, new Color(255,182,213),
                        getWidth(), 0, new Color(200,162,255)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        p.setPreferredSize(new Dimension(0, 60));
        p.setLayout(new FlowLayout(FlowLayout.LEFT, 24, 16));

        JLabel lbl = new JLabel("🏠  Welcome to CareVault,  " +
                LoginScreen.loggedInEmail + "!");
        lbl.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        lbl.setForeground(new Color(80, 30, 60));
        p.add(lbl);
        return p;
    }

    private JPanel buildBody() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(BG);

        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth()-4, getHeight()-4, 20, 20);
                g2.setColor(new Color(255, 218, 235));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth()-5, getHeight()-5, 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(40, 60, 40, 60));

        JLabel emoji = new JLabel("🌸", SwingConstants.CENTER);
        emoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 52));
        emoji.setAlignmentX(CENTER_ALIGNMENT);

        JLabel title = new JLabel("Dashboard Coming Soon!", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI Emoji", Font.BOLD, 22));
        title.setForeground(TEXT_DARK);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Stats, recent activity and quick actions will appear here.",
                SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        sub.setForeground(TEXT_MID);
        sub.setAlignmentX(CENTER_ALIGNMENT);

        card.add(emoji);
        card.add(Box.createVerticalStrut(12));
        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(sub);

        p.add(card);
        return p;
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new DashboardPanel().setVisible(true));
    }
}