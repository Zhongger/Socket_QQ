package cn.View;

import cn.Model.ServerConfig;
import cn.Service.AudioServer;
import cn.Service.VoiceServer;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class ReceiveVoiceChatPanel extends JFrame {
    private Container container;
    private JPanel requestJpanel;
    private VoiceImageJpanel voiceImageJpanel;
    private JPanel buttonJpanel;
    private JLabel requestMsgJLabel;
    private JButton accepetJbutton;
    private JButton refuseJbutton;
    private ReceiveVoiceChatPanel receiveVoiceChatPanel;

    public ReceiveVoiceChatPanel(String requesterUsername,String targetUsername){
        receiveVoiceChatPanel=this;
        container=this.getContentPane();
        container.setLayout(new BorderLayout());

        requestJpanel=new JPanel();
        requestMsgJLabel=new JLabel(requesterUsername+"邀请你语音聊天");
        requestJpanel.add(requestMsgJLabel);
        VoicingPanel voicingPanel = new VoicingPanel(targetUsername,InputJPanel.inputJPanel,AcceptListener.acceptListener);
        voiceImageJpanel=new VoiceImageJpanel();

        buttonJpanel=new JPanel();
        buttonJpanel.setLayout(new FlowLayout());
        voice_warn();

        accepetJbutton=new JButton("接听");
        accepetJbutton.addActionListener(new AcceptListener(accepetJbutton,requesterUsername,receiveVoiceChatPanel));

        refuseJbutton=new JButton("拒绝");
        refuseJbutton.addActionListener(new RefuseListener(refuseJbutton,requesterUsername,receiveVoiceChatPanel));
        buttonJpanel.add(accepetJbutton);
        buttonJpanel.add(refuseJbutton);

        container.add(requestJpanel,BorderLayout.NORTH);
        container.add(voiceImageJpanel,BorderLayout.CENTER);
        container.add(buttonJpanel,BorderLayout.SOUTH);

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setVisible(true);
        this.setSize(263,342);
        GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle rect=ge.getMaximumWindowBounds();
        int w=rect.width;
        int h=rect.height;
        this.setLocation(w-263,h-342);

    }
    public static void voice_warn(){
        try {
            FileInputStream fileau = new FileInputStream("D:\\Socket\\tubiao\\voiceRequest.wav");
            AudioStream as = new AudioStream(fileau);
            AudioPlayer.player.start(as);     }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


}

class AcceptListener implements ActionListener{
    private JButton acceptJbutton;
    private String requesterUsername;
    private ReceiveVoiceChatPanel receiveVoiceChatPanel;
    public static AudioServer audioServer;
    public static AcceptListener acceptListener;
    private Random random=new Random();
    public AcceptListener(JButton acceptJbutton,String requesterUsername,ReceiveVoiceChatPanel receiveVoiceChatPanel){
        this.acceptJbutton=acceptJbutton;
        this.requesterUsername=requesterUsername;
        this.receiveVoiceChatPanel=receiveVoiceChatPanel;
        acceptListener=this;

    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==acceptJbutton){
            //获取当前ip并随机生成端口号
            try {
                InetAddress localHost = InetAddress.getLocalHost();
                String localHostString = localHost.toString();
                String[] strings = localHostString.split("/");
                String ip = strings[1];
                System.out.println("ip"+ip);
                //端口号范围在55~60
                int port=random.nextInt(6)+55;

                Socket sendSocket = new Socket(ServerConfig.getVoiceServerHost(), VoiceServer.receivePort);
                Socket receiveSocket = new Socket(ServerConfig.getVoiceServerHost(), VoiceServer.pushPort);
                System.out.println(sendSocket);
                DataOutputStream dataOutputStream = new DataOutputStream(sendSocket.getOutputStream());
                dataOutputStream.writeUTF(ip+"&&"+port+"&&"+requesterUsername+"&&true");
                dataOutputStream.flush();
                System.out.println("发送给服务器成功");
                //被请求方成为音频服务器
                InputJPanel.setVoiceFlag(true);
                audioServer = new AudioServer(port);
                audioServer.start();
                receiveVoiceChatPanel.setVisible(false);
                System.out.println("语音来电提醒对象已隐藏");
                VoicingPanel voicingPanel = new VoicingPanel(requesterUsername,InputJPanel.inputJPanel,this);

            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
    public void closeAudioServer(){
        audioServer.setFlag(false);
    }
}

class RefuseListener implements ActionListener{
    private JButton refuseJbutton;
    private String requesterUsername;
    private ReceiveVoiceChatPanel receiveVoiceChatPanel;


    public RefuseListener(JButton refuseJbutton,String requesterUsername,ReceiveVoiceChatPanel receiveVoiceChatPanel){
        this.refuseJbutton=refuseJbutton;
        this.requesterUsername=requesterUsername;
        this.receiveVoiceChatPanel=receiveVoiceChatPanel;

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==refuseJbutton){

            try {
                Socket sendSocket = new Socket(ServerConfig.getVoiceServerHost(), VoiceServer.receivePort);
                Socket receiveSocket = new Socket(ServerConfig.getVoiceServerHost(), VoiceServer.pushPort);
                DataOutputStream dataOutputStream = new DataOutputStream(sendSocket.getOutputStream());
                dataOutputStream.writeUTF(requesterUsername+"&&false");
                dataOutputStream.flush();
                receiveVoiceChatPanel.dispose();
                System.out.println("发送给服务器成功");
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }
    }
}

class VoiceImageJpanel extends JPanel{
    ImageIcon imageIcon;
    Image image;
    public VoiceImageJpanel(){
        imageIcon=new ImageIcon(getClass().getResource("/img/voice.jpg"));
        image=imageIcon.getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0,this.getWidth(), this.getHeight(), this);
    }
}
