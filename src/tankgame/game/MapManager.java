package tankgame.game;

import java.io.*;

/**
 * 地图管理器 - 负责加载和管理地图
 */
public class MapManager {
    // 障碍物类型常量
    public static final int EMPTY = 0;
    public static final int WALL = 1;
    public static final int IRON_WALL = 2;
    public static final int TREE = 3;

    private int[][] currentMap;

    // 存储路径（在src目录下）
    private final String mapsPath = "src/maps";
    private final String levelsPath = "src/levels";

    public MapManager() {
        currentMap = new int[20][20];
        createDirectories();
    }

    private void createDirectories() {
        File mapsDir = new File(mapsPath);
        File levelsDir = new File(levelsPath);

        if (!mapsDir.exists()) {
            mapsDir.mkdirs();
        }
        if (!levelsDir.exists()) {
            levelsDir.mkdirs();
        }
    }

    /**
     * 加载地图文件
     */
    public boolean loadMap(String fileName) {
        String filePath = mapsPath + File.separator + fileName + ".tankmap";
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            int[][] loadedMap = (int[][]) ois.readObject();
            if (loadedMap.length == 20 && loadedMap[0].length == 20) {
                currentMap = loadedMap;
                System.out.println("地图加载成功: " + fileName);
                return true;
            }
        } catch (Exception e) {
            System.err.println("加载地图失败: " + e.getMessage());
        }
        return false;
    }

    /**
     * 加载关卡
     */
    public void loadLevel(String levelName) {
        String filePath = levelsPath + File.separator + levelName + ".level";
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            int[][] loadedMap = (int[][]) ois.readObject();
            if (loadedMap.length == 20 && loadedMap[0].length == 20) {
                currentMap = loadedMap;
                System.out.println("关卡加载成功: " + levelName);
            }
        } catch (Exception e) {
            System.err.println("加载关卡失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前地图数据
     */
    public int[][] getCurrentMap() {
        return currentMap;
    }

}