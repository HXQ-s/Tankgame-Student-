package tankgame.ui;

import tankgame.GameLauncherFrame;
import tankgame.config.GameConfig;


import javax.swing.*;
import java.awt.*;

/**
 * 游戏设置面板
 */
public class SettingsPanel extends JPanel {
    private final GameLauncherFrame parent;
    private final GameConfig gameConfig;
    private JSlider volumeSlider;
    private JLabel volumeValue;
    private JComboBox<String> resolutionCombo;

    private static final Color TITLE_COLOR = new Color(255, 140, 0);
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 30);

    public SettingsPanel(GameLauncherFrame parent, GameConfig gameConfig) {
        this.parent = parent;
        this.gameConfig = gameConfig;
        setOpaque(false);
        setLayout(new GridBagLayout());
        initComponents();

        // 添加ESC键监听
        setFocusable(true);
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
                    parent.showMainMenu();  // 返回主菜单
                }
            }
        });

    }

    private void initComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 30, 15, 30);
        gbc.fill = GridBagConstraints.BOTH;          // 改为 BOTH，让滚动面板可以扩展
        gbc.weightx = 1.0;                          // 水平权重
        gbc.weighty = 1.0;                          // 垂直权重
        gbc.anchor = GridBagConstraints.CENTER;


        // 创建设置卡片
        JPanel settingsCard = createSettingsCard();
        // 将卡片放入滚动面板
        JScrollPane scrollPane = new JScrollPane(settingsCard);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 50, 20, 50);
        add(scrollPane, gbc);
    }

    private JPanel createSettingsCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setOpaque(true);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 210), 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 音量设置
        addVolumeControls(card, gbc);

        // 分辨率设置
        addResolutionControls(card, gbc);

        // 按键设置
        addKeyConfigControls(card, gbc);

        // 应用按钮
        addApplyButton(card, gbc);

        // 添加地图编辑器按钮
        gbc.gridy = 8;
        ModernButton mapEditorButton = new ModernButton("地图编辑器", new Color(100, 150, 200), 16);
        mapEditorButton.setPreferredSize(new Dimension(250, 50));
        mapEditorButton.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            tankgame.editor.MapEditor editor = new tankgame.editor.MapEditor();
            editor.setVisible(true);
        }));
        card.add(mapEditorButton, gbc);

        return card;
    }

    private void addVolumeControls(JPanel card, GridBagConstraints gbc) {
        gbc.gridy = 0;
        gbc.gridx = 0;
        JLabel volumeLabel = new JLabel("音量控制");
        volumeLabel.setFont(new Font("STXingkai", Font.BOLD, 20));
        volumeLabel.setForeground(new Color(60, 60, 80));
        card.add(volumeLabel, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        volumeSlider = new JSlider(0, 100, 70);
        volumeSlider.setPreferredSize(new Dimension(400, 50));
        volumeSlider.setMajorTickSpacing(25);
        volumeSlider.setMinorTickSpacing(5);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        card.add(volumeSlider, gbc);

        gbc.gridy = 2;
        volumeValue = new JLabel("70%", SwingConstants.CENTER);
        volumeValue.setFont(new Font("STXingkai", Font.BOLD, 24));
        volumeValue.setForeground(new Color(100, 149, 237));
        card.add(volumeValue, gbc);

        volumeSlider.addChangeListener(e ->
                volumeValue.setText(volumeSlider.getValue() + "%")
        );
    }

    private void addResolutionControls(JPanel card, GridBagConstraints gbc) {
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        JLabel resolutionLabel = new JLabel("显示设置");
        resolutionLabel.setFont(new Font("STXingkai", Font.BOLD, 20));
        resolutionLabel.setForeground(new Color(60, 60, 80));
        card.add(resolutionLabel, gbc);

        gbc.gridy = 4;
        resolutionCombo = new JComboBox<>(new String[]{
                "1024 × 768", "1280 × 720", "1366 × 768",
                "1600 × 900", "1920 × 1080", "全屏模式"
        });
        resolutionCombo.setFont(new Font("STXingkai", Font.PLAIN, 16));
        resolutionCombo.setPreferredSize(new Dimension(300, 40));
        card.add(resolutionCombo, gbc);
    }

    private void addKeyConfigControls(JPanel card, GridBagConstraints gbc) {
        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JLabel keysLabel = new JLabel("按键配置", SwingConstants.CENTER);
        keysLabel.setFont(new Font("STXingkai", Font.BOLD, 20));
        keysLabel.setForeground(new Color(60, 60, 80));
        card.add(keysLabel, gbc);

        JPanel keysPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        keysPanel.setOpaque(false);
        keysPanel.add(new KeyConfigPanel("玩家1", gameConfig.getPlayer1Keys(),
                new Color(100, 200, 100)));
        keysPanel.add(new KeyConfigPanel("玩家2", gameConfig.getPlayer2Keys(),
                new Color(100, 150, 255)));

        gbc.gridy = 6;
        gbc.insets = new Insets(20, 0, 20, 0);
        card.add(keysPanel, gbc);
    }

    private void addApplyButton(JPanel card, GridBagConstraints gbc) {
        gbc.gridy = 7;
        gbc.insets = new Insets(30, 0, 0, 0);
        ModernButton applyButton = new ModernButton("应用设置",
                new Color(100, 149, 237), 16);
        applyButton.setPreferredSize(new Dimension(250, 50));
        applyButton.addActionListener(e -> applySettings());
        card.add(applyButton, gbc);
    }

    private void applySettings() {
        Object selected = resolutionCombo.getSelectedItem();
        if (selected == null) return;
        String resolution = selected.toString();

        if ("全屏模式".equals(resolution)) {
            parent.setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            parent.setExtendedState(JFrame.NORMAL);
            String[] size = resolution.split(" × ");
            if (size.length == 2) {
                try {
                    int width = Integer.parseInt(size[0]);
                    int height = Integer.parseInt(size[1]);
                    parent.setSize(width, height);
                    parent.setLocationRelativeTo(null); // 居中
                    parent.validate(); // 强制重新布局
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(parent, "分辨率格式错误", "提示", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(parent, "分辨率格式错误", "提示", JOptionPane.WARNING_MESSAGE);
            }
        }

        // 可选：保存音量设置到 gameConfig
        gameConfig.setVolume(volumeSlider.getValue());

        JOptionPane.showMessageDialog(parent,
                "设置已保存！\n音量：" + volumeSlider.getValue() + "%\n分辨率：" + resolution,
                "提示", JOptionPane.INFORMATION_MESSAGE);

        // 应用设置后重新获取焦点
        // 可选：重新确认ESC监听（已经存在，无需重复添加）
        SwingUtilities.invokeLater(this::requestFocusInWindow);

    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setFont(new Font("STXingkai", Font.BOLD, 38));
        String title = "游戏设置";
        FontMetrics fm = g2d.getFontMetrics();
        int titleX = (getWidth() - fm.stringWidth(title)) / 2;
        int titleY = 60;

        g2d.setColor(SHADOW_COLOR);
        g2d.drawString(title, titleX + 3, titleY + 3);
        g2d.setColor(TITLE_COLOR);
        g2d.drawString(title, titleX, titleY);
    }
}
