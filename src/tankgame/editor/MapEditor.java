package tankgame.editor;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
// 添加这行

/**
 * 地图编辑器主界面
 */
public class MapEditor extends JFrame {
    // 地图尺寸
    private static final int MAP_WIDTH = 20;    // 20x20网格
    private static final int MAP_HEIGHT = 20;
    private static final int CELL_SIZE = 48;    // 每个格子48x48像素

    // 障碍物类型
    public static final int EMPTY = 0;
    public static final int WALL = 1;
    public static final int IRON_WALL = 2;
    public static final int TREE = 3;
    public static final int SPAWN_P1 = 4;  // 玩家1出生点
    public static final int SPAWN_P2 = 5;  // 玩家2出生点

    // 材质图片
    private Image wallImage;
    private Image ironWallImage;
    private Image treeImage;

    // 地图数据
    private int[][] mapData;

    // UI组件
    private MapPanel mapPanel;
    private JComboBox<String> toolComboBox;
    private JLabel statusLabel;
    private String currentMapPath = null;

    // 存储路径（在src目录下）
    private final String mapsPath = System.getProperty("user.dir") + File.separator + "maps";
    private final String levelsPath = System.getProperty("user.dir") + File.separator + "levels";

    public MapEditor() {
        setTitle("坦克大战 - 地图编辑器");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // 创建目录
        createDirectories();

        // 加载材质图片
        loadTextures();

        // 初始化地图数据
        mapData = new int[MAP_HEIGHT][MAP_WIDTH];

        // 初始化UI
        initUI();

        // 新建空白地图
        newMap();
    }

    private void createDirectories() {
        File mapsDir = new File(mapsPath);
        File levelsDir = new File(levelsPath);

        if (!mapsDir.exists()) {
            mapsDir.mkdirs();
            System.out.println("创建地图目录: " + mapsDir.getAbsolutePath());
        }
        if (!levelsDir.exists()) {
            levelsDir.mkdirs();
            System.out.println("创建关卡目录: " + levelsDir.getAbsolutePath());
        }
    }

    private void loadTextures() {
        String texturePath = "src/resources/texture";
        try {
            wallImage = new ImageIcon(texturePath + File.separator + "wall.png").getImage();
            ironWallImage = new ImageIcon(texturePath + File.separator + "iron_wall.png").getImage();
            treeImage = new ImageIcon(texturePath + File.separator + "tree.png").getImage();
        } catch (Exception e) {
            System.err.println("加载材质图片失败: " + e.getMessage());
        }

        java.net.URL url = getClass().getResource("/tank/tank_green.png");
        System.out.println("坦克图片URL: " + url);
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // 创建工具栏
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        // 工具选择
        toolBar.add(new JLabel("绘制工具: "));
        String[] tools = {"清除", "砖墙", "钢墙", "树林", "玩家1出生点", "玩家2出生点"};
        toolComboBox = new JComboBox<>(tools);
        toolComboBox.setPreferredSize(new Dimension(100, 30));
        toolBar.add(toolComboBox);

        toolBar.addSeparator();

        // 按钮
        JButton newButton = new JButton("新建");
        newButton.addActionListener(e -> newMap());
        toolBar.add(newButton);

        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(e -> saveMap());
        toolBar.add(saveButton);

        JButton loadButton = new JButton("加载");
        loadButton.addActionListener(e -> loadMap());
        toolBar.add(loadButton);

        JButton saveToLevelButton = new JButton("保存为关卡");
        saveToLevelButton.addActionListener(e -> saveToLevel());
        toolBar.add(saveToLevelButton);

        toolBar.addSeparator();

        JButton clearButton = new JButton("清空地图");
        clearButton.addActionListener(e -> clearMap());
        toolBar.add(clearButton);

        JButton fillButton = new JButton("填充边界");
        fillButton.addActionListener(e -> fillBorder());
        toolBar.add(fillButton);

        add(toolBar, BorderLayout.NORTH);

        // 地图面板
        mapPanel = new MapPanel();
        add(mapPanel, BorderLayout.CENTER);

        // 状态栏
        statusLabel = new JLabel("就绪");
        statusLabel.setBorder(BorderFactory.createEtchedBorder());
        add(statusLabel, BorderLayout.SOUTH);

        // 添加快捷键
        setupKeyboardShortcuts();
    }

