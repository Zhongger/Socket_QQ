package cn.Service;

import cn.Model.ServerConfig;
import cn.View.AudioClient;
import cn.View.InputJPanel;
import cn.View.ReceiveVoiceChatPanel;
import cn.View.VoicingPanel;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SendVoiceRequest extends Thread implements MouseListener {
    private String myUsername,targetUsername;
    private Socket sendAudioRequestSocket;//连接发送请求的Socket
    private Socket receiveAudioSocket;//接收服务器发回数据的Socket

    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    public void setTargetUsername(String targetUsername) {
        this.targetUsername = targetUsername;
    }

    public SendVoiceRequest(String myUsername, String targetUsername, Socket receiveAudioSocket){
        this.myUsername=myUsername;
        this.targetUsername=targetUsername;
        this.receiveAudioSocket=receiveAudioSocket;

        try {
            sendAudioRequestSocket=new Socket(ServerConfig.getVoiceServerHost(),VoiceServer.receivePort);
            //第一次连接时，把自己的用户名发送过去
            outputStream = new DataOutputStream(sendAudioRequestSocket.getOutputStream());
            outputStream.writeUTF(myUsername);
            outputStream.flush();

        } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        try {
            System.out.println("被点击了");
            outputStream = new DataOutputStream(sendAudioRequestSocket.getOutputStream());
            inputStream = new DataInputStream(receiveAudioSocket.getInputStream());
            VoicingPanel voicingPanel = new VoicingPanel(targetUsername,InputJPanel.inputJPanel,null);
            new Thread(){
                @Override
                public void run() {
                    try {
                        outputStream.writeUTF("voice&&"+myUsername+"&&"+targetUsername);//将请求消息发送给服务端
                        outputStream.flush();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                }
            }.start();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {}
}