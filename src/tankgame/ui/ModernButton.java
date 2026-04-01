package tankgame.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 现代化按钮组件
 */
public class ModernButton extends JButton {
    private Color baseColor;
    private boolean isHover = false;
    private int fontSize;

    public ModernButton(String text, Color baseColor, int fontSize) {
        super(text);
        this.baseColor = baseColor;
        this.fontSize = fontSize;
        setupButton();
    }

    private void setupButton() {
        setFont(new Font("STXingkai", Font.BOLD, fontSize));
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(280, 70));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHover = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHover = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // 根据状态选择颜色
        Color startColor, endColor;
        if (getModel().isPressed()) {
            startColor = baseColor.darker();
            endColor = baseColor.darker().darker();
        } else if (isHover) {
            startColor = new Color(135, 206, 250);
            endColor = baseColor;
        } else {
            startColor = baseColor;
            endColor = baseColor.darker();
        }

        // 渐变填充
        GradientPaint gradient = new GradientPaint(0, 0, startColor, 0, height, endColor);
        g2d.setPaint(gradient);
        g2d.fillRoundRect(0, 0, width, height, 20, 20);

        // 边框光效
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.drawRoundRect(0, 0, width - 1, height - 1, 20, 20);

        // 内阴影效果
        if (!getModel().isPressed()) {
            g2d.setColor(new Color(255, 255, 255, 50));
            g2d.fillRoundRect(2, 2, width - 4, 4, 20, 20);
        }

        // 绘制文字
        drawText(g2d);

        g2d.dispose();
    }

    private void drawText(Graphics2D g2d) {
        String text = getText();
        g2d.setFont(getFont());
        FontMetrics fm = g2d.getFontMetrics();
        int textX = (getWidth() - fm.stringWidth(text)) / 2;
        int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;

        // 文字阴影
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.drawString(text, textX + 1, textY + 1);

        // 文字本身
        g2d.setColor(Color.WHITE);
        g2d.drawString(text, textX, textY);
    }
}