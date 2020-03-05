package cn.View;


import cn.util.AudioUtils;

import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class AudioClient extends Thread{
    private OutputStream out;
    private InputStream in;
    private Socket socket;
    private String ip;
    private int port;
    private byte[] bos=new byte[2024];
    //private static ByteArrayOutputStream baos;
    private static byte[] bis=new byte[2024];
    private boolean flag=true;
    public AudioClient(String ip, int port, Socket socket){
        this.ip=ip;
        this.port=port;
        this.socket=socket;

    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    @Override
    public void run() {
        startClient(ip,port,socket);
    }

    private void startClient(String ip, int port,Socket socket) {
        try {
            //这里需要根据自己的ip修改
            System.out.println("到了这里");
            //socket = new Socket(ip, port);
            System.out.println(ip);
            System.out.println(port);
            System.out.println(socket);
            out = socket.getOutputStream();
            System.out.println("客户端:连接成功");
            // 保持通讯
            in = socket.getInputStream();
            TargetDataLine targetDataLine = AudioUtils.getTargetDataLine();
            SourceDataLine sourceDataLine = AudioUtils.getSourceDataLine();
            while (flag) {
                System.out.println("Client:");

                //获取音频流
                int writeLen = targetDataLine.read(bos,0,bos.length);
                //发
                if (bos != null) {
                    //向对方发送拾音器获取到的音频
                    System.out.println("Client 发");
                    out.write(bos,0,writeLen);
                }
                //收
                int readLen = in.read(bis);
                if (bis != null) {
                    //播放对方发送来的音频
                    System.out.println("Client 收");
                    sourceDataLine.write(bis, 0, readLen);
                }

            }

        } catch (Exception ex) {
            Logger.getLogger(AudioClient.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

}