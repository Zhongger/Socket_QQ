package cn.View;

import cn.Service.SendMsg;
import cn.Service.SendVoiceRequest;
import cn.View.chatjpanel.ChatPanel;
import cn.View.chatjpanel.listener.Btn_SendFileListener;
import cn.util.Filetools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InputJPanel extends JPanel {

    private static JTextArea inputJTextArea;
    private JScrollPane inputJScrollPane;
    private JPanel inputJP_function;
    private JLabel send_file,voice_chat,video_chat,sendnews_JLabel;
    public static AudioClient audioClient;
    private Date date;
    private String current_username=null,name;
    public static boolean voiceFlag=false;
    public Filetools filetool;
    public SimpleDateFormat format;
    public ClientMainFrame clientMainFrame;
    public static InputJPanel inputJPanel;
    public Btn_SendFileListener listener;
    public SendVoiceRequest sendVoiceRequest;
    public ImageIcon icon1 = new ImageIcon("D:\\Socket\\tubiao\\ziyuan.png");
    public ImageIcon icon2 = new ImageIcon("D:\\Socket\\tubiao\\yuyin.png");
    public ImageIcon icon3 = new ImageIcon("D:\\Socket\\tubiao\\shipin2.png");
    public static void setVoiceFlag(boolean voiceFlag) {
        InputJPanel.voiceFlag = voiceFlag;
    }
    public InputJPanel(String name,ClientMainFrame clientMainFrame){
       this.name=name;
       inputJPanel=this;
       inputJPanel.clientMainFrame = clientMainFrame;
       filetool = new Filetools(" ");
       format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

       send_file = new JLabel();
       send_file.setBounds(28,15,17,14);
       icon1.setImage(icon1.getImage().getScaledInstance(17,14,Image.SCALE_SMOOTH));
       send_file.setIcon(icon1);
       listener = new Btn_SendFileListener(name,current_username,inputJPanel);
       send_file.addMouseListener(listener);

       voice_chat = new JLabel();
       voice_chat.setBounds(468,15,19,17);
       icon2.setImage(icon2.getImage().getScaledInstance(19,17,Image.SCALE_SMOOTH));
       voice_chat.setIcon(icon2);

       sendVoiceRequest = new SendVoiceRequest(name, current_username, ClientMainFrame.receiveAudioSocket);
       sendVoiceRequest.start();
       voice_chat.addMouseListener(sendVoiceRequest);


       video_chat = new JLabel();
       video_chat.setBounds(497,14,21,21);
       icon3.setImage(icon3.getImage().getScaledInstance(21,21,Image.SCALE_SMOOTH));
       video_chat.setIcon(icon3);
       video_chat.addMouseListener(new MouseAdapter() {
           public void mouseEntered(MouseEvent e) {
               setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
           }

           public void mouseExited(MouseEvent e) {
               setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
           }
       });


       inputJP_function = new JPanel();
       inputJP_function.add(send_file);
       inputJP_function.add(voice_chat);
       inputJP_function.add(video_chat);
       inputJP_function.setBounds(0,0,555,38);
       inputJP_function.setBorder(null);
       inputJP_function.setLayout(null);
       inputJP_function.setBackground(Color.white);

       inputJTextArea = new JTextArea();
       inputJTextArea.setLineWrap(true);
       Font font = new Font("微软雅黑", Font.PLAIN, 14);
       inputJTextArea.setFont(font);
       inputJScrollPane = new JScrollPane(inputJTextArea);
       inputJScrollPane.setBounds(0, 38, 525, 62);
       inputJScrollPane.setBorder(null);
       inputJScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

       sendnews_JLabel = new JLabel("发送(S)",JLabel.CENTER);
       sendnews_JLabel.setBackground(new Color(243, 243 ,243));
       Font font2 = new Font("微软雅黑", Font.PLAIN, 14);
       sendnews_JLabel.setFont(font2);
       sendnews_JLabel.setForeground(Color.black);
       sendnews_JLabel.addMouseListener(new MouseAdapter() {
           public void mouseEntered(MouseEvent e) {
               setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
               sendnews_JLabel.setBackground(new Color(25, 170 ,30));
               sendnews_JLabel.setForeground(Color.white);
           }
           public void mouseExited(MouseEvent e) {
               setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
               sendnews_JLabel.setBackground(new Color(243, 243 ,243));
               sendnews_JLabel.setForeground(Color.black);
           }

           @Override
           public void mouseClicked(MouseEvent e)  {
                   super.mouseClicked(e);
                   String text = inputJTextArea.getText();
                   //trim方法为去掉两端空格符
                   if (text.trim().length()!=0) {
                       SendMsg msg = new SendMsg(clientMainFrame.getSendSocket(), "text", current_username, text,clientMainFrame);
                       msg.start();
                       date = new Date();
                       String Msgdate = format.format(date);
                       try {
                           filetool.file_writer("D:\\Socket\\" + name + "_chatrecord\\" + current_username + ".txt", "text&&" + name + "&&" + Msgdate);
                           clientMainFrame.add_time(name, Msgdate);
                           filetool.file_writer("D:\\Socket\\" + name + "_chatrecord\\" + current_username + ".txt", text);
                           clientMainFrame.add_message(name, text);
                           clientMainFrame.updateTimeNews(current_username, 0, clientMainFrame.getChatRecord_index(current_username)
                                   , Msgdate, text);
                           inputJTextArea.setText("");
                       } catch (IOException a) {
                           a.printStackTrace();
                       }
                   }else {
                       JOptionPane.showMessageDialog(clientMainFrame,"发送信息不能为空","发送失败",JOptionPane.WARNING_MESSAGE);
                   }
               }
           });
       sendnews_JLabel.setBounds(450, 105, 69,26);
       sendnews_JLabel.setBorder(BorderFactory.createLineBorder(new Color(225, 225 ,225),1));
       sendnews_JLabel.setOpaque(true);
       new ReceiveAudioHandler(ClientMainFrame.receiveAudioSocket);
       this.add(inputJP_function);
       this.add(inputJScrollPane);
       this.add(sendnews_JLabel);
       this.setLayout(null);
       this.setBackground(Color.white);
       this.setBounds(310, 461, 555, 170);
    }

    public void setUsername(String name) {
        current_username = name;
        listener.setFriend(name);
        sendVoiceRequest.setTargetUsername(name);
    }
    private class ReceiveAudioHandler{
        private Socket pushSocket;//接收服务端推送消息的Socket
        private DataInputStream inputStream;
        private DataOutputStream outputStream;
        public ReceiveAudioHandler(Socket pushSocket){
            this.pushSocket=pushSocket;
            new Thread(){
                @Override
                public void run() {
                    try {
                        while (true){
                            inputStream=new DataInputStream(pushSocket.getInputStream());
                            String requestMsg = inputStream.readUTF();

                            System.out.println("接收到的消息为："+requestMsg);

                            String[] splitedRequestMsg = requestMsg.split("&&");
                            if (splitedRequestMsg.length==1){
                                System.out.println("对方拒绝了你的请求");
                            }
                            if (splitedRequestMsg.length==2){
                                String msg1=splitedRequestMsg[0];
                                System.out.println(msg1);
                                String msg2=splitedRequestMsg[1];
                                System.out.println(msg2);
                                System.out.println(InputJPanel.voiceFlag);
                                if ("voice".equals(msg1)){
                                    new ReceiveVoiceChatPanel(msg2,name);
                                    System.out.println("执行了语音来电提醒");
                                    continue;
                                }else {
                                    System.out.println("到了最后一步"+msg1);
                                    System.out.println("到了最后一步"+msg2);
                                    Socket socket = new Socket(msg1, Integer.parseInt(msg2));
                                    audioClient = new AudioClient(msg1, Integer.parseInt(msg2),socket);
                                    audioClient.start();
                                    System.out.println("执行了666");
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }
    public void closeAudioClient(){
        audioClient.setFlag(false);
    }


    public void setChatPane(ChatPanel panel){
       listener.setChatPanel(panel);
    }

}