    private void setupKeyboardShortcuts() {
        // Ctrl+S 保存
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "save");
        getRootPane().getActionMap().put("save", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveMap();
            }
        });

        // Ctrl+O 加载
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK), "load");
        getRootPane().getActionMap().put("load", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadMap();
            }
        });

        // Ctrl+N 新建
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "new");
        getRootPane().getActionMap().put("new", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newMap();
            }
        });
    }

    private void newMap() {
        // 直接清空，不询问
        for (int i = 0; i < MAP_HEIGHT; i++) {
            for (int j = 0; j < MAP_WIDTH; j++) {
                mapData[i][j] = EMPTY;
            }
        }
        currentMapPath = null;
        mapPanel.repaint();
        statusLabel.setText("已创建新地图");
    }

    private void clearMap() {
        for (int i = 0; i < MAP_HEIGHT; i++) {
            for (int j = 0; j < MAP_WIDTH; j++) {
                mapData[i][j] = EMPTY;
            }
        }
        mapPanel.repaint();
        statusLabel.setText("地图已清空");
    }

    private void fillBorder() {
        // 填充边界为砖墙
        for (int i = 0; i < MAP_HEIGHT; i++) {
            for (int j = 0; j < MAP_WIDTH; j++) {
                if (i == 0 || i == MAP_HEIGHT - 1 || j == 0 || j == MAP_WIDTH - 1) {
                    mapData[i][j] = WALL;
                }
            }
        }
        mapPanel.repaint();
        statusLabel.setText("已添加边界围墙");
    }

    private void saveMap() {
        JFileChooser fileChooser = new JFileChooser(mapsPath);
        fileChooser.setFileFilter(new FileNameExtensionFilter("坦克大战地图文件", "tank-map"));

        if (currentMapPath != null) {
            fileChooser.setSelectedFile(new File(currentMapPath));
        }

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String path = file.getAbsolutePath();
            if (!path.endsWith(".tankmap")) {
                path += ".tankmap";
                file = new File(path);
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(mapData);
                currentMapPath = path;
                statusLabel.setText("地图已保存: " + file.getName());
                JOptionPane.showMessageDialog(this, "地图保存成功！\n路径: " + file.getAbsolutePath(),
                        "成功", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "保存失败: " + e.getMessage(),
                        "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadMap() {
        JFileChooser fileChooser = new JFileChooser(mapsPath);
        fileChooser.setFileFilter(new FileNameExtensionFilter("坦克大战地图文件", "tank-map"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                int[][] loadedMap = (int[][]) ois.readObject();
                if (loadedMap.length == MAP_HEIGHT && loadedMap[0].length == MAP_WIDTH) {
                    mapData = loadedMap;
                    currentMapPath = file.getAbsolutePath();
                    mapPanel.repaint();
                    statusLabel.setText("地图已加载: " + file.getName());
                } else {
                    JOptionPane.showMessageDialog(this, "地图尺寸不匹配！",
                            "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "加载失败: " + e.getMessage(),
                        "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveToLevel() {
        String levelName = JOptionPane.showInputDialog(this, "请输入关卡名称:", "保存关卡");
        if (levelName != null && !levelName.trim().isEmpty()) {
            File levelFile = new File(levelsPath, levelName + ".level");
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(levelFile))) {
                oos.writeObject(mapData);
                statusLabel.setText("关卡已保存: " + levelName);
                JOptionPane.showMessageDialog(this, "关卡保存成功！\n路径: " + levelFile.getAbsolutePath(),
                        "成功", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "保存失败: " + e.getMessage(),
                        "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 地图绘制面板
     */
    private class MapPanel extends JPanel {
        private int selectedX = -1, selectedY = -1;

        public MapPanel() {
            setBackground(Color.DARK_GRAY);
            setPreferredSize(new Dimension(MAP_WIDTH * CELL_SIZE, MAP_HEIGHT * CELL_SIZE));

            // 鼠标绘制
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    int x = e.getX() / CELL_SIZE;
                    int y = e.getY() / CELL_SIZE;
                    if (x >= 0 && x < MAP_WIDTH && y >= 0 && y < MAP_HEIGHT) {
                        // 鼠标左键：放置障碍物
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            drawCell(x, y);
                            selectedX = x;
                            selectedY = y;
                        }
                        // 鼠标右键：删除障碍物
                        else if (SwingUtilities.isRightMouseButton(e)) {
                            deleteCell(x, y);
                            selectedX = x;
                            selectedY = y;
                        }
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    selectedX = -1;
                    selectedY = -1;
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    int x = e.getX() / CELL_SIZE;
                    int y = e.getY() / CELL_SIZE;
                    if (x >= 0 && x < MAP_WIDTH && y >= 0 && y < MAP_HEIGHT) {
                        if (x != selectedX || y != selectedY) {
                            drawCell(x, y);
                            selectedX = x;
                            selectedY = y;
                        }
                    }
                }
            });
        }

        private void drawCell(int x, int y) {
            int tool = toolComboBox.getSelectedIndex();
            // 检查玩家1出生点数量限制
            if (tool == SPAWN_P1 && countSpawnPoints(SPAWN_P1) >= 1) {
                statusLabel.setText("玩家1出生点只能有一个！");
                return;
            }
            // 检查玩家2出生点数量限制
            if (tool == SPAWN_P2 && countSpawnPoints(SPAWN_P2) >= 1) {
                statusLabel.setText("玩家2出生点只能有一个！");
                return;
            }
            mapData[y][x] = tool;
            repaint();
            statusLabel.setText(String.format("在 (%d, %d) 放置: %s",
                    x, y, toolComboBox.getSelectedItem()));
        }

        /**
         * 删除障碍物（鼠标右键）
         */
        private void deleteCell(int x, int y) {
            // 只删除障碍物，不删除出生点
            if (mapData[y][x] != SPAWN_P1 && mapData[y][x] != SPAWN_P2) {
                mapData[y][x] = EMPTY;
                repaint();
                statusLabel.setText(String.format("在 (%d, %d) 已删除", x, y));
            } else {
                statusLabel.setText("出生点不能删除，请使用清除工具");
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            // 绘制网格
            g2d.setColor(new Color(100, 100, 100));
            for (int i = 0; i <= MAP_WIDTH; i++) {
                g2d.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, getHeight());
            }
            for (int i = 0; i <= MAP_HEIGHT; i++) {
                g2d.drawLine(0, i * CELL_SIZE, getWidth(), i * CELL_SIZE);
            }

            // 绘制障碍物
            for (int y = 0; y < MAP_HEIGHT; y++) {
                for (int x = 0; x < MAP_WIDTH; x++) {
                    int type = mapData[y][x];
                    Image image = switch (type) {
                        case WALL -> wallImage;
                        case IRON_WALL -> ironWallImage;
                        case TREE -> treeImage;
                        default -> null;
                    };

                    if (image != null) {
                        g2d.drawImage(image, x * CELL_SIZE, y * CELL_SIZE,
                                CELL_SIZE, CELL_SIZE, null);
                    } else if (type == EMPTY) {
                        // 绘制半透明网格
                        g2d.setColor(new Color(50, 50, 50, 50));
                        g2d.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    }
                    else if (type == SPAWN_P1) {
                        // 绘制玩家1出生点（绿色圆）
                        g2d.setColor(new Color(0, 255, 0, 100));
                        g2d.fillOval(x * CELL_SIZE + 4, y * CELL_SIZE + 4, CELL_SIZE - 8, CELL_SIZE - 8);
                        g2d.setColor(Color.GREEN);
                        g2d.drawOval(x * CELL_SIZE + 4, y * CELL_SIZE + 4, CELL_SIZE - 8, CELL_SIZE - 8);
                        g2d.setFont(new Font("Arial", Font.BOLD, 12));
                        g2d.drawString("P1", x * CELL_SIZE + 8, y * CELL_SIZE + 18);
                    }
                    else if (type == SPAWN_P2) {
                        // 绘制玩家2出生点（蓝色圆）
                        g2d.setColor(new Color(0, 0, 255, 100));
                        g2d.fillOval(x * CELL_SIZE + 4, y * CELL_SIZE + 4, CELL_SIZE - 8, CELL_SIZE - 8);
                        g2d.setColor(Color.BLUE);
                        g2d.drawOval(x * CELL_SIZE + 4, y * CELL_SIZE + 4, CELL_SIZE - 8, CELL_SIZE - 8);
                        g2d.setFont(new Font("Arial", Font.BOLD, 12));
                        g2d.drawString("P2", x * CELL_SIZE + 8, y * CELL_SIZE + 18);
                    }
                }
            }

            // 绘制坐标提示
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("行楷", Font.PLAIN, 12));
            for (int i = 0; i < MAP_WIDTH; i++) {
                g2d.drawString(String.valueOf(i), i * CELL_SIZE + 5, 15);
            }
            for (int i = 0; i < MAP_HEIGHT; i++) {
                g2d.drawString(String.valueOf(i), 5, i * CELL_SIZE + 20);
            }
        }
    }
    // 统计出生点数量
    private int countSpawnPoints(int type) {
        int count = 0;
        for (int i = 0; i < MAP_HEIGHT; i++) {
            for (int j = 0; j < MAP_WIDTH; j++) {
                if (mapData[i][j] == type) count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MapEditor().setVisible(true));
    }
}