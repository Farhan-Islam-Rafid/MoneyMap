package src.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import src.database.DBConnection;
import src.session.Session;

public class LoginFrame extends JFrame {

    public LoginFrame() {

        setTitle("MoneyMap v2.0 - Login");
        setSize(520, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        ImageIcon appIcon = loadIcon("/icons/moneymap.png");
        if (appIcon != null) {
            setIconImage(appIcon.getImage());
        }

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(new Color(245, 247, 250));
        contentPane.setBorder(new EmptyBorder(40, 40, 40, 40));

        // ---- Card with soft drop shadow ----
        JPanel shadowWrapper = new ShadowPanel();
        shadowWrapper.setLayout(new BorderLayout());
        shadowWrapper.setOpaque(false);

        JPanel loginCard = new JPanel();
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
        loginCard.setBackground(Color.WHITE);
        loginCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 232, 236), 1, true),
                new EmptyBorder(45, 40, 45, 40)));

        // ---- Logo, smoothed + rounded, with guaranteed fallback ----
        JLabel logoLabel = new JLabel();
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        if (appIcon != null) {
            logoLabel.setIcon(new ImageIcon(makeRoundedScaledImage(appIcon.getImage(), 72, 72, 16)));
        } else {
            // Fallback: draw a rounded placeholder logo so something always shows
            logoLabel.setIcon(new ImageIcon(makePlaceholderLogo(72, "M")));
        }

        JLabel titleLabel = new JLabel("MoneyMap");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(33, 37, 41));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Sign in to continue");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitleLabel.setForeground(new Color(120, 128, 136));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        userLabel.setForeground(new Color(70, 75, 80));
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField userField = new JTextField();
        userField.setAlignmentX(Component.CENTER_ALIGNMENT);
        styleInputField(userField);

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        passLabel.setForeground(new Color(70, 75, 80));
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPasswordField passField = new JPasswordField();
        passField.setAlignmentX(Component.CENTER_ALIGNMENT);
        styleInputField(passField);

        // ---- Guaranteed-visible rounded button (self-contained) ----
        JButton loginBtn = createRoundedButton("Sign In", new Color(40, 167, 69), Color.WHITE);
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(320, 48));
        loginBtn.setPreferredSize(new Dimension(320, 48));

        // ---- Register prompt + link (no hover effect) ----
        JLabel registerPrompt = new JLabel("Don't have an account?");
        registerPrompt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        registerPrompt.setForeground(new Color(108, 117, 125));
        registerPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel registerLink = new JLabel("Register here");
        registerLink.setFont(new Font("Segoe UI", Font.BOLD, 13));
        registerLink.setForeground(new Color(40, 167, 69));
        registerLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new RegisterFrame().setVisible(true);
            }
        });

        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter both username and password.",
                        "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(
                         "SELECT id, username FROM users WHERE username = ? AND password = ?")) {

                pstmt.setString(1, username);
                pstmt.setString(2, password);

                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    int userId = rs.getInt("id");
                    String loggedUsername = rs.getString("username");
                    Session.setUser(userId, loggedUsername);
                    System.out.println("Login User ID : " + Session.userId);
                    dispose();
                    SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Invalid username or password.",
                            "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Database connection error. Ensure PostgreSQL is running.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        loginCard.add(logoLabel);
        loginCard.add(Box.createRigidArea(new Dimension(0, 12)));
        loginCard.add(titleLabel);
        loginCard.add(Box.createRigidArea(new Dimension(0, 8)));
        loginCard.add(subtitleLabel);
        loginCard.add(Box.createRigidArea(new Dimension(0, 36)));

        loginCard.add(userLabel);
        loginCard.add(Box.createRigidArea(new Dimension(0, 8)));
        loginCard.add(userField);
        loginCard.add(Box.createRigidArea(new Dimension(0, 20)));

        loginCard.add(passLabel);
        loginCard.add(Box.createRigidArea(new Dimension(0, 8)));
        loginCard.add(passField);
        loginCard.add(Box.createRigidArea(new Dimension(0, 32)));

        loginCard.add(loginBtn);
        loginCard.add(Box.createRigidArea(new Dimension(0, 20)));
        loginCard.add(registerPrompt);
        loginCard.add(Box.createRigidArea(new Dimension(0, 4)));
        loginCard.add(registerLink);

        shadowWrapper.add(loginCard, BorderLayout.CENTER);
        contentPane.add(shadowWrapper, BorderLayout.CENTER);
        setContentPane(contentPane);
    }

    /** Rounded rect JButton that always paints correctly, with hover feedback. */
    private JButton createRoundedButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text) {
            private Color currentBg = bg;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? currentBg.darker()
                        : getModel().isRollover() ? currentBg.brighter() : currentBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setForeground(fg);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    /** Card with a soft drop shadow, drawn behind the white card. */
    private static class ShadowPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int shadowSize = 10;
            for (int i = 0; i < shadowSize; i++) {
                g2.setColor(new Color(0, 0, 0, 3));
                g2.fillRoundRect(i, i + 4, getWidth() - i * 2, getHeight() - i * 2, 20, 20);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private Image makeRoundedScaledImage(Image src, int w, int h, int radius) {
        Image scaled = src.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        java.awt.image.BufferedImage rounded =
                new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = rounded.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setClip(new RoundRectangle2D.Float(0, 0, w, h, radius, radius));
        g2.drawImage(scaled, 0, 0, null);
        g2.dispose();
        return rounded;
    }

    /** Draws a simple rounded-square logo with an initial letter, used when no image icon is found. */
    private Image makePlaceholderLogo(int size, String letter) {
        java.awt.image.BufferedImage img =
                new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(40, 167, 69));
        g2.fillRoundRect(0, 0, size, size, 16, 16);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, (int) (size * 0.5)));
        java.awt.FontMetrics fm = g2.getFontMetrics();
        int textX = (size - fm.stringWidth(letter)) / 2;
        int textY = (size - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(letter, textX, textY);
        g2.dispose();
        return img;
    }

    private ImageIcon loadIcon(String fileName) {
        java.net.URL url = getClass().getResource(fileName);
        if (url != null) {
            return new ImageIcon(url);
        }
        String relativePath = fileName.startsWith("/") ? fileName.substring(1) : fileName;
        java.io.File file = new java.io.File(relativePath);
        if (file.exists()) {
            return new ImageIcon(file.getAbsolutePath());
        }
        System.err.println("Warning: " + fileName + " not found, using placeholder logo instead.");
        return null;
    }

    private void styleInputField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setMaximumSize(new Dimension(320, 46));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(210, 214, 219), 1, true),
                new EmptyBorder(10, 14, 10, 14)));
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(40, 167, 69), 1, true),
                        new EmptyBorder(10, 14, 10, 14)));
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(210, 214, 219), 1, true),
                        new EmptyBorder(10, 14, 10, 14)));
            }
        });
    }
}