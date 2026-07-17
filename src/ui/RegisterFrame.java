package src.ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import src.database.DBConnection;
import src.ui.components.RoundedButton;

public class RegisterFrame extends JFrame {

    public RegisterFrame() {
        setTitle("MoneyMap v2.0 - Register");
        setSize(460, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(new Color(245, 247, 250));
        contentPane.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Main Register Card
        JPanel registerCard = new JPanel();
        registerCard.setLayout(new BoxLayout(registerCard, BoxLayout.Y_AXIS));
        registerCard.setBackground(Color.WHITE);
        registerCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 228, 232), 1),
                new EmptyBorder(35, 40, 35, 40)));

        // Header
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titleLabel.setForeground(new Color(33, 37, 41));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Join MoneyMap today");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(108, 117, 125));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Form Fields
        JLabel nameLabel = new JLabel("Full Name");
        styleLabel(nameLabel);
        JTextField nameField = new JTextField();
        styleInputField(nameField);

        JLabel userLabel = new JLabel("Username");
        styleLabel(userLabel);
        JTextField userField = new JTextField();
        styleInputField(userField);

        JLabel passLabel = new JLabel("Password");
        styleLabel(passLabel);
        JPasswordField passField = new JPasswordField();
        styleInputField(passField);

        JLabel confirmPassLabel = new JLabel("Confirm Password");
        styleLabel(confirmPassLabel);
        JPasswordField confirmPassField = new JPasswordField();
        styleInputField(confirmPassField);

        // Register Button
        RoundedButton registerBtn = new RoundedButton("Register", new Color(0, 123, 255), Color.WHITE);
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.setMaximumSize(new Dimension(320, 48));
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));

        // DATABASE SAVE LOGIC ADDED HERE
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
                // Save user to MySQL database
                try (Connection conn = DBConnection.getConnection();
                        PreparedStatement pstmt = conn.prepareStatement(
                                "INSERT INTO users (full_name, username, password) VALUES (?, ?, ?)",
                                Statement.RETURN_GENERATED_KEYS)) {

                    pstmt.setString(1, name);
                    pstmt.setString(2, username);
                    pstmt.setString(3, password);

                    int affectedRows = pstmt.executeUpdate();

                    if (affectedRows > 0) {

                        // Get newly created user id
                        ResultSet rs = pstmt.getGeneratedKeys();

                        if (rs.next()) {

                            int userId = rs.getInt(1);

                            System.out.println(
                                    "New User ID : " + userId);

                        }

                        JOptionPane.showMessageDialog(this,
                                "Registration Successful! You can now log in.",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);

                        dispose();

                        new LoginFrame().setVisible(true);
                    }
                } catch (java.sql.SQLIntegrityConstraintViolationException ex) {
                    // Handle duplicate username (UNIQUE constraint violation)
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

        // Back to Login Link
        JLabel loginLink = new JLabel("<html><u>Already have an account? Sign in</u></html>");
        loginLink.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        loginLink.setForeground(new Color(108, 117, 125));
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
        registerCard.add(titleLabel);
        registerCard.add(Box.createRigidArea(new Dimension(0, 6)));
        registerCard.add(subtitleLabel);
        registerCard.add(Box.createRigidArea(new Dimension(0, 25)));

        registerCard.add(nameLabel);
        registerCard.add(Box.createRigidArea(new Dimension(0, 6)));
        registerCard.add(nameField);
        registerCard.add(Box.createRigidArea(new Dimension(0, 15)));

        registerCard.add(userLabel);
        registerCard.add(Box.createRigidArea(new Dimension(0, 6)));
        registerCard.add(userField);
        registerCard.add(Box.createRigidArea(new Dimension(0, 15)));

        registerCard.add(passLabel);
        registerCard.add(Box.createRigidArea(new Dimension(0, 6)));
        registerCard.add(passField);
        registerCard.add(Box.createRigidArea(new Dimension(0, 15)));

        registerCard.add(confirmPassLabel);
        registerCard.add(Box.createRigidArea(new Dimension(0, 6)));
        registerCard.add(confirmPassField);
        registerCard.add(Box.createRigidArea(new Dimension(0, 30)));

        registerCard.add(registerBtn);
        registerCard.add(Box.createRigidArea(new Dimension(0, 15)));
        registerCard.add(loginLink);

        contentPane.add(registerCard, BorderLayout.CENTER);
        setContentPane(contentPane);
    }

    private void styleLabel(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(70, 75, 80));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private void styleInputField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setMaximumSize(new Dimension(320, 46));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 210), 1),
                new EmptyBorder(10, 14, 10, 14)));
    }
}