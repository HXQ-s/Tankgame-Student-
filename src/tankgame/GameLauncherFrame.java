package tankgame;

import tankgame.ui.BackgroundPanel;
import tankgame.ui.MainMenuPanel;
import tankgame.ui.ModeSelectionPanel;
import tankgame.ui.SettingsPanel;
import tankgame.config.KeyConfig;
import tankgame.config.GameConfig;
import tankgame.background.BackgroundManager;
import tankgame.game.GameWindow;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * 坦克大战启动器主窗口 - 使用 KeyListener 版本
 */
public class GameLauncherFrame extends JFrame {
    private CardLayout cardLayout;
    private BackgroundPanel backgroundPanel;
    private GameConfig gameConfig;

    // 面板组件
    private MainMenuPanel mainMenuPanel;

    public GameLauncherFrame() {
        initConfig();
        initUI();
        setupGlobalEscListener();
        startBackgroundAnimation();
    }

    private void initConfig() {
        gameConfig = new GameConfig();
        gameConfig.setPlayer1Keys(new KeyConfig(
                KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A,
                KeyEvent.VK_D, KeyEvent.VK_SPACE
        ));
        gameConfig.setPlayer2Keys(new KeyConfig(
                KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT,
                KeyEvent.VK_RIGHT, KeyEvent.VK_ENTER
        ));
    }

    private void initUI() {
        setTitle("坦克大战 - Tank Battle");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setResizable(false);

        // 初始化背景管理器
        BackgroundManager backgroundManager = new BackgroundManager();

        // 创建背景面板
        backgroundPanel = new BackgroundPanel(backgroundManager);
        cardLayout = new CardLayout();
        backgroundPanel.setLayout(cardLayout);
        setContentPane(backgroundPanel);

        // 创建各个面板
        mainMenuPanel = new MainMenuPanel(this);
        ModeSelectionPanel modeSelectionPanel = new ModeSelectionPanel(this);
        SettingsPanel settingsPanel = new SettingsPanel(this, gameConfig);

        // 添加面板
        backgroundPanel.add(mainMenuPanel, "main");
        backgroundPanel.add(modeSelectionPanel, "mode");
        backgroundPanel.add(settingsPanel, "settings");

        // 显示主菜单
        cardLayout.show(backgroundPanel, "main");

        // 确保框架可以获得键盘事件
        setFocusable(true);
        requestFocusInWindow();
    }

    private void setupGlobalEscListener() {
        // 为框架添加键盘监听
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    if (!isMainMenuShowing()) {
                        showMainMenu();
                    }
                }
            }
        });
    }

    private boolean isMainMenuShowing() {
        Component current = null;
        for (Component comp : backgroundPanel.getComponents()) {
            if (comp.isVisible()) {
                current = comp;
                break;
            }
        }
        return current == mainMenuPanel;
    }

    public void showMainMenu() {
        cardLayout.show(backgroundPanel, "main");
        // 重新获取焦点，确保键盘监听有效
        requestFocusInWindow();
    }

    public void showModeSelection() {
        cardLayout.show(backgroundPanel, "mode");
        requestFocusInWindow();
    }

    public void showSettings() {
        cardLayout.show(backgroundPanel, "settings");
        requestFocusInWindow();
    }

    public void startGame(String mode) {
        String modeName = mode.equals("multiplayer") ? "多人对战" : "关卡模式";
        int result = JOptionPane.showConfirmDialog(this,
                "即将启动游戏 - " + modeName + "\n\n确定要开始游戏吗？",
                "开始游戏", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            // 关闭启动器
            setVisible(false);

            // 启动游戏窗口
            SwingUtilities.invokeLater(() -> {
                GameWindow gameWindow = new GameWindow();
                gameWindow.startGame();

                // 可选：监听游戏窗口关闭事件，重新显示启动器
                gameWindow.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent e) {
                        setVisible(true);
                    }
                });
            });
        }
    }

    private void startBackgroundAnimation() {
        javax.swing.Timer timer = new javax.swing.Timer(50, e -> {
            if (backgroundPanel != null) {
                backgroundPanel.repaint();
            }
        });
        timer.start();
    }

}