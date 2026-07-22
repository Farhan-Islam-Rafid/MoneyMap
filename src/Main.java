package src;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import src.database.DatabaseInitializer;
import src.ui.LoginFrame;
import src.ui.SplashScreen;

public class Main {

    public static void main(String[] args) {

        // Modern Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set Look and Feel.");
        }

        SwingUtilities.invokeLater(() -> {

            SplashScreen splash = new SplashScreen();
            splash.setVisible(true);

            // Database loading in background
            new Thread(() -> {
                try {
                    updateStatus(splash, "Connecting database...");
                    Thread.sleep(800);

                    updateStatus(splash, "Initializing database...");
                    DatabaseInitializer.initialize();
                    Thread.sleep(800);

                    updateStatus(splash, "Loading MoneyMap...");
                    Thread.sleep(800);

                    SwingUtilities.invokeLater(() -> {
                        splash.dispose();
                        new LoginFrame().setVisible(true);
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    SwingUtilities.invokeLater(() -> {
                        splash.dispose();
                        JOptionPane.showMessageDialog(null,
                                "Failed to start MoneyMap: " + e.getMessage()
                                + "\n\nPlease check your database connection and try again.",
                                "Startup Error",
                                JOptionPane.ERROR_MESSAGE);
                    });
                }
            }, "startup-thread").start();

        });
    }

    /**
     * Safely updates the splash status label on the EDT from a background
     * thread.
     */
    private static void updateStatus(SplashScreen splash, String text) {
        SwingUtilities.invokeLater(() -> splash.setStatus(text));
    }
}
