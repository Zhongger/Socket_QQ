package cn.util;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class AudioUtils {

    private static AudioFormat af;
    private static Info info;
    private static TargetDataLine td;
    private static Info dataLineInfo;
    private static SourceDataLine sd;

    /**
     * 获取音频流数据(从拾音器)
     *
     * @return TargetDataLine
     * @throws LineUnavailableException
     */
    public static TargetDataLine getTargetDataLine() throws LineUnavailableException {
        if (td != null) {
            return td;
        } else {
            // 1.获取音频流数据
            // af为AudioFormat也就是音频格式
            af = getAudioFormat();
            info = new Info(TargetDataLine.class, af);
            // 这里的td实际上是
            td = (TargetDataLine) (AudioSystem.getLine(info));
            // 打开具有指定格式的行，这样可使行获得所有所需的系统资源并变得可操作。
            td.open(af);
            // 允许某一数据行执行数据 I/O
            td.start();
            return td;
        }

    }
    /**
     * 获取混编器 写入数据会自动播放
     *
     * @return SourceDataLine
     * @throws LineUnavailableException
     */
    public static SourceDataLine getSourceDataLine() throws LineUnavailableException {
        if (sd != null) {
            return sd;
        } else {
            // 2.从音频流获取数据
            dataLineInfo = new Info(SourceDataLine.class, af);
            sd = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            // 打开具有指定格式的行，这样可使行获得所有所需的系统资源并变得可操作。
            sd.open(af);
            // 允许某一数据行执行数据 I/O
            sd.start();

            return sd;
        }
    }

    /**
     * 设置AudioFormat的参数
     *
     * @return AudioFormat
     */
    public static AudioFormat getAudioFormat() {
        AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
        float rate = 8000f;
        int sampleSize = 16;
        String signedString = "signed";
        boolean bigEndian = true;
        int channels = 1;
        return new AudioFormat(encoding, rate, sampleSize, channels, (sampleSize / 8) * channels, rate, bigEndian);
    }

    /**
     * 关闭资源
     */
    public static void close() {
        if (td != null) {
            td.close();
        }
        if (sd != null) {
            sd.close();
        }

    }
}
