package cn.View.chatjpanel.listener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.net.Socket;

/*
 * author:Tan
 * date:2019-11-08
 * class:好友发送过来的文件的小面板，监听器
 */
public class Panel_RecFileListener implements MouseListener {
    private File file;
    private String friend;
    private long file_size;
    private String file_name;
    private JProgressBar bar;
    private JPanel file_panel;
    private RecFile_Thread thread;
//*******************************************************************************
    //存放接收文件的路径
    private final String file_dir="D:\\Socket\\File\\";
    private final String server_ip="192.168.43.152";
//*******************************************************************************
    public Panel_RecFileListener(String friend,JPanel file_panel,JProgressBar bar,String file_name,String file_size)
    {
        this.bar=bar;
        this.friend=friend;
        this.file_name=file_name;
        this.file_panel=file_panel;
        this.file_size=Long.parseLong(file_size);

    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        file=new File(file_dir+file_name);
        //判断文件存在
        if(file.exists())
        {
            //文件已下载完成，点击则打开文件
            if (file.length()>=file_size)
            {
                try {
                    Desktop.getDesktop().open(file);
                    return;
                } catch (IOException ex) {ex.printStackTrace();}
            }
            //文件存在，但是没有下载完(1.线程正在下载 2.没有线程或者线程停止了下载)
            if(file.length()<file_size)
            {
                if(thread==null)
                    thread=new RecFile_Thread();
                else
                {
                    if(thread.getReceieving())
                        thread.setReceieving(false);
                    else
                        thread=new RecFile_Thread();
                }
            }
        }

        //
        if(!file.exists())
        {
            try
            {
                if(file.createNewFile())
                {
                    thread=new RecFile_Thread();
                }
                else
                    System.out.println("文件创建失败");
            } catch (IOException ex) {ex.printStackTrace();}
        }

    }

    //接收文件的线程
    private class RecFile_Thread extends Thread{
        private boolean receieving=false;
        public RecFile_Thread()
        {
            this.start();
        }
        //
        public void run()
        {
            Socket socket =null;

            //连接服务器，发送下载文件请求
            try {
                //
                socket=new Socket(server_ip,1236);
                if(socket!=null)
                    System.out.println("连接接收文件服务成功");
                DataOutputStream output=new DataOutputStream(socket.getOutputStream());
                String request=friend+"&&"+file_name+"&&"+file.length();
                output.writeUTF(request);
                output.flush();
                System.out.println(request);
            } catch (IOException e) {e.printStackTrace(); }

            //接收文件
            try {
                FileOutputStream fos=new FileOutputStream(file,true);
                DataInputStream input=new DataInputStream(socket.getInputStream());
                int len;
                long progress=file.length();
                byte[] bytes=new byte[1024];
                bar.setStringPainted(true);
                bar.setForeground(new Color(100, 200, 100));
                receieving=true;
                while (true)
                {
                    if(receieving==false)
                    {
                        bar.setForeground(new Color(0xFFC628));
                        fos.close();
                        socket.close();
                        System.out.println("暂停了文件接收");
                        return;
                    }
                    if(input.available()>0)
                    {
                        len=input.read(bytes,0,bytes.length);
                        fos.write(bytes,0,len);
                        fos.flush();
                        progress+=len;
                        bar.setValue((int)(100*progress/file_size));
                        bar.repaint();
                        if(progress>=file_size)
                        {
                            bar.setStringPainted(false);
                            fos.close();
                            socket.close();
                            System.out.println("文件接收成功");
                            return;
                        }
                    }
                }
            } catch (FileNotFoundException e) {e.printStackTrace();} catch (IOException e) {e.printStackTrace();}

        }


        //
        public void setReceieving(boolean receieving)
        {
            this.receieving=receieving;
        }

        //
        public boolean getReceieving()
        {
            return receieving;
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
