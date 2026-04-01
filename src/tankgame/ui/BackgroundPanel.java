package tankgame.ui;

import tankgame.background.BackgroundManager;

import javax.swing.*;
import java.awt.*;

/**
 * 背景面板 - 负责绘制背景
 */
public class BackgroundPanel extends JPanel {
    private BackgroundManager backgroundManager;

    // 渐变背景颜色
    private static final Color BG_COLOR_1 = new Color(240, 248, 255);
    private static final Color BG_COLOR_2 = new Color(255, 255, 245);

    public BackgroundPanel(BackgroundManager backgroundManager) {
        this.backgroundManager = backgroundManager;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        if (backgroundManager.hasBackground()) {
            // 绘制背景图片
            g2d.drawImage(backgroundManager.getBackgroundImage().getImage(),
                    0, 0, getWidth(), getHeight(), null);
        } else {
            // 绘制渐变背景
            drawGradientBackground(g2d);
            drawDecorativeEffects(g2d);
        }
    }

    private void drawGradientBackground(Graphics2D g2d) {
        GradientPaint gradient = new GradientPaint(0, 0, BG_COLOR_1,
                0, getHeight(), BG_COLOR_2);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }

    private void drawDecorativeEffects(Graphics2D g2d) {
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        for (int i = 0; i < 3; i++) {
            RadialGradientPaint radialGradient = new RadialGradientPaint(
                    getWidth() / 2 + (i - 1) * 100,
                    getHeight() / 2 + (i - 1) * 100,
                    400,
                    new float[]{0f, 1f},
                    new Color[]{new Color(255, 200, 100, 50),
                            new Color(255, 200, 100, 0)}
            );
            g2d.setPaint(radialGradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }
}