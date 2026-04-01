package tankgame.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 游戏主面板 - 管理游戏循环和碰撞检测
 */
public class GamePanel extends JPanel implements Runnable {

    private Thread gameThread;
    private boolean running = true;  // ✅ 添加这个属性

    // 坦克列表
    private List<Tank> tanks;
    private Tank player1;
    private Tank player2;

    // 游戏状态
    private boolean gameOver = false;
    private int winner = 0;

    // 游戏区域大小
    private final int GAME_WIDTH = 1200;
    private final int GAME_HEIGHT = 800;

    // 障碍物列表（可选）
    private List<Rectangle> obstacles;

    public GamePanel() {
        setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        setBackground(Color.DARK_GRAY);
        setFocusable(true);

        initGame();
        setupKeyListener();

        startGameThread();
    }

    /**
     * 初始化游戏
     */
    private void initGame() {
        tanks = new ArrayList<>();
        obstacles = new ArrayList<>();

        // 创建玩家坦克
        player1 = new Tank(100, GAME_HEIGHT - 150, 1);
        player2 = new Tank(GAME_WIDTH - 140, GAME_HEIGHT - 150, 2);

        // 玩家1: W/S 前进后退，A/D 左右旋转
        player1.setKeyBindings(
                KeyEvent.VK_W,      // 前进
                KeyEvent.VK_S,      // 后退
                KeyEvent.VK_A,      // 左转
                KeyEvent.VK_D,      // 右转
                KeyEvent.VK_SPACE   // 射击
        );

// 玩家2: 方向键前进后退，左右箭头旋转
        player2.setKeyBindings(
                KeyEvent.VK_UP,     // 前进
                KeyEvent.VK_DOWN,   // 后退
                KeyEvent.VK_LEFT,   // 左转
                KeyEvent.VK_RIGHT,  // 右转
                KeyEvent.VK_ENTER   // 射击
        );

        tanks.add(player1);
        tanks.add(player2);

        initObstacles();
    }

    /**
     * 初始化障碍物
     */
    private void initObstacles() {
        // 添加一些砖墙作为障碍物
        for (int i = 0; i < 5; i++) {
            obstacles.add(new Rectangle(200 + i * 80, 300, 40, 40));
            obstacles.add(new Rectangle(200 + i * 80, 350, 40, 40));
        }

        for (int i = 0; i < 5; i++) {
            obstacles.add(new Rectangle(GAME_WIDTH - 400 + i * 80, 300, 40, 40));
            obstacles.add(new Rectangle(GAME_WIDTH - 400 + i * 80, 350, 40, 40));
        }

        // 添加中心障碍物
        obstacles.add(new Rectangle(GAME_WIDTH/2 - 50, GAME_HEIGHT/2 - 50, 100, 100));
    }

    /**
     * 设置键盘监听
     */
    private void setupKeyListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                player1.keyPressed(e);
                player2.keyPressed(e);

                // 按R键重新开始
                if (e.getKeyCode() == KeyEvent.VK_R && gameOver) {
                    restartGame();
                }

