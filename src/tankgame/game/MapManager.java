package tankgame.game;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.awt.Point;

/**
 * 地图管理器 - 负责加载和管理地图
 */
public class MapManager {
    public static final int WALL = 1;
    public static final int IRON_WALL = 2;
    public static final int TREE = 3;
    public static final int SPAWN_P1 = 4;  // 添加
    public static final int SPAWN_P2 = 5;  // 添加
    private int[][] currentMap;
    // 添加或修改
    private static final int MAP_WIDTH = 20;
    private static final int MAP_HEIGHT = 20;

    // 存储路径（在src目录下）
    private final String mapsPath = System.getProperty("user.dir") + File.separator + "maps";
    private final String levelsPath = System.getProperty("user.dir") + File.separator + "levels";

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
        // 创建20x20的地图
        int[][] defaultMap = new int[20][20];  // 注意：地图尺寸是20x20

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
            if (loadedMap.length == MAP_HEIGHT && loadedMap[0].length == MAP_WIDTH) {
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

    /**
     * 获取玩家1出生点位置（像素坐标）
     */
    public Point getPlayer1Spawn() {
        return findSpawnPoint(SPAWN_P1);
    }

    /**
     * 获取玩家2出生点位置（像素坐标）
     */
    public Point getPlayer2Spawn() {
        return findSpawnPoint(SPAWN_P2);
    }

    /**
     * 查找出生点位置，返回像素坐标（单元格大小48px）
     */
    private Point findSpawnPoint(int spawnType) {
        for (int y = 0; y < currentMap.length; y++) {
            for (int x = 0; x < currentMap[y].length; x++) {
                if (currentMap[y][x] == spawnType) {
                    // 单元格大小为48像素
                    return new Point(x * 48, y * 48);
                }
            }
        }
        return null;
    }

    /**
     * 获取所有可用地图文件列表
     */
    public List<String> getAvailableMaps() {
        List<String> maps = new ArrayList<>();
        File mapsDir = new File(mapsPath);
        if (mapsDir.exists() && mapsDir.isDirectory()) {
            File[] files = mapsDir.listFiles((dir, name) -> name.endsWith(".tankmap"));
            if (files != null) {
                for (File file : files) {
                    String name = file.getName();
                    maps.add(name.substring(0, name.lastIndexOf(".tankmap")));
                }
            }
        }
        return maps;
    }

    /**
     * 获取所有可用关卡文件列表
     */
    public List<String> getAvailableLevels() {
        List<String> levels = new ArrayList<>();
        File levelsDir = new File(levelsPath);
        if (levelsDir.exists() && levelsDir.isDirectory()) {
            File[] files = levelsDir.listFiles((dir, name) -> name.endsWith(".level"));
            if (files != null) {
                for (File file : files) {
                    String name = file.getName();
                    levels.add(name.substring(0, name.lastIndexOf(".level")));
                }
            }
        }
        return levels;
    }

}