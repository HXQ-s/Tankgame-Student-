package tankgame.ui;

import tankgame.GameLauncherFrame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import java.awt.*;

/**
 * 游戏模式选择面板
 */
public class ModeSelectionPanel extends JPanel {
    private final GameLauncherFrame parent;
    private static final Color TITLE_COLOR = new Color(255, 140, 0);
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 30);

    public ModeSelectionPanel(GameLauncherFrame parent) {
        this.parent = parent;
        setOpaque(false);
        setLayout(new GridBagLayout());
        initComponents();

        // 添加 ESC 键监听
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    parent.showMainMenu();  // 返回主菜单
                }
            }
        });
    }

    private void initComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 添加间距
        gbc.insets = new Insets(150, 200, 30, 200);
        add(new JLabel(" "), gbc);

        // 多人对战按钮
        ModernButton multiplayerButton = new ModernButton("多人对战",
                new Color(100, 149, 237), 22);
        multiplayerButton.setPreferredSize(new Dimension(320, 85));
        multiplayerButton.addActionListener(e -> parent.startGame("multiplayer"));
        gbc.insets = new Insets(20, 200, 20, 200);
        add(multiplayerButton, gbc);

        // 关卡模式按钮
        ModernButton levelModeButton = new ModernButton("关卡模式",
                new Color(255, 140, 0), 22);
        levelModeButton.setPreferredSize(new Dimension(320, 85));
        levelModeButton.addActionListener(e -> parent.startGame("level mode"));
        add(levelModeButton, gbc);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setFont(new Font("STXingkai", Font.BOLD, 42));
        String title = "选择游戏模式";
        FontMetrics fm = g2d.getFontMetrics();
        int titleX = (getWidth() - fm.stringWidth(title)) / 2;
        int titleY = 100;

        // 阴影
        g2d.setColor(SHADOW_COLOR);
        g2d.drawString(title, titleX + 3, titleY + 3);

        // 标题文字
        g2d.setColor(TITLE_COLOR);
        g2d.drawString(title, titleX, titleY);
    }
}