                // 按ESC键退出游戏
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    stopGame();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                player1.keyReleased(e);
                player2.keyReleased(e);
            }
        });
    }

    /**
     * 开始游戏线程
     */
    private void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * 停止游戏
     */
    public void stopGame() {
        running = false;
        if (gameThread != null) {
            gameThread.interrupt();
        }
    }

    /**
     * 游戏主循环
     */
    @Override
    public void run() {
        int FPS = 60;
        double timePerFrame = 1000000000.0 / FPS;
        long lastTime = System.nanoTime();

        // ✅ 修复：使用 running 变量控制循环
        while (running) {
            long now = System.nanoTime();
            double delta = (now - lastTime) / timePerFrame;

            if (delta >= 1) {
                update();
                repaint();
                lastTime = now;
            }
        }
    }

    /**
     * 更新游戏逻辑
     */
    private void update() {
        if (gameOver) return;

        // 更新坦克
        for (Tank tank : tanks) {
            tank.update();
        }

        // 碰撞检测
        checkCollisions();

        // 检查游戏结束条件
        checkGameOver();
    }

    /**
     * 碰撞检测
     */
    private void checkCollisions() {
        // 坦克之间的碰撞
        checkTankCollision(player1, player2);

        // 坦克与障碍物的碰撞
        for (Tank tank : tanks) {
            for (Rectangle obstacle : obstacles) {
                if (tank.getBounds().intersects(obstacle)) {
                    resolveTankCollision(tank, obstacle);
                }
            }
        }

        // 子弹碰撞检测
        for (Tank tank : tanks) {
            List<Bullet> bullets = tank.getBullets();
            Iterator<Bullet> bulletIterator = bullets.iterator();

            while (bulletIterator.hasNext()) {
                Bullet bullet = bulletIterator.next();
                boolean hit = false;

                // 检查是否击中敌方坦克
                for (Tank otherTank : tanks) {
                    if (otherTank != tank && otherTank.isAlive() &&
                            bullet.hitTarget(otherTank.getBounds())) {
                        otherTank.takeDamage(bullet.getDamage());
                        bulletIterator.remove();
                        hit = true;
                        break;
                    }
                }

                // 检查是否击中障碍物
                if (!hit) {
                    for (Rectangle obstacle : obstacles) {
                        if (bullet.getBounds().intersects(obstacle)) {
                            bulletIterator.remove();
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * 坦克之间的碰撞处理
     */
    private void checkTankCollision(Tank t1, Tank t2) {
        if (t1.getBounds().intersects(t2.getBounds())) {
            // 分离坦克
            Rectangle rect1 = t1.getBounds();
            Rectangle rect2 = t2.getBounds();

            int dx = rect1.x + rect1.width/2 - (rect2.x + rect2.width/2);
            int dy = rect1.y + rect1.height/2 - (rect2.y + rect2.height/2);

            if (Math.abs(dx) > Math.abs(dy)) {
                if (dx > 0) {
                    t1.setPosition(t1.getX() + 5, t1.getY());
                    t2.setPosition(t2.getX() - 5, t2.getY());
                } else {
                    t1.setPosition(t1.getX() - 5, t1.getY());
                    t2.setPosition(t2.getX() + 5, t2.getY());
                }
            } else {
                if (dy > 0) {
                    t1.setPosition(t1.getX(), t1.getY() + 5);
                    t2.setPosition(t2.getX(), t2.getY() - 5);
                } else {
                    t1.setPosition(t1.getX(), t1.getY() - 5);
                    t2.setPosition(t2.getX(), t2.getY() + 5);
                }
            }
        }
    }

    /**
     * 坦克与障碍物的碰撞处理
     */
    private void resolveTankCollision(Tank tank, Rectangle obstacle) {
        Rectangle tankRect = tank.getBounds();
        int dx = tankRect.x + tankRect.width/2 - (obstacle.x + obstacle.width/2);
        int dy = tankRect.y + tankRect.height/2 - (obstacle.y + obstacle.height/2);

        if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 0) {
                tank.setPosition(tank.getX() + 5, tank.getY());
            } else {
                tank.setPosition(tank.getX() - 5, tank.getY());
            }
        } else {
            if (dy > 0) {
                tank.setPosition(tank.getX(), tank.getY() + 5);
            } else {
                tank.setPosition(tank.getX(), tank.getY() - 5);
            }
        }
    }

    /**
     * 检查游戏是否结束
     */
    private void checkGameOver() {
        boolean player1Alive = player1.isAlive();
        boolean player2Alive = player2.isAlive();

        if (!player1Alive && !player2Alive) {
            gameOver = true;
            winner = 0;
        } else if (!player1Alive) {
            gameOver = true;
            winner = 2;
        } else if (!player2Alive) {
            gameOver = true;
            winner = 1;
        }
    }

    /**
     * 重新开始游戏
     */
    private void restartGame() {
        gameOver = false;
        winner = 0;
        initGame();
        // 重新请求焦点
        requestFocusInWindow();
    }

    /**
     * 绘制游戏画面
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // 绘制背景
        drawBackground(g2d);

        // 绘制障碍物
        drawObstacles(g2d);

        // 绘制坦克
        for (Tank tank : tanks) {
            tank.draw(g);
        }

        // 绘制UI
        drawUI(g2d);

        // 绘制游戏结束画面
        if (gameOver) {
            drawGameOver(g2d);
        }
    }

    /**
     * 绘制背景
     */
    private void drawBackground(Graphics2D g2d) {
        // 渐变背景
        GradientPaint gradient = new GradientPaint(0, 0, new Color(50, 50, 50),
                GAME_WIDTH, GAME_HEIGHT, new Color(30, 30, 30));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        // 绘制网格
        g2d.setColor(new Color(100, 100, 100, 50));
        for (int i = 0; i < GAME_WIDTH; i += 50) {
            g2d.drawLine(i, 0, i, GAME_HEIGHT);
            g2d.drawLine(0, i, GAME_WIDTH, i);
        }
    }

    /**
     * 绘制障碍物
     */
    private void drawObstacles(Graphics2D g2d) {
        for (Rectangle obstacle : obstacles) {
            // 砖墙效果
            g2d.setColor(new Color(180, 100, 50));
            g2d.fillRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
            g2d.setColor(new Color(140, 70, 30));
            g2d.drawRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);

            // 添加砖纹效果
            g2d.setColor(new Color(200, 120, 60));
            g2d.drawLine(obstacle.x + obstacle.width/2, obstacle.y,
                    obstacle.x + obstacle.width/2, obstacle.y + obstacle.height);
            g2d.drawLine(obstacle.x, obstacle.y + obstacle.height/2,
                    obstacle.x + obstacle.width, obstacle.y + obstacle.height/2);
        }
    }

    /**
     * 绘制UI界面
     */
    private void drawUI(Graphics2D g2d) {
        // 绘制生命值条
        g2d.setFont(new Font("华文行楷", Font.BOLD, 16));

        // 玩家1
        g2d.setColor(Color.GREEN);
        g2d.drawString("玩家1", 20, 30);
        drawHealthBar(g2d, 20, player1.getHealth());

        // 玩家2
        g2d.setColor(Color.BLUE);
        g2d.drawString("玩家2", GAME_WIDTH - 220, 30);
        drawHealthBar(g2d, GAME_WIDTH - 220, player2.getHealth());

        // 提示信息
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("华文行楷", Font.PLAIN, 12));
        g2d.drawString("W/S: 前进/后退 | A/D: 旋转 | SPACE: 射击", 20, GAME_HEIGHT - 20);
        g2d.drawString("↑/↓: 前进/后退 | ←/→: 旋转 | ENTER: 射击", GAME_WIDTH - 350, GAME_HEIGHT - 20);
        g2d.drawString("按 R 键重新开始 | ESC 键退出", GAME_WIDTH/2 - 120, GAME_HEIGHT - 20);
    }

    /**
     * 绘制生命值条
     */
    private void drawHealthBar(Graphics2D g2d, int x, int health) {
        // 背景
        g2d.setColor(Color.RED);
        g2d.fillRect(x, 40, 200, 20);

        // 当前生命值
        g2d.setColor(Color.GREEN);
        int currentWidth = 200 * health / 100;
        g2d.fillRect(x, 40, currentWidth, 20);

        // 边框
        g2d.setColor(Color.WHITE);
        g2d.drawRect(x, 40, 200, 20);
    }

    /**
     * 绘制游戏结束画面
     */
    private void drawGameOver(Graphics2D g2d) {
        // 半透明遮罩
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        // 游戏结束文字
        g2d.setFont(new Font("华文行楷", Font.BOLD, 72));
        g2d.setColor(Color.WHITE);
        String gameOverText = "GAME OVER";
        FontMetrics fm = g2d.getFontMetrics();
        int textX = (GAME_WIDTH - fm.stringWidth(gameOverText)) / 2;
        g2d.drawString(gameOverText, textX, GAME_HEIGHT/2 - 100);

        // 胜利者信息
        if (winner > 0) {
            g2d.setFont(new Font("华文行楷", Font.BOLD, 48));
            String winnerText = "玩家 " + winner + " 胜利！";
            fm = g2d.getFontMetrics();
            textX = (GAME_WIDTH - fm.stringWidth(winnerText)) / 2;
            g2d.setColor(winner == 1 ? Color.GREEN : Color.BLUE);
            g2d.drawString(winnerText, textX, GAME_HEIGHT/2);
        } else {
            g2d.setFont(new Font("华文行楷", Font.BOLD, 48));
            String drawText = "平局！";
            fm = g2d.getFontMetrics();
            textX = (GAME_WIDTH - fm.stringWidth(drawText)) / 2;
            g2d.setColor(Color.YELLOW);
            g2d.drawString(drawText, textX, GAME_HEIGHT/2);
        }

        // 重新开始提示
        g2d.setFont(new Font("华文行楷", Font.PLAIN, 24));
        String restartText = "按 R 键重新开始";
        fm = g2d.getFontMetrics();
        textX = (GAME_WIDTH - fm.stringWidth(restartText)) / 2;
        g2d.setColor(Color.WHITE);
        g2d.drawString(restartText, textX, GAME_HEIGHT/2 + 100);
    }
}