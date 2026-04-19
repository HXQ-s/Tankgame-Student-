package tankgame.sound;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private static SoundManager instance;
    private final Map<String, Clip> soundClips = new HashMap<>();
    private Clip bgmClip;
    private float volume = 0.7f; // 0.0~1.0

    private SoundManager() {}

    public static SoundManager getInstance() {
        if (instance == null) instance = new SoundManager();
        return instance;
    }

    // 加载短音效（不循环）
    public void loadSound(String name, String filePath) {
        try {
            URL url = getClass().getResource(filePath);
            if (url == null) {
                System.err.println("音效文件不存在: " + filePath);
                return;
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            soundClips.put(name, clip);
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

    // 加载背景音乐（支持循环）
    public void loadBGM(String filePath) {
        try {
            URL url = getClass().getResource(filePath);
            if (url == null) {
                System.err.println("背景音乐不存在: " + filePath);
                return;
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(url);
            bgmClip = AudioSystem.getClip();
            bgmClip.open(audioStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 播放短音效（每次从头开始）
    public void playSound(String name) {
        Clip clip = soundClips.get(name);
        if (clip != null) {
            clip.setFramePosition(0);
            setClipVolume(clip, volume);
            clip.start();
        }
    }

    // 播放背景音乐（循环）
    public void playBGM() {
        boolean bgmMuted = false;
        if (bgmClip != null && !bgmMuted) {
            bgmClip.setFramePosition(0);
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
            setClipVolume(bgmClip, volume);
        }
    }

    // 设置全局音量
    public void setVolume(float volume) {
        this.volume = Math.max(0f, Math.min(1f, volume));
        for (Clip clip : soundClips.values()) {
            setClipVolume(clip, this.volume);
        }
        if (bgmClip != null) setClipVolume(bgmClip, this.volume);
    }

    private void setClipVolume(Clip clip, float volume) {
        if (clip == null) return;
        FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        if (gain != null) {
            float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            gain.setValue(Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), dB)));
        }
    }
}