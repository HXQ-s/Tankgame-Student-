package tankgame.ui;

import tankgame.GameLauncherFrame;

import javax.swing.*;
import java.awt.*;

/**
 * 主菜单面板
 */
public class MainMenuPanel extends JPanel {
    private GameLauncherFrame parent;
    private static final Color TITLE_COLOR = new Color(255, 140, 0);
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 30);

    public MainMenuPanel(GameLauncherFrame parent) {
        this.parent = parent;
        setOpaque(false);
        setLayout(new GridBagLayout());
        initComponents();
    }

    private void initComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 添加顶部间距
        gbc.insets = new Insets(30, 0, 10, 0);
        add(new JLabel(" "), gbc);

        // 开始游戏按钮
        ModernButton startButton = new ModernButton("开始游戏", new Color(100, 149, 237), 24);
        startButton.addActionListener(e -> parent.showModeSelection());
        gbc.insets = new Insets(20, 200, 20, 200);
        add(startButton, gbc);

        // 游戏设置按钮
        ModernButton settingsButton = new ModernButton("游戏设置", new Color(119, 136, 153), 24);
        settingsButton.addActionListener(e -> parent.showSettings());
        add(settingsButton, gbc);

        // 退出游戏按钮
        ModernButton exitButton = new ModernButton("退出游戏", new Color(220, 100, 80), 24);
        exitButton.addActionListener(e -> System.exit(0));
        add(exitButton, gbc);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // 绘制主标题
        drawTitle(g2d);

        // 绘制副标题
        drawSubtitle(g2d);

        // 绘制提示信息
        drawHint(g2d);
    }

    private void drawTitle(Graphics2D g2d) {
        g2d.setFont(new Font("STXingkai", Font.BOLD, 100));
        String title = "坦克大战";
        FontMetrics fm = g2d.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        int titleX = (getWidth() - titleWidth) / 2;
        int titleY = 150;

        // 阴影
        g2d.setColor(SHADOW_COLOR);
        g2d.drawString(title, titleX + 4, titleY + 4);

        // 渐变文字
        GradientPaint gradient = new GradientPaint(titleX, titleY, TITLE_COLOR,
                titleX + titleWidth, titleY,
                new Color(255, 100, 50));
        g2d.setPaint(gradient);
        g2d.drawString(title, titleX, titleY);
    }

    private void drawSubtitle(Graphics2D g2d) {
        g2d.setFont(new Font("STXingkai", Font.PLAIN, 18));
        String subtitle = "经典重现 · 坦克大战";
        FontMetrics fm = g2d.getFontMetrics();
        int subWidth = fm.stringWidth(subtitle);
        g2d.setColor(new Color(100, 100, 120));
        g2d.drawString(subtitle, (getWidth() - subWidth) / 2, 200);
    }

    private void drawHint(Graphics2D g2d) {
        g2d.setFont(new Font("STXingkai", Font.PLAIN, 12));
        String escHint = "按 ESC 键返回主菜单";
        FontMetrics fm = g2d.getFontMetrics();
        int hintWidth = fm.stringWidth(escHint);
        g2d.setColor(new Color(150, 150, 160));
        g2d.drawString(escHint, getWidth() - hintWidth - 20, getHeight() - 20);
    }
}