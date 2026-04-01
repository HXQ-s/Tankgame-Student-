package tankgame.game;

import javax.swing.*;

/**
 * 游戏主窗口
 */
public class GameWindow extends JFrame {
    private GamePanel gamePanel;

    public GameWindow() {
        setTitle("坦克大战 - 多人对战");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        gamePanel = new GamePanel();
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