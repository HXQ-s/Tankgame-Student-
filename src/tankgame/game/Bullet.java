package tankgame.game;

import java.awt.*;

/**
 * 子弹类 - 支持360度方向
 */
public class Bullet {
    // 子弹属性
    private double x, y;
    private static final int WIDTH = 6;
    private static final int HEIGHT = 6;
    private final double angle;
    private boolean active = true;

    public Bullet(int x, int y, double angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    /**
     * 更新子弹位置
     */
    public void update() {
        if (!active) return;

        double speed = 8;
        x += Math.cos(angle) * speed;
        y += Math.sin(angle) * speed;

        // 边界检查
        if (x < -100 || x > 1300 || y < -100 || y > 900) {
            active = false;
        }
    }

    /**
     * 绘制子弹
     */
    public void draw(Graphics g) {
        if (!active) return;

        Graphics2D g2d = (Graphics2D) g;

        int drawX = (int)x;
        int drawY = (int)y;

        // 绘制子弹主体
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(drawX, drawY, WIDTH, HEIGHT);

        // 绘制光晕
        g2d.setColor(new Color(255, 255, 100, 100));
        g2d.fillOval(drawX - 2, drawY - 2, WIDTH + 4, HEIGHT + 4);

        // 绘制弹道轨迹
        g2d.setColor(new Color(255, 200, 0, 150));
        g2d.setStroke(new BasicStroke(2));

        int centerX = drawX + WIDTH / 2;
        int centerY = drawY + HEIGHT / 2;
        int endX = centerX + (int)(Math.cos(angle) * 15);
        int endY = centerY + (int)(Math.sin(angle) * 15);

        g2d.drawLine(centerX, centerY, endX, endY);
    }

    /**
     * 检查是否击中目标
     */
    public boolean hitTarget(Rectangle targetBounds) {
        if (!active) return false;

        Rectangle bulletBounds = getBounds();
        if (bulletBounds.intersects(targetBounds)) {
            active = false;
            return true;
        }
        return false;
    }

    /**
     * 获取子弹边界矩形
     */
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, WIDTH, HEIGHT);
    }

    // Getters and Setters
    public boolean isActive() { return active; }
    public int getDamage() {
        return 25; }

    public static int getWIDTH() { return WIDTH; }
    public static int getHEIGHT() { return HEIGHT; }

}