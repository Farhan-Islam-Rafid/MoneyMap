package ui;

import javax.swing.*;
import java.awt.*;

/**
 * A JButton subclass that paints itself with rounded corners.
 * Used to give MoneyMap's Add button a modern, polished feel
 * without pulling in any external UI library.
 */
public class RoundedButton extends JButton {

    private static final int ARC = 18;

    public RoundedButton(String text) {
        super(text);
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setForeground(Color.WHITE);
        setBackground(new Color(41, 98, 255)); // primary blue
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color fill = getModel().isPressed()
                ? getBackground().darker()
                : getModel().isRollover()
                    ? getBackground().brighter()
                    : getBackground();

        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC, ARC);
        g2.dispose();

        super.paintComponent(g);
    }
}
