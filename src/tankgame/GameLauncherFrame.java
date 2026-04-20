package tankgame;

import tankgame.background.BackgroundManager;
import tankgame.config.GameConfig;
import tankgame.config.KeyConfig;
import tankgame.game.GameWindow;
import tankgame.game.MapManager;
import tankgame.ui.BackgroundPanel;
import tankgame.ui.MainMenuPanel;
import tankgame.ui.ModeSelectionPanel;
import tankgame.ui.SettingsPanel;
import java.awt.KeyboardFocusManager;


import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * 坦克大战启动器主窗口 - 使用 KeyListener 版本
 */
public class GameLauncherFrame extends JFrame {
    private CardLayout cardLayout;
    private BackgroundPanel backgroundPanel;
    private GameConfig gameConfig;
    private SettingsPanel settingsPanel;

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
        setMinimumSize(new Dimension(800, 600));

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
        settingsPanel = new SettingsPanel(this, gameConfig);
        backgroundPanel.add(settingsPanel, "settings");

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
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                // 获取当前显示的面板
                Component currentPanel = null;
                for (Component comp : backgroundPanel.getComponents()) {
                    if (comp.isVisible()) {
                        currentPanel = comp;
                        break;
                    }
                }
                // 如果当前不是主菜单，则返回主菜单
                if (currentPanel != mainMenuPanel) {
                    SwingUtilities.invokeLater(this::showMainMenu);
                    return true; // 事件已处理
                }
            }
            return false; // 继续传递事件
        });
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
        settingsPanel.requestFocusInWindow();
    }

    public void startGame(String mode) {
        if (mode.equals("multiplayer")) {
            showMapSelectionForMultiplayer();   // 多人对战：选择地图
        } else if (mode.equals("level mode")) {
            showMapSelectionForLevelMode();      // 关卡模式：选择关卡
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

    private void showMapSelectionForMultiplayer() {
        MapManager mapManager = new MapManager();
        java.util.List<String> maps = mapManager.getAvailableMaps();

        if (maps.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "没有找到地图文件！\n请先使用地图编辑器创建地图。",
                    "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] mapArray = maps.toArray(new String[0]);
        String selectedMap = (String) JOptionPane.showInputDialog(this,
                "请选择地图:", "选择地图",
                JOptionPane.QUESTION_MESSAGE, null, mapArray, mapArray[0]);

        if (selectedMap != null) {
            startGameWithMap("multiplayer", selectedMap);
        }
    }

    private void showMapSelectionForLevelMode() {
        MapManager mapManager = new MapManager();
        java.util.List<String> levels = mapManager.getAvailableLevels();

        if (levels.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "没有找到关卡文件！\n请先使用地图编辑器创建关卡。",
                    "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] levelArray = levels.toArray(new String[0]);
        String selectedLevel = (String) JOptionPane.showInputDialog(this,
                "请选择关卡:", "选择关卡",
                JOptionPane.QUESTION_MESSAGE, null, levelArray, levelArray[0]);

        if (selectedLevel != null) {
            startGameWithMap("level mode", selectedLevel);
        }
    }

    private void startGameWithMap(String mode, String mapName) {
        setVisible(false);

        SwingUtilities.invokeLater(() -> {
            boolean isLevelMode = mode.equals("levelmode");
            GameWindow gameWindow = new GameWindow(isLevelMode, mapName);

            // 如果启动器当前处于全屏（最大化）状态，则游戏窗口也设为全屏
            if ((getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0) {
                gameWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }

            gameWindow.startGame();

            gameWindow.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    setVisible(true);
                }
            });
        });
    }

}