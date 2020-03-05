package cn.Service;

import cn.View.ClientMainFrame;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SendMsg extends Thread{
    private String msg;
    private String target;
    private Socket sendSocket;
    private String type;
    public String result;
    public ClientMainFrame clientMainFrame;

    public SendMsg(Socket sendSocket, String type, String target, String msg,ClientMainFrame clientMainFrame){
        this.sendSocket=sendSocket;
        this.type=type;
        this.msg=msg;
        this.target=target;
        this.clientMainFrame=clientMainFrame;
    }

    @Override
    public void run() {
        super.run();
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(this.sendSocket.getOutputStream());
            if (type.equals("text")){
                if ((msg!=null)&&(target!=null)){
                    String out=type+"&&"+target+"&&"+msg;
                    System.out.println(out);
                    dataOutputStream.writeUTF(out);
                    dataOutputStream.flush();
                    System.out.println("发送成功");
                    return;
                }
            }
            if (type.equals("addfriend")){
                System.out.println("jiahaoyou:  "+target);
                if (target!=null){
                    String out=type+"&&"+target+"&&"+"请求添加好友";
                    System.out.println("jiahaoyou:  "+out);
                    dataOutputStream.writeUTF(out);
                }
                while (true) {
                    DataInputStream input = new DataInputStream(this.sendSocket.getInputStream());
                    if (input.available() > 0) {
                        String text = input.readUTF();
                        if (!text.equals("&&")) {
                            if(text.equals("请求成功")){
                                JOptionPane.showMessageDialog(clientMainFrame,text,"查找结果",JOptionPane.WARNING_MESSAGE);
                            }else if(text.equals("user not exist")){
                                JOptionPane.showMessageDialog(clientMainFrame,text,"查找结果",JOptionPane.WARNING_MESSAGE);
                            }else if (text.equals("你们已经是好友")){
                                JOptionPane.showMessageDialog(clientMainFrame,text,"查找结果",JOptionPane.WARNING_MESSAGE);
                            }
                            return;
                        }
                    }
                }
            }
        return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getSearchResult(){
        return result;
    }
}
