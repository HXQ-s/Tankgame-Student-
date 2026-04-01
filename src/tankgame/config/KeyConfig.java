package tankgame.config;

import java.awt.event.KeyEvent;

/**
 * 按键配置类
 */
public class KeyConfig {
    public int up, down, left, right, shoot;

    public KeyConfig(int up, int down, int left, int right, int shoot) {
        this.up = up;
        this.down = down;
        this.left = left;
        this.right = right;
        this.shoot = shoot;
    }

    @Override
    public String toString() {
        return String.format("↑:%s ↓:%s ←:%s →:%s ●:%s",
                KeyEvent.getKeyText(up), KeyEvent.getKeyText(down),
                KeyEvent.getKeyText(left), KeyEvent.getKeyText(right),
                KeyEvent.getKeyText(shoot));
    }
}