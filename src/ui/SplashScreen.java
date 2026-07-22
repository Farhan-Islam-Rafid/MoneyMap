package src.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class SplashScreen extends JFrame {

    private JProgressBar progressBar;
    private JLabel statusLabel;

    public SplashScreen() {

        setSize(460, 260);
        setLocationRelativeTo(null);
        setUndecorated(true);

        // Outer wrapper gives the undecorated window a visible edge
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Color.WHITE);
        outer.setBorder(new LineBorder(new Color(225, 228, 232), 1));

        // Thin accent bar along the top for a branded touch
        JPanel accentBar = new JPanel();
        accentBar.setBackground(new Color(40, 167, 69));
        accentBar.setPreferredSize(new Dimension(0, 4));

        // Center content
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(new EmptyBorder(35, 40, 25, 40));

        JLabel title = new JLabel("MoneyMap", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(new Color(33, 37, 41));
        title.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JLabel version = new JLabel("v2.0", SwingConstants.CENTER);
        version.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        version.setForeground(new Color(150, 156, 163));
        version.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        statusLabel = new JLabel("Starting application...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setForeground(new Color(108, 117, 125));
        statusLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        progressBar = new RoundedProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setBorderPainted(false);
        progressBar.setMaximumSize(new Dimension(320, 6));
        progressBar.setPreferredSize(new Dimension(320, 6));
        progressBar.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        progressBar.setBackground(new Color(235, 237, 240));
        progressBar.setForeground(new Color(40, 167, 69));

        centerPanel.add(title);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        centerPanel.add(version);
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(progressBar);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        centerPanel.add(statusLabel);

        // Footer
        JLabel footer = new JLabel("© 2026 MoneyMap · Personal Finance Manager", SwingConstants.CENTER);
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footer.setForeground(new Color(180, 185, 190));
        footer.setBorder(new EmptyBorder(0, 0, 14, 0));

        outer.add(accentBar, BorderLayout.NORTH);
        outer.add(centerPanel, BorderLayout.CENTER);
        outer.add(footer, BorderLayout.SOUTH);

        add(outer);
    }

    public void setStatus(String text) {
        statusLabel.setText(text);
    }

    /** Progress bar with smooth rounded corners on both the track and the indicator. */
    private static class RoundedProgressBar extends JProgressBar {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int arc = h;

            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, w, h, arc, arc);

            if (isIndeterminate()) {
                int barWidth = w / 3;
                long time = System.currentTimeMillis() % 1200;
                int x = (int) ((time / 1200.0) * (w + barWidth)) - barWidth;
                g2.setColor(getForeground());
                g2.fillRoundRect(Math.max(0, x), 0, barWidth, h, arc, arc);
                repaint(30);
            } else {
                int fillWidth = (int) (w * (getPercentComplete()));
                g2.setColor(getForeground());
                g2.fillRoundRect(0, 0, fillWidth, h, arc, arc);
            }
            g2.dispose();
        }
    }
}