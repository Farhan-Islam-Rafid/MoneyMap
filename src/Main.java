import javax.swing.*;
import ui.MainFrame;

/**
 * Entry point for the MoneyMap application.
 */
public class Main {

    public static void main(String[] args) {
        // Use the operating system's native look and feel for a
        // cleaner, more modern appearance where available.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Not fatal - fall back to the default Swing look and feel.
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                // Most likely a database connection problem (MySQL not
                // running, wrong credentials, missing driver, etc).
                JOptionPane.showMessageDialog(
                        null,
                        "MoneyMap could not start.\n" +
                                "Please check that MySQL is running and that the " +
                                "connection settings in DBConnection.java are correct.\n\n" +
                                "Details: " + e.getMessage(),
                        "MoneyMap - Startup Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}
