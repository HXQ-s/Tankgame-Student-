package tankgame.game;

import java.awt.*;

/**
 * 子弹类 - 包含移动、碰撞等核心功能
 */
public class Bullet {
    // 子弹属性
    private int x, y;
    static final int WIDTH = 6;
    static final int HEIGHT = 6;
    private Tank.Direction direction;
    private int speed = 8;
    private boolean active = true;
    private int damage = 25;
    private int ownerId; // 发射者的玩家ID，用于避免击中自己

    public Bullet(int x, int y, Tank.Direction direction, int ownerId) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.ownerId = ownerId;
    }

    /**
     * 更新子弹位置
     */
    public void update() {
        if (!active) return;

        switch (direction) {
            case UP:
                y -= speed;
                break;
            case DOWN:
                y += speed;
                break;
            case LEFT:
                x -= speed;
                break;
            case RIGHT:
                x += speed;
                break;
        }

        // 边界检查（超出边界则失效）
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

        // 绘制子弹主体
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(x, y, WIDTH, HEIGHT);

        // 添加光晕效果
        g2d.setColor(new Color(255, 255, 100, 100));
        g2d.fillOval(x - 2, y - 2, WIDTH + 4, HEIGHT + 4);

        // 绘制弹道轨迹
        g2d.setColor(new Color(255, 200, 0, 150));
        g2d.setStroke(new BasicStroke(2));
        switch (direction) {
            case UP:
                g2d.drawLine(x + WIDTH/2, y + HEIGHT, x + WIDTH/2, y - 10);
                break;
            case DOWN:
                g2d.drawLine(x + WIDTH/2, y, x + WIDTH/2, y + HEIGHT + 10);
                break;
            case LEFT:
                g2d.drawLine(x + WIDTH, y + HEIGHT/2, x - 10, y + HEIGHT/2);
                break;
            case RIGHT:
                g2d.drawLine(x, y + HEIGHT/2, x + WIDTH + 10, y + HEIGHT/2);
                break;
        }
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
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }

    // Getters and Setters
    public boolean isActive() { return active; }
    public int getDamage() { return damage; }
    public int getOwnerId() { return ownerId; }
    public int getX() { return x; }
    public int getY() { return y; }

    public static int getWIDTH() { return WIDTH; }
    public static int getHEIGHT() { return HEIGHT; }

    public void setActive(boolean active) {
        this.active = active;
    }
}