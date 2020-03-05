package cn.View.chatjpanel.listener;
import cn.View.InputJPanel;
import cn.View.chatjpanel.ChatPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.Cursor;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * author:Tan
 * date:2019-11-06
 * class:发送文件按钮的监听器
 */
public class Btn_SendFileListener implements MouseListener {
    private String user,friend;
    private ChatPanel chatPanel;
    private InputJPanel inputPanel;
    private File file;

    private SimpleDateFormat format;
    private Date date;
    public Btn_SendFileListener(String user, String friend, InputJPanel inputJPanel)
    {
        this.user=user;
        this.friend=friend;
        this.inputPanel=inputJPanel;
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public void mouseClicked(MouseEvent e)
    {
        JFileChooser jfc=new JFileChooser(new File("."));
        jfc.setPreferredSize(new Dimension(700,600));
        int statu=jfc.showOpenDialog(null);
        if(statu==JFileChooser.APPROVE_OPTION)
        {
            file=jfc.getSelectedFile();
            if(file!=null)
            {
                System.out.println(file.getName()+" "+file.length()+"\n"+file.getAbsolutePath());
                //实例化发文件线程
                date = new Date();
                String Msgdate = format.format(date);
                SendFile_Thread send_file=new SendFile_Thread(file,friend,user,Msgdate);
                //往聊天面板加文件
                try {
                    chatPanel.addTimePanel(Msgdate);
                    System.out.println(user+chatPanel);
                    JProgressBar bar=chatPanel.addFilePanel(user,send_file, file.getName(),Long.toString(file.length()),file.getAbsolutePath());
                    send_file.setProgressBar(bar);
                    send_file.start();
                } catch (IOException c) {c.printStackTrace(); }
            }
            else
                System.out.println("取消了选择文件");
        }


    }

    public void mouseEntered(MouseEvent e) {
        inputPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void mouseExited(MouseEvent e) {
        inputPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    public void mousePressed(MouseEvent e) {
        System.out.println("鼠标按下了"+e.getPoint());
    }

    public void mouseReleased(MouseEvent e) {
        System.out.println("鼠标释放了"+e.getPoint());
    }

    public void setChatPanel(ChatPanel panel){
        chatPanel=panel;
    }

    public void setFriend(String friend1){
        friend=friend1;
    }
}
