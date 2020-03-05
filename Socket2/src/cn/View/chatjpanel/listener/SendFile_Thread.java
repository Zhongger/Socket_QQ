package cn.View.chatjpanel.listener;

import cn.util.Filetools;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SendFile_Thread extends Thread{

    private File file;
    private String friend,user;
    private JProgressBar bar;
    private boolean sending=false;

    public Filetools filetools;
    public SimpleDateFormat format;
    public String Msgdate;

    private static final String Server_ip="192.168.43.152";

    public SendFile_Thread(File file,String friend,String user,String date)
    {
        this.file=file;
        this.friend=friend;
        this.user=user;
        this.Msgdate=date;
        filetools = new Filetools(user);
    }

    public void run()
    {
        //向服务器发送文件
        sendfile();
    }

    //向服务器请求发送文件
    public void sendfile()
    {
        //连接发文件服务
        Socket fileSocket;
        while(true)
        {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {e.printStackTrace(); }
            try {
                fileSocket=new Socket(Server_ip,1238);
                if (fileSocket!=null)
                    break;
            } catch (IOException e) { continue; }

        }
        System.out.println("1连接发送文件服务成功！");

        //
        if(!file.exists())
        {
            System.out.println("文件不存在");
        }

        //发送文件
        FileInputStream fis= null;
        try {
            DataOutputStream output=new DataOutputStream(fileSocket.getOutputStream());
            output.writeUTF("File&&"+friend+"&&"+file.length()+"&&"+file.getName());
            output.flush();

            //获取断点续传的位置
            long skipLength=0;
            while(true)
            {
                DataInputStream input=new DataInputStream(fileSocket.getInputStream());
                if(input.available()>0)
                {
                    String t=input.readUTF();
                    if(t.equals("&&"))
                        continue;
                    String[]temp=t.split("&&");
                    if(temp[0].equals("skip"))
                    {
                        skipLength=Long.parseLong(temp[1]);
                        System.out.println("跳过的长度："+skipLength);
                        break;
                    }

                }
                Thread.sleep(100);
            }
            if(skipLength>=file.length())
            {
                System.out.println("发送成功");
                //设置进度条为100%
                bar.setValue(100);
                bar.repaint();
                filetools.file_writer("D:\\Socket\\"+user+"_chatrecord\\"+friend+".txt","file&&"
                        +user+"&&"+Msgdate+"&&"+file.getName()+"&&"+file.length());
                filetools.file_writer("D:\\Socket\\"+user+"_chatrecord\\"+friend+".txt",file.getAbsolutePath());
                return;
            }

            //跳过skipLength长度
            fis = new FileInputStream(file);
            fis.skip(skipLength);//跳过

            //开始传文件
            byte[] bytes=new byte[1024];
            int len=0;
            long progress=skipLength;
            sending=true;//设置为正在发送状态
            bar.setStringPainted(true);//设置进度条可见
            bar.setForeground(new Color(100, 200, 100));
            while((len=fis.read(bytes,0,bytes.length))!=-1)
            {
                if(sending==true)
                {
                    output.write(bytes,0,len);
                    output.flush();
                    progress+=len;
                    bar.setValue((int)(100*progress/file.length()));
                    bar.repaint();
                    System.out.println("发送进度："+100*progress/file.length()+"%");
                }
                if(sending==false)//暂停发送
                {
                    bar.setForeground(new Color(0xFFC628));
                    fis.close();
                    return;
                }
                //Thread.sleep(100);
            }
            sending=false;//设置为非发送状态
            bar.setStringPainted(false);
            fis.close();//关闭
            System.out.println("发送成功！");
            filetools.file_writer("D:\\Socket\\"+user+"_chatrecord\\"+friend+".txt","file&&"
            +user+"&&"+Msgdate+"&&"+file.getName()+"&&"+file.length());
            filetools.file_writer("D:\\Socket\\"+user+"_chatrecord\\"+friend+".txt",file.getAbsolutePath());
//********************************************************************************************
            //写入聊天记录文本，格式为：(file&&发送者的用户名&&时间&&文件名&&文件大小)
            //****
            //在下一行再写入一条该文件的路径的记录
            //****
//*********************************************************************************************
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setProgressBar(JProgressBar bar)
    {
        this.bar=bar;
    }
    //设置发送状态
    public void setIsSending(boolean sending)
    {
        this.sending = sending;
    }
    //获取发送状态
    public boolean getIsSending()
    {
        return sending;
    }
}
