package cn.Service;

import cn.View.ClientMainFrame;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * 服务器问客户端，是否添加xxx为好友的接收类
 */
public class ReceiveNewFriend extends Thread{
    private Socket addfriendSocket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private ClientMainFrame mf;
    public String username;

    public ReceiveNewFriend(Socket addfriendSocket, ClientMainFrame mf,String username){
        this.username=username;
        this.addfriendSocket=addfriendSocket;
        this.mf=mf;
        try {
            this.dataInputStream=new DataInputStream(this.addfriendSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();
        while (true){
            try {
                Thread.sleep(500);
                String addfriendmsg = dataInputStream.readUTF();
                if (addfriendmsg!=null&&!addfriendmsg.equals("&&")){
                    int option = JOptionPane.showConfirmDialog(mf, "是否添加" + addfriendmsg + "为好友？", "提示", JOptionPane.YES_NO_CANCEL_OPTION);
                    dataOutputStream=new DataOutputStream(this.addfriendSocket.getOutputStream());
                    if (option==JOptionPane.YES_OPTION){
                        System.out.println("确认添加");
                        dataOutputStream.writeUTF("add successfully");
                        mf.getSingle(addfriendmsg,"D:\\Socket\\"+username+"\\");
                    }
                    if (option==JOptionPane.NO_OPTION){
                        System.out.println("拒绝添加");
                        dataOutputStream.writeUTF("add failed");
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
