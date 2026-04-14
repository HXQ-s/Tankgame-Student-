package tankgame.game;

import javax.swing.*;

/**
 * 游戏主窗口
 */
public class GameWindow extends JFrame {
    private final GamePanel gamePanel;

    // 无参构造函数（兼容旧代码）
    public GameWindow() {
        this(false, null);
    }

    public GameWindow(boolean isLevelMode, String mapName) {
        setTitle(isLevelMode ? "坦克大战 - 关卡模式" : "坦克大战 - 多人对战");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        gamePanel = new GamePanel(isLevelMode, mapName);
        add(gamePanel);

        pack();
        setLocationRelativeTo(null);
    }

    public void startGame() {
        setVisible(true);
        gamePanel.requestFocusInWindow();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameWindow window = new GameWindow();
            window.startGame();
        });
    }


}