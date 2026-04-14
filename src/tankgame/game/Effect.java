package tankgame.game;

import java.awt.*;

/**
 * 简易特效：爆炸圆圈
 */
public class Effect {
    private final int x;
    private final int y;
    private int radius = 10;
    private int alpha = 255;      // 透明度
    private boolean active = true;
    private final long startTime;

    public Effect(int x, int y) {
        this.x = x;
        this.y = y;
        this.startTime = System.currentTimeMillis();
    }

    public void update() {
        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed > 200) {  // 200毫秒后消失
            active = false;
            return;
        }
        // 半径逐渐扩大，透明度逐渐降低
        float progress = elapsed / 200f;
        int maxRadius = 25;
        radius = (int)(10 + (maxRadius - 10) * progress);
        alpha = (int)(255 * (1 - progress));
    }

    public void draw(Graphics2D g2d) {
        if (!active) return;
        g2d.setColor(new Color(255, 100, 0, alpha));
        g2d.fillOval(x - radius/2, y - radius/2, radius, radius);
        g2d.setColor(new Color(255, 255, 0, alpha));
        g2d.fillOval(x - radius/4, y - radius/4, radius/2, radius/2);
    }

    public boolean isActive() {
        return active;
    }
}