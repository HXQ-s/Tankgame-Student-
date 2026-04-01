package tankgame.ui;

import tankgame.config.KeyConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * 按键配置面板
 */
public class KeyConfigPanel extends JPanel {
    private final String playerName;
    private final KeyConfig keyConfig;
    private final Color accentColor;

    public KeyConfigPanel(String playerName, KeyConfig keyConfig, Color accentColor) {
        this.playerName = playerName;
        this.keyConfig = keyConfig;
        this.accentColor = accentColor;
        setOpaque(false);
        setLayout(new GridBagLayout());
        initComponents();
    }

    private void initComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 玩家标题
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel nameLabel = new JLabel(playerName, SwingConstants.CENTER);
        nameLabel.setFont(new Font("STXingkai", Font.BOLD, 18));
        nameLabel.setForeground(accentColor);
        add(nameLabel, gbc);

        gbc.gridwidth = 1;

        // 添加各个按键配置
        addKeyButton(gbc, "↑ 上移:", "up", KeyEvent.getKeyText(keyConfig.up), 1);
        addKeyButton(gbc, "↓ 下移:", "down", KeyEvent.getKeyText(keyConfig.down), 2);
        addKeyButton(gbc, "← 左移:", "left", KeyEvent.getKeyText(keyConfig.left), 3);
        addKeyButton(gbc, "→ 右移:", "right", KeyEvent.getKeyText(keyConfig.right), 4);
        addKeyButton(gbc, "● 射击:", "shoot", KeyEvent.getKeyText(keyConfig.shoot), 5);

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 210), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
    }

    private void addKeyButton(GridBagConstraints gbc, String labelText,
                              String action, String keyText, int row) {
        gbc.gridy = row;

        gbc.gridx = 0;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("STXingkai", Font.PLAIN, 14));
        add(label, gbc);

        gbc.gridx = 1;
        JButton button = new JButton(keyText);
        button.setFont(new Font("STXingkai", Font.BOLD, 13));
        button.setBackground(new Color(240, 240, 245));
        button.setForeground(new Color(60, 60, 80));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 210), 1));
        button.setPreferredSize(new Dimension(100, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addActionListener(e -> showKeyDialog(button, action));
        add(button, gbc);
    }

    private void showKeyDialog(JButton button, String action) {
        Window owner = SwingUtilities.getWindowAncestor(this);
        if (owner == null) {
            return;
        }

        JDialog keyDialog = new JDialog((Frame) owner, "设置按键", true);
        keyDialog.setSize(350, 180);
        keyDialog.setLocationRelativeTo(owner);
        keyDialog.setLayout(new BorderLayout());

        // 创建内容面板
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel("请按下新的按键...", SwingConstants.CENTER);
        label.setFont(new Font("STXingkai", Font.PLAIN, 16));
        label.setForeground(new Color(60, 60, 80));
        contentPanel.add(label, BorderLayout.CENTER);

        JLabel tipLabel = new JLabel("提示：按 ESC 取消设置", SwingConstants.CENTER);
        tipLabel.setFont(new Font("STXingkai", Font.PLAIN, 12));
        tipLabel.setForeground(new Color(150, 150, 160));
        contentPanel.add(tipLabel, BorderLayout.SOUTH);

        keyDialog.add(contentPanel);

        // 修复键盘监听问题 - 使用 KeyBinding 或确保对话框可聚焦
        keyDialog.setFocusable(true);
        keyDialog.setFocusTraversalKeysEnabled(false); // 禁用焦点遍历

        // 添加键盘监听
        keyDialog.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (keyCode != KeyEvent.VK_ESCAPE) {
                    updateKeyConfig(action, keyCode);
                    button.setText(KeyEvent.getKeyText(keyCode));
                    keyDialog.dispose();
                } else {
                    keyDialog.dispose();
                }
            }
        });

        // 确保对话框可以获得焦点
        keyDialog.requestFocusInWindow();

        // 添加一个组件点击监听，确保焦点在对话框上
        contentPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                keyDialog.requestFocusInWindow();
            }
        });

        keyDialog.setVisible(true);
    }

    private void updateKeyConfig(String action, int keyCode) {
        switch (action) {
            case "up":
                keyConfig.up = keyCode;
                break;
            case "down":
                keyConfig.down = keyCode;
                break;
            case "left":
                keyConfig.left = keyCode;
                break;
            case "right":
                keyConfig.right = keyCode;
                break;
            case "shoot":
                keyConfig.shoot = keyCode;
                break;
        }
    }
}