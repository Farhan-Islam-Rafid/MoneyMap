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
import java.sql.Statement;

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
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import src.database.DBConnection;

public class RegisterFrame extends JFrame {

    public RegisterFrame() {
        setTitle("MoneyMap v2.0 - Register");
        setSize(520, 760);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // App / taskbar icon
        ImageIcon appIcon = loadIcon("/icons/moneymap.png");
        if (appIcon != null) {
            setIconImage(appIcon.getImage());
        }

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(new Color(245, 247, 250));
        contentPane.setBorder(new EmptyBorder(30, 40, 30, 40));

        // ---- Card with soft drop shadow ----
        ShadowPanel shadowWrapper = new ShadowPanel();
        shadowWrapper.setLayout(new BorderLayout());
        shadowWrapper.setOpaque(false);

        JPanel registerCard = new JPanel();
        registerCard.setLayout(new BoxLayout(registerCard, BoxLayout.Y_AXIS));
        registerCard.setBackground(Color.WHITE);
        registerCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 232, 236), 1, true),
                new EmptyBorder(35, 40, 35, 40)));

        // ---- Logo, smoothed + rounded, with guaranteed fallback ----
        JLabel logoLabel = new JLabel();
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        if (appIcon != null) {
            logoLabel.setIcon(new ImageIcon(makeRoundedScaledImage(appIcon.getImage(), 64, 64, 14)));
        } else {
            logoLabel.setIcon(new ImageIcon(makePlaceholderLogo(64, "M")));
        }

        // Header
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titleLabel.setForeground(new Color(33, 37, 41));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Join MoneyMap today");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitleLabel.setForeground(new Color(120, 128, 136));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Form Fields
        JLabel nameLabel = new JLabel("Full Name");
        styleLabel(nameLabel);
        JTextField nameField = new JTextField();
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        styleInputField(nameField);

        JLabel userLabel = new JLabel("Username");
        styleLabel(userLabel);
        JTextField userField = new JTextField();
        userField.setAlignmentX(Component.CENTER_ALIGNMENT);
        styleInputField(userField);

        JLabel passLabel = new JLabel("Password");
        styleLabel(passLabel);
        JPasswordField passField = new JPasswordField();
        passField.setAlignmentX(Component.CENTER_ALIGNMENT);
        styleInputField(passField);

        JLabel confirmPassLabel = new JLabel("Confirm Password");
        styleLabel(confirmPassLabel);
        JPasswordField confirmPassField = new JPasswordField();
        confirmPassField.setAlignmentX(Component.CENTER_ALIGNMENT);
        styleInputField(confirmPassField);

        // ---- Guaranteed-visible rounded button ----
        JButton registerBtn = createRoundedButton("Register", new Color(0, 123, 255), Color.WHITE);
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.setMaximumSize(new Dimension(320, 48));
        registerBtn.setPreferredSize(new Dimension(320, 48));

        // DATABASE SAVE LOGIC
        registerBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            String confirmPass = new String(confirmPassField.getPassword()).trim();

            if (name.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please fill in all fields.",
                        "Registration Failed",
                        JOptionPane.ERROR_MESSAGE);
            } else if (!password.equals(confirmPass)) {
                JOptionPane.showMessageDialog(this,
                        "Passwords do not match.",
                        "Registration Failed",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                try (Connection conn = DBConnection.getConnection();
                        PreparedStatement pstmt = conn.prepareStatement(
                                "INSERT INTO users (full_name, username, password) VALUES (?, ?, ?)",
                                Statement.RETURN_GENERATED_KEYS)) {

                    pstmt.setString(1, name);
                    pstmt.setString(2, username);
                    pstmt.setString(3, password);

                    int affectedRows = pstmt.executeUpdate();

                    if (affectedRows > 0) {
                        ResultSet rs = pstmt.getGeneratedKeys();
                        if (rs.next()) {
                            int userId = rs.getInt(1);
                            System.out.println("New User ID : " + userId);
                        }

                        JOptionPane.showMessageDialog(this,
                                "Registration Successful! You can now log in.",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);

                        dispose();
                        new LoginFrame().setVisible(true);
                    }
                } catch (java.sql.SQLIntegrityConstraintViolationException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Username already exists! Please choose another one.",
                            "Registration Failed",
                            JOptionPane.WARNING_MESSAGE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                            "Database Error: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Back to Login link (no hover, no underline)
        JLabel loginPrompt = new JLabel("Already have an account?");
        loginPrompt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        loginPrompt.setForeground(new Color(108, 117, 125));
        loginPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel loginLink = new JLabel("Sign in");
        loginLink.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginLink.setForeground(new Color(0, 123, 255));
        loginLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });

        // Assemble the card
        registerCard.add(logoLabel);
        registerCard.add(Box.createRigidArea(new Dimension(0, 12)));
        registerCard.add(titleLabel);
        registerCard.add(Box.createRigidArea(new Dimension(0, 6)));
        registerCard.add(subtitleLabel);
        registerCard.add(Box.createRigidArea(new Dimension(0, 28)));

        registerCard.add(nameLabel);
        registerCard.add(Box.createRigidArea(new Dimension(0, 6)));
        registerCard.add(nameField);
        registerCard.add(Box.createRigidArea(new Dimension(0, 16)));

        registerCard.add(userLabel);
        registerCard.add(Box.createRigidArea(new Dimension(0, 6)));
        registerCard.add(userField);
        registerCard.add(Box.createRigidArea(new Dimension(0, 16)));

        registerCard.add(passLabel);
        registerCard.add(Box.createRigidArea(new Dimension(0, 6)));
        registerCard.add(passField);
        registerCard.add(Box.createRigidArea(new Dimension(0, 16)));

        registerCard.add(confirmPassLabel);
        registerCard.add(Box.createRigidArea(new Dimension(0, 6)));
        registerCard.add(confirmPassField);
        registerCard.add(Box.createRigidArea(new Dimension(0, 32)));

        registerCard.add(registerBtn);
        registerCard.add(Box.createRigidArea(new Dimension(0, 20)));
        registerCard.add(loginPrompt);
        registerCard.add(Box.createRigidArea(new Dimension(0, 4)));
        registerCard.add(loginLink);

        shadowWrapper.add(registerCard, BorderLayout.CENTER);
        contentPane.add(shadowWrapper, BorderLayout.CENTER);
        setContentPane(contentPane);
    }

    /** Rounded rect JButton that always paints correctly, with hover feedback. */
    private JButton createRoundedButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bg.darker()
                        : getModel().isRollover() ? bg.brighter() : bg);
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

    /** Scales an image and clips it to rounded corners. */
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
        g2.setColor(new Color(0, 123, 255));
        g2.fillRoundRect(0, 0, size, size, 14, 14);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, (int) (size * 0.5)));
        java.awt.FontMetrics fm = g2.getFontMetrics();
        int textX = (size - fm.stringWidth(letter)) / 2;
        int textY = (size - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(letter, textX, textY);
        g2.dispose();
        return img;
    }

    /**
     * Loads an icon from the classpath, falling back to a relative filesystem
     * path if it isn't found there. Returns null (and skips the icon) if
     * neither location has the file, so a missing image never crashes the UI.
     */
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

    /** Card wrapper with a soft drop shadow, drawn behind the white card. */
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

    private void styleLabel(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(70, 75, 80));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
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
                        new LineBorder(new Color(0, 123, 255), 1, true),
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