package tankgame.game;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * 坦克类 - 包含移动、开火等核心功能
 */
public class Tank {
    // 坦克属性
    private int x, y;
    private int width = 48;      // 根据图片大小调整
    private int height = 48;     // 根据图片大小调整
    private Direction direction;
    private int speed = 5;
    private Color color;
    private boolean isAlive = true;
    private int health = 100;
    private int shootCooldown = 0;
    private final int COOLDOWN_MAX = 30;

    // 移动状态
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean shootPressed = false;

    // 子弹列表
    private List<Bullet> bullets;

    // 玩家标识
    private int playerId;

    // 按键配置
    private int keyUp, keyDown, keyLeft, keyRight, keyShoot;

    // 图片素材相关
    private BufferedImage[] tankImages;  // 存储四个方向的坦克图片
    private boolean useImage = true;     // 是否使用图片素材
    private String imagePath = "E:\\JAVA\\Program1\\src\\resources\\Tank";

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public Tank(int x, int y, Color color, int playerId) {
        this.x = x;
        this.y = y;
        this.playerId = playerId;
        this.direction = Direction.UP;
        this.bullets = new ArrayList<>();
        // 加载坦克图片
        loadTankImages();
    }

    /**
     * 加载坦克图片素材
     */
    private void loadTankImages() {
        tankImages = new BufferedImage[4];

        // 根据玩家ID选择不同的坦克图片
        String tankPrefix = (playerId == 1) ? "tank_green" : "tank_blue";

        try {
            // 加载四个方向的图片
            File upFile = new File(imagePath + File.separator + tankPrefix + "_up.png");
            File downFile = new File(imagePath + File.separator + tankPrefix + "_down.png");
            File leftFile = new File(imagePath + File.separator + tankPrefix + "_left.png");
            File rightFile = new File(imagePath + File.separator + tankPrefix + "_right.png");

            // 检查文件是否存在
            if (upFile.exists() && downFile.exists() &&
                    leftFile.exists() && rightFile.exists()) {
                tankImages[0] = ImageIO.read(upFile);
                tankImages[1] = ImageIO.read(downFile);
                tankImages[2] = ImageIO.read(leftFile);
                tankImages[3] = ImageIO.read(rightFile);

                // 根据图片大小调整坦克尺寸
                if (tankImages[0] != null) {
                    width = tankImages[0].getWidth();
                    height = tankImages[0].getHeight();
                }
                useImage = true;
                System.out.println("坦克图片加载成功: " + tankPrefix);
            } else {
                // 如果图片不存在，尝试加载通用坦克图片
                loadDefaultTankImages(tankPrefix);
            }
        } catch (IOException e) {
            System.err.println("加载坦克图片失败: " + e.getMessage());
            useImage = false;
        }
    }

    /**
     * 加载默认坦克图片（如果没有特定方向的图片）
     */
    private void loadDefaultTankImages(String tankPrefix) {
        try {
            // 尝试加载单个坦克图片，然后旋转使用
            File defaultFile = new File(imagePath + File.separator + tankPrefix + ".png");
            if (defaultFile.exists()) {
                BufferedImage originalImage = ImageIO.read(defaultFile);
                if (originalImage != null) {
                    // 创建四个方向的旋转图片
                    tankImages[0] = originalImage;  // 上
                    tankImages[1] = rotateImage(originalImage, 180);  // 下
                    tankImages[2] = rotateImage(originalImage, 270);  // 左
                    tankImages[3] = rotateImage(originalImage, 90);   // 右

                    width = originalImage.getWidth();
                    height = originalImage.getHeight();
                    useImage = true;
                    System.out.println("使用默认坦克图片并旋转: " + tankPrefix);
                }
            } else {
                useImage = false;
                System.out.println("未找到坦克图片，使用图形绘制");
            }
        } catch (IOException e) {
            useImage = false;
            System.err.println("加载默认坦克图片失败: " + e.getMessage());
        }
    }

    /**
     * 旋转图片
     */
    private BufferedImage rotateImage(BufferedImage original, int degrees) {
        int width = original.getWidth();
        int height = original.getHeight();

        // 创建旋转后的图片
        BufferedImage rotated = new BufferedImage(width, height, original.getType());
        Graphics2D g2d = rotated.createGraphics();

        // 设置旋转中心
        g2d.rotate(Math.toRadians(degrees), width / 2, height / 2);
        g2d.drawImage(original, 0, 0, null);
        g2d.dispose();

        return rotated;
    }

    /**
     * 设置按键配置
     */
    public void setKeyBindings(int up, int down, int left, int right, int shoot) {
        this.keyUp = up;
        this.keyDown = down;
        this.keyLeft = left;
        this.keyRight = right;
        this.keyShoot = shoot;
    }

