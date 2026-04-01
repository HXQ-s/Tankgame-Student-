package tankgame;

import javax.swing.*;

/**
 * 坦克大战游戏启动器 - 主入口类
 */
public class GameLauncher {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            GameLauncherFrame frame = new GameLauncherFrame();
            frame.setVisible(true);
        });
    }
}