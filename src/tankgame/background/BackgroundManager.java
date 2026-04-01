package tankgame.background;

import javax.swing.*;
import java.io.File;

/**
 * 背景图片管理器
 */
public class BackgroundManager {
    private ImageIcon backgroundImage;
    private String backgroundFolderPath;

    public BackgroundManager() {
        this.backgroundFolderPath = "E:\\JAVA\\Program1\\src\\resources\\background";
        loadBackground();
    }

    public BackgroundManager(String backgroundFolderPath) {
        this.backgroundFolderPath = backgroundFolderPath;
        loadBackground();
    }

    private void loadBackground() {
        File folder = new File(backgroundFolderPath);
        System.out.println("正在加载背景图片，路径：" + backgroundFolderPath);

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> {
                String lowerName = name.toLowerCase();
                return lowerName.endsWith(".gif") || lowerName.endsWith(".png") ||
                        lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg");
            });

            if (files != null && files.length > 0) {
                try {
                    System.out.println("找到图片文件：" + files[0].getName());
                    backgroundImage = new ImageIcon(files[0].getAbsolutePath());
                    System.out.println("背景图片加载成功！");
                } catch (Exception e) {
                    System.err.println("加载背景图片失败: " + e.getMessage());
                    backgroundImage = null;
                }
            } else {
                System.out.println("文件夹中没有找到图片文件，将使用渐变背景");
                backgroundImage = null;
            }
        } else {
            System.out.println("背景文件夹不存在，将使用渐变背景");
            backgroundImage = null;
        }
    }

    public ImageIcon getBackgroundImage() {
        return backgroundImage;
    }

    public boolean hasBackground() {
        return backgroundImage != null && backgroundImage.getImage() != null;
    }
}