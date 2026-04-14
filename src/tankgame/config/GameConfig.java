package tankgame.config;

/**
 * 游戏配置类
 */
public class GameConfig {
    private KeyConfig player1Keys;
    private KeyConfig player2Keys;
    private  int volume;
    private final String resolution;

    public GameConfig() {
        this.volume = 70;
        this.resolution = "1280 × 720";
    }

    public KeyConfig getPlayer1Keys() {
        return player1Keys;
    }

    public void setPlayer1Keys(KeyConfig player1Keys) {
        this.player1Keys = player1Keys;
    }

    public KeyConfig getPlayer2Keys() {
        return player2Keys;
    }

    public void setPlayer2Keys(KeyConfig player2Keys) {
        this.player2Keys = player2Keys;
    }

    @Override
    public String toString() {
        return "游戏配置:\n" +
                "玩家1: " + player1Keys + "\n" +
                "玩家2: " + player2Keys + "\n" +
                "音量: " + volume + "%\n" +
                "分辨率: " + resolution;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }
}