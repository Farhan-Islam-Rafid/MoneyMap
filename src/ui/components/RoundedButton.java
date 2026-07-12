package src.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoundedButton extends JButton {
    private Color hoverBackgroundColor;
    private Color normalBackgroundColor;

    public RoundedButton(String text, Color bg, Color fg) {
        super(text);
        this.normalBackgroundColor = bg;
        this.hoverBackgroundColor = bg.darker();
        setForeground(fg);
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                normalBackgroundColor = hoverBackgroundColor;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                normalBackgroundColor = bg;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(normalBackgroundColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        super.paintComponent(g);
        g2.dispose();
    }
}