    /**
     * 处理按键事件
     */
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == keyUp) upPressed = true;
        if (key == keyDown) downPressed = true;
        if (key == keyLeft) leftPressed = true;
        if (key == keyRight) rightPressed = true;
        if (key == keyShoot) shootPressed = true;
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == keyUp) upPressed = false;
        if (key == keyDown) downPressed = false;
        if (key == keyLeft) leftPressed = false;
        if (key == keyRight) rightPressed = false;
        if (key == keyShoot) shootPressed = false;
    }

    /**
     * 更新坦克状态（移动、射击）
     */
    public void update() {
        if (!isAlive) return;

        // 更新移动
        updateMovement();

        // 更新射击冷却
        if (shootCooldown > 0) {
            shootCooldown--;
        }

        // 处理射击
        if (shootPressed && shootCooldown == 0) {
            shoot();
            shootCooldown = COOLDOWN_MAX;
        }

        // 更新所有子弹
        bullets.removeIf(bullet -> !bullet.isActive());
        for (Bullet bullet : bullets) {
            bullet.update();
        }
    }

    /**
     * 更新移动逻辑
     */
    private void updateMovement() {
        int newX = x;
        int newY = y;

        // 根据按键更新方向
        if (upPressed) {
            newY -= speed;
            direction = Direction.UP;
        }
        if (downPressed) {
            newY += speed;
            direction = Direction.DOWN;
        }
        if (leftPressed) {
            newX -= speed;
            direction = Direction.LEFT;
        }
        if (rightPressed) {
            newX += speed;
            direction = Direction.RIGHT;
        }

        // 边界检查（游戏区域边界）
        int gameWidth = 1200;
        int gameHeight = 800;
        int borderOffset = 20;

        if (newX >= borderOffset && newX + width <= gameWidth - borderOffset) {
            x = newX;
        }
        if (newY >= borderOffset && newY + height <= gameHeight - borderOffset) {
            y = newY;
        }
    }

    /**
     * 射击方法
     */
    public void shoot() {
        int bulletX = x + width / 2 - Bullet.getWIDTH() / 2;
        int bulletY = y + height / 2 - Bullet.getHEIGHT() / 2;

        Bullet bullet = new Bullet(bulletX, bulletY, direction, playerId);
        bullets.add(bullet);
    }


    /**
     * 获取当前方向的图片
     */
    private BufferedImage getCurrentDirectionImage() {
        if (tankImages == null) return null;

        switch (direction) {
            case UP:
                return tankImages[0];
            case DOWN:
                return tankImages[1];
            case LEFT:
                return tankImages[2];
            case RIGHT:
                return tankImages[3];
            default:
                return tankImages[0];
        }
    }

    /**
     * 使用图形绘制坦克（备用方案）
     */
    private void drawGraphicTank(Graphics2D g2d) {
        // 绘制坦克身体
        g2d.setColor(color);
        g2d.fillRect(x, y, width, height);

        // 绘制坦克炮塔
        g2d.setColor(color.darker());
        int turretSize = 20;
        g2d.fillOval(x + width/2 - turretSize/2, y + height/2 - turretSize/2, turretSize, turretSize);

        // 绘制炮管
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(3));
        int barrelLength = 20;
        switch (direction) {
            case UP:
                g2d.drawLine(x + width/2, y + height/2, x + width/2, y - barrelLength);
                break;
            case DOWN:
                g2d.drawLine(x + width/2, y + height/2, x + width/2, y + height + barrelLength);
                break;
            case LEFT:
                g2d.drawLine(x + width/2, y + height/2, x - barrelLength, y + height/2);
                break;
            case RIGHT:
                g2d.drawLine(x + width/2, y + height/2, x + width + barrelLength, y + height/2);
                break;
        }

        // 绘制玩家标识
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("P" + playerId, x + width/2 - 8, y - 5);
    }

    /**
     * 绘制生命值条
     */
    private void drawHealthBar(Graphics2D g2d) {
        int barWidth = width;
        int barHeight = 5;
        int barX = x;
        int barY = y - 10;

        // 背景
        g2d.setColor(Color.RED);
        g2d.fillRect(barX, barY, barWidth, barHeight);

        // 当前生命值
        g2d.setColor(Color.GREEN);
        int currentWidth = barWidth * health / 100;
        g2d.fillRect(barX, barY, currentWidth, barHeight);
    }

    /**
     * 受到伤害
     */
    public void takeDamage(int damage) {
        if (!isAlive) return;

        health -= damage;
        if (health <= 0) {
            health = 0;
            isAlive = false;
        }
    }

    /**
     * 获取坦克的边界矩形（用于碰撞检测）
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    /**
     * 获取子弹列表
     */
    public List<Bullet> getBullets() {
        return bullets;
    }

    /**
     * 设置图片路径
     */
    public void setImagePath(String path) {
        this.imagePath = path;
        loadTankImages();
    }

    // Getters and Setters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public boolean isAlive() { return isAlive; }
    public int getHealth() { return health; }
    public int getPlayerId() { return playerId; }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}