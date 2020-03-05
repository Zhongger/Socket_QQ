package cn.Service;
import cn.util.AudioUtils;

import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 主要实现局域网通讯中服务端的功能
 *
 * @author Administrator
 */
public class AudioServer extends Thread{
    private OutputStream out;
    private InputStream in;
    private ServerSocket serverSocket;
    private Socket socket;
    //private int counter = 1;
    private byte[] bos=new byte[2024];
    //private static ByteArrayOutputStream baos;
    private  byte[] bis=new byte[2024];
    private int port;
    private boolean flag=true;
    public AudioServer(int port) {
        this.port=port;

    }

    @Override
    public void run() {
        startServer(this.port);
    }

    private void startServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            // 等待连接
            System.out.println("服务端:等待连接");
            socket = serverSocket.accept();
            out = socket.getOutputStream();
            // out.flush();
            System.out.println("服务端：连接成功");
            // 保持通讯
            in = socket.getInputStream();

            TargetDataLine targetDataLine = AudioUtils.getTargetDataLine();

            SourceDataLine sourceDataLine = AudioUtils.getSourceDataLine();
            while (flag) {
                System.out.println("server开始发了");

                /**
                 * 这里一定要先发再收  不然socket的读取流会阻塞
                 */

                //获取音频流
                int writeLen = targetDataLine.read(bos,0,bos.length);
                //发
                if (bos != null) {
                    //向对方发送拾音器获取到的音频
                    System.out.println("服务端发送音频");
                    out.write(bos,0,writeLen);
                }
                //收
                int readLen = in.read(bis);
                if (bis != null) {
                    //播放对方发送来的音频
                    System.out.println("服务端接收音频");
                    sourceDataLine.write(bis, 0, readLen);
                }
            }


        } catch (Exception ex) {
            Logger.getLogger(AudioServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}