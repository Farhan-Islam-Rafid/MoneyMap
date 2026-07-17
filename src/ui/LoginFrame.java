package src.ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import src.database.DBConnection;
import src.session.Session;
import src.ui.components.RoundedButton;

public class LoginFrame extends JFrame {

    public LoginFrame() {
        setTitle("MoneyMap v2.0 - Login");
        setSize(460, 560); // Slightly taller to fit the register link comfortably
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(new Color(245, 247, 250));
        contentPane.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Main Login Card
        JPanel loginCard = new JPanel();
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
        loginCard.setBackground(Color.WHITE);
        loginCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 228, 232), 1),
                new EmptyBorder(45, 40, 45, 40)));

        // Header
        JLabel titleLabel = new JLabel("MoneyMap");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(33, 37, 41));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Sign in to continue");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(108, 117, 125));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Form Fields
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(new Color(70, 75, 80));
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField userField = new JTextField();
        styleInputField(userField);

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passLabel.setForeground(new Color(70, 75, 80));
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPasswordField passField = new JPasswordField();
        styleInputField(passField);

        // Login Button
        RoundedButton loginBtn = new RoundedButton("Sign In", new Color(40, 167, 69), Color.WHITE);
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(320, 48));
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));

        // Link to Register Page
        JLabel registerLink = new JLabel("<html><u>Don't have an account? Register here</u></html>");
        registerLink.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        registerLink.setForeground(new Color(108, 117, 125));
        registerLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose(); // Close Login
                new RegisterFrame().setVisible(true); // Open Register
            }
        });

        // Database Authentication Action
        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter both username and password.",
                        "Input Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (Connection conn = DBConnection.getConnection();
                    PreparedStatement pstmt = conn
                            .prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {

                pstmt.setString(1, username);
                pstmt.setString(2, password); // Note: In a production app, use hashed passwords!

                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {

                    int userId = rs.getInt("id");

                    String loggedUsername = rs.getString("username");

                    // Save logged in user information
                    Session.setUser(userId, loggedUsername);

                    System.out.println(
                            "Login User ID : " + Session.userId);

                    dispose(); // Close login

                    new MainFrame().setVisible(true);

                } else {
                    JOptionPane.showMessageDialog(this,
                            "Invalid username or password.",
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Database connection error. Ensure MySQL is running.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Assemble the card
        loginCard.add(titleLabel);
        loginCard.add(Box.createRigidArea(new Dimension(0, 8)));
        loginCard.add(subtitleLabel);
        loginCard.add(Box.createRigidArea(new Dimension(0, 40)));

        loginCard.add(userLabel);
        loginCard.add(Box.createRigidArea(new Dimension(0, 8)));
        loginCard.add(userField);
        loginCard.add(Box.createRigidArea(new Dimension(0, 20)));

        loginCard.add(passLabel);
        loginCard.add(Box.createRigidArea(new Dimension(0, 8)));
        loginCard.add(passField);
        loginCard.add(Box.createRigidArea(new Dimension(0, 35)));

        loginCard.add(loginBtn);
        loginCard.add(Box.createRigidArea(new Dimension(0, 15)));
        loginCard.add(registerLink); // Added register link to the layout

        contentPane.add(loginCard, BorderLayout.CENTER);
        setContentPane(contentPane);
    }

    private void styleInputField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setMaximumSize(new Dimension(320, 46));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 210), 1),
                new EmptyBorder(10, 14, 10, 14)));
    }
}