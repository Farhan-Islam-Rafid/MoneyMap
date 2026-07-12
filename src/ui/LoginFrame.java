package src.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import src.ui.components.RoundedButton;

public class LoginFrame extends JFrame {

    public LoginFrame() {
        setTitle("MoneyMap v2.0 - Login");
        setSize(460, 520);
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

        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();

            if (username.equals("rafid") && password.equals("1234")) {
                dispose(); // Close login
                new MainFrame().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid username or password.",
                        "Login Failed",
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

    // Optional: Add a small footer or branding
    // You can extend this later if needed
}