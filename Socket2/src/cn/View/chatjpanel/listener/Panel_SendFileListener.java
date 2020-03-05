package cn.View.chatjpanel.listener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

/*
 * author:Tan
 * date:2019-11-06
 * class:文件消息面板的监听器
 */
public class Panel_SendFileListener implements MouseListener {
    private File file;
    private String friend;
    private JProgressBar bar;
    private JPanel file_panel;
    private SendFile_Thread thread;
    //private String file_name,file_size,file_dir;

    public Panel_SendFileListener(JPanel panel, String friend, SendFile_Thread thread, File file,JProgressBar bar)
    {
        this.file_panel=panel;
        this.friend=friend;
        this.thread=thread;
        this.file=file;
        this.bar=bar;
    }
    @Override
    public void mouseClicked(MouseEvent e)
    {
        if(thread!=null)
        {
            //线程正在发送文件
            if(thread.getIsSending())
            {
                //暂停发送
                thread.setIsSending(false);
            }
            //线程不是发送状态
            else
            {
                if(bar.getValue()<100)
                {
                    SendFile_Thread send_file=new SendFile_Thread(file,friend,null,null);
                    send_file.setProgressBar(bar);
                    this.thread=send_file;
                    send_file.start();
                }
                if (bar.getValue()==100)
                {
                    try {
                        Desktop.getDesktop().open(file);
                    } catch (IOException ex) {ex.printStackTrace();}
                }
            }
        }
        //
        if(thread==null)
        {
            try {
                Desktop.getDesktop().open(file);
                //Runtime.getRuntime().exec("cmd /c start explorer "+file.getParent());
            } catch (IOException ex) {ex.printStackTrace();}
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        e.getComponent().setBackground(new Color(230,230,230));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        e.getComponent().setBackground(new Color(255,255,255));
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        e.getComponent().setBackground(new Color(240,240,240));
        file_panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        e.getComponent().setBackground(new Color(255,255,255));
        file_panel.setCursor(Cursor.getDefaultCursor());
    }
}

