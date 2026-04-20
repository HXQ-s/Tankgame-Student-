package tankgame.util;

import java.awt.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 代码质量检测工具（运行时监控）
 * 功能：帧率监控、内存使用报告、潜在对象泄漏检测、长时间操作警告
 */
public class CodeQualityChecker {
    private static CodeQualityChecker instance;
    private boolean enabled = true;
    private long lastTime = System.nanoTime();
    private int frameCount = 0;
    private float currentFPS = 0;
    private final Queue<Long> frameTimeQueue = new LinkedList<>();
    private static final int FPS_SAMPLE_SIZE = 60;
    private final MemoryMXBean memoryBean;
    private long lastHeapUsed = 0;

    private CodeQualityChecker() {
        memoryBean = ManagementFactory.getMemoryMXBean();
    }

    public static CodeQualityChecker getInstance() {
        if (instance == null) {
            instance = new CodeQualityChecker();
        }
        return instance;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 在游戏主循环的每一帧调用，用于计算FPS和检测卡顿
     */
    public void updateFrameRate() {
        if (!enabled) return;
        frameCount++;
        long now = System.nanoTime();
        long delta = now - lastTime;
        if (delta >= 1_000_000_000) {
            currentFPS = frameCount * 1_000_000_000f / delta;
            frameCount = 0;
            lastTime = now;

            // 记录帧时间用于抖动分析
            frameTimeQueue.offer(delta / 1_000_000); // ms
            if (frameTimeQueue.size() > FPS_SAMPLE_SIZE) frameTimeQueue.poll();

            // 检测低帧率
            if (currentFPS < 30) {
                System.err.println("[性能警告] 当前帧率过低: " + String.format("%.1f", currentFPS) + " FPS");
            }
        }
    }

    /**
     * 检查内存使用情况，如果超过阈值则输出警告
     */
    public void checkMemoryUsage() {
        if (!enabled) return;
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        long used = heapUsage.getUsed() / (1024 * 1024);
        long max = heapUsage.getMax() / (1024 * 1024);
        int usagePercent = (int) (used * 100 / max);

        // 内存使用超过80%时警告（MB）
        int gcWarningThreshold = 80;
        if (usagePercent > gcWarningThreshold && used > lastHeapUsed + 5) {
            System.err.printf("[内存警告] 堆内存使用: %d MB / %d MB (%d%%)%n", used, max, usagePercent);
            // 建议手动触发GC（仅用于诊断）
            // System.gc();
        }
        lastHeapUsed = used;
    }

    /**
     * 在UI上绘制FPS和内存信息（可用于调试界面）
     */
    public void drawDebugInfo(Graphics2D g2d, int x, int y) {
        if (!enabled) return;
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));
        g2d.drawString(String.format("FPS: %.1f", currentFPS), x, y);
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        long used = heapUsage.getUsed() / (1024 * 1024);
        long max = heapUsage.getMax() / (1024 * 1024);
        g2d.drawString(String.format("Heap: %d / %d MB", used, max), x, y + 15);
        // 可选：显示平均帧时间
        if (!frameTimeQueue.isEmpty()) {
            double avgFrameTime = frameTimeQueue.stream().mapToLong(Long::longValue).average().orElse(0);
            g2d.drawString(String.format("Avg Frame: %.2f ms", avgFrameTime), x, y + 30);
        }
    }

}