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
    public static final int SPAWN_P1 = 4;  // 添加
    public static final int SPAWN_P2 = 5;  // 添加
    private int[][] currentMap;

    // 存储路径（在src目录下）
    private final String mapsPath = "src/maps";
    private final String levelsPath = "src/levels";

    public MapManager() {
        currentMap = new int[20][20];
        createDirectories();
        // 检查默认地图是否存在，不存在则创建
        File defaultMapFile = new File(mapsPath + File.separator + "default.tankmap");
        if (!defaultMapFile.exists()) {
            createDefaultMap();
        }
    }

    /**
     * 创建默认地图
     */
    public void createDefaultMap() {
        // 创建40x40的地图
        int[][] defaultMap = new int[20][20];  // 注意：地图尺寸是40x40

        // 填充边界为砖墙
        for (int i = 0; i < 20; i++) {
            defaultMap[0][i] = WALL;           // 上边界
            defaultMap[19][i] = WALL;          // 下边界
            defaultMap[i][0] = WALL;           // 左边界
            defaultMap[i][19] = WALL;          // 右边界
        }

        // 添加一些障碍物
        for (int i = 0; i < 20; i++) {
            defaultMap[5][i] = WALL;
            defaultMap[14][i] = WALL;
        }

        // 添加树林
        defaultMap[10][10] = TREE;
        defaultMap[10][11] = TREE;
        defaultMap[11][10] = TREE;

        // 添加出生点
        defaultMap[18][5] = SPAWN_P1;   // 玩家1出生点
        defaultMap[18][14] = SPAWN_P2;  // 玩家2出生点

        // 保存地图
        saveMapToFile(defaultMap);
    }

    private void saveMapToFile(int[][] map) {
        File file = new File("src/maps/default.tankmap");
        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(map);
            System.out.println("创建默认地图: " + "src/maps/default.tankmap");
        } catch (IOException e) {
            System.err.println("创建默认地图失败: " + e.getMessage());
        }
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