package tankgame.game;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * 坦克类 - 支持360度旋转
 */
public class Tank {
    // 坦克属性
    private int x, y;
    private int width = 48;
    private int height = 48;
    private double angle = 0;              // 坦克朝向角度（弧度）
    private boolean isAlive = true;
    private int health = 100;
    private int shootCooldown = 0;

    // 移动状态 - 修改这些属性名
    private boolean forwardPressed = false;    // 前进
    private boolean backwardPressed = false;   // 后退
    private boolean leftRotatePressed = false; // 左转
    private boolean rightRotatePressed = false;// 右转
    private boolean shootPressed = false;

    // 子弹列表
    private final List<Bullet> bullets;

    // 玩家标识
    private final int playerId;

    // 按键配置 - 修改这些属性名
    private int keyForward;      // 前进键
    private int keyBackward;     // 后退键
    private int keyRotateLeft;   // 左转键
    private int keyRotateRight;  // 右转键
    private int keyShoot;        // 射击键

    // 图片素材
    private BufferedImage tankImage;

    public Tank(int x, int y, int playerId) {
        this.x = x;
        this.y = y;
        this.playerId = playerId;
        this.bullets = new ArrayList<>();
        loadTankImage();
    }

    /**
     * 加载坦克图片
     */
    private void loadTankImage() {
        String tankName = (playerId == 1) ? "tank_green" : "tank_blue";
        int targetWidth = 48;   // 目标宽度
        int targetHeight = 48;  // 目标高度
        try {
            String imagePath = "E:\\JAVA\\Program1\\src\\resources\\Tank";
            File imageFile = new File(imagePath + File.separator + tankName + ".png");
            if (imageFile.exists()) {
                BufferedImage original = ImageIO.read(imageFile);
                if (original != null) {
                    // 缩放图片
                    tankImage = scaleImage(original, targetWidth, targetHeight);
                    width = targetWidth;
                    height = targetHeight;
                    System.out.println("坦克图片加载成功并缩放: " + tankName);
                }
            } else {
                System.err.println("错误：找不到坦克图片！使用默认图形");
                createDefaultImage(targetWidth, targetHeight);
            }
        } catch (IOException e) {
            System.err.println("加载坦克图片失败: " + e.getMessage());
            createDefaultImage(targetWidth, targetHeight);
        }
    }
    /**
     * 缩放图片到指定尺寸
     */
    private BufferedImage scaleImage(BufferedImage original, int targetWidth, int targetHeight) {
        if (original == null) return null;

        // 如果图片已经是目标尺寸，直接返回
        if (original.getWidth() == targetWidth && original.getHeight() == targetHeight) {
            return original;
        }

        BufferedImage scaledImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaledImage.createGraphics();

        // 设置高质量缩放
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(original, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();

        return scaledImage;
    }

    /**
     * 创建默认坦克图像
     */
    private void createDefaultImage(int width, int height) {
        tankImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = tankImage.createGraphics();

        Color tankColor = (playerId == 1) ? Color.GREEN : Color.BLUE;

        // 清除背景（透明）
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, width, height);
        g2d.setComposite(AlphaComposite.SrcOver);

        // 绘制坦克身体
        g2d.setColor(tankColor);
        g2d.fillRect(width/4, height/4, width/2, height/2);

        // 绘制炮塔
        g2d.setColor(tankColor.darker());
        int turretSize = Math.max(8, width/3);
        g2d.fillOval(width/2 - turretSize/2, height/2 - turretSize/2, turretSize, turretSize);

        // 绘制炮管
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(width/2, height/2, width/2, height/4);

        g2d.dispose();

        this.width = width;
        this.height = height;
    }

    /**
     * 设置按键配置
     */
    public void setKeyBindings(int forward, int backward, int rotateLeft, int rotateRight, int shoot) {
        this.keyForward = forward;
        this.keyBackward = backward;
        this.keyRotateLeft = rotateLeft;
        this.keyRotateRight = rotateRight;
        this.keyShoot = shoot;
    }

    /**
     * 处理按键事件
     */
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == keyForward) forwardPressed = true;
        if (key == keyBackward) backwardPressed = true;
        if (key == keyRotateLeft) leftRotatePressed = true;
        if (key == keyRotateRight) rightRotatePressed = true;
        if (key == keyShoot) shootPressed = true;
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == keyForward) forwardPressed = false;
        if (key == keyBackward) backwardPressed = false;
        if (key == keyRotateLeft) leftRotatePressed = false;
        if (key == keyRotateRight) rightRotatePressed = false;
        if (key == keyShoot) shootPressed = false;
    }

    /**
     * 更新坦克状态
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
            shootCooldown = 30;
        }

        // 更新所有子弹 - 修复 isActive() 方法调用
        bullets.removeIf(bullet -> !bullet.isActive());
        for (Bullet bullet : bullets) {
            bullet.update();
        }
    }

    /**
     * 更新移动逻辑（支持旋转）
     */
    private void updateMovement() {
        // 更新旋转
        // 旋转速度
        double rotationSpeed = 0.1;
        if (leftRotatePressed) {
            angle -= rotationSpeed;
        }
        if (rightRotatePressed) {
            angle += rotationSpeed;
        }

        // 归一化角度到 0-2π
        angle = (angle + 2 * Math.PI) % (2 * Math.PI);


        // 计算移动
        int newX = x;
        int newY = y;

        int speed = 5;
        if (forwardPressed) {
            newX += (int)(Math.cos(angle) * speed);
            newY += (int)(Math.sin(angle) * speed);
        }
        if (backwardPressed) {
            newX -= (int)(Math.cos(angle) * speed);
            newY -= (int)(Math.sin(angle) * speed);
        }

        // 边界检查
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



        // 传入角度而不是方向枚举
        Bullet bullet = new Bullet(bulletX, bulletY,angle);
        bullets.add(bullet);
    }

    /**
     * 绘制坦克（支持旋转）
     */
    public void draw(Graphics g) {
        if (!isAlive) return;

        Graphics2D g2d = (Graphics2D) g;
        // 保存原始变换
        AffineTransform oldTransform = g2d.getTransform();

        // 旋转到坦克角度
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        g2d.rotate(angle + Math.PI / 2, centerX, centerY);

        // 绘制坦克图片
        if (tankImage != null) {
            g2d.drawImage(tankImage, x, y, width, height, null);
        }

        // 恢复变换
        g2d.setTransform(oldTransform);

        // 绘制生命值条
        drawHealthBar(g2d);

        // 绘制所有子弹
        for (Bullet bullet : bullets) {
            bullet.draw(g);
        }
    }

    /**
     * 绘制生命值条
     */
    private void drawHealthBar(Graphics2D g2d) {
        int barWidth = width;
        int barHeight = 5;
        int barX = x;
        int barY = y - 10;

        g2d.setColor(Color.RED);
        g2d.fillRect(barX, barY, barWidth, barHeight);

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
     * 获取坦克的边界矩形
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

    // Getters and Setters
    public int getX() { return x; }
    public int getY() { return y; }


    public boolean isAlive() { return isAlive; }
    public int getHealth() { return health; }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}