package cn.View;

import cn.Model.User;
import cn.Service.SocketClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class LoginFrame extends JFrame{
    JPanel mb;
    JLabel lb,lb1,lb2,lb3;
    JButton bt;
    JTextField username;
    JPasswordField password;
    JCheckBox select1,select2;
    private SocketClient socketClient;
    public static LoginFrame loginFrame;
    public  int flag;

    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) { e.printStackTrace(); } catch (InstantiationException e) { e.printStackTrace(); } catch (IllegalAccessException e) { e.printStackTrace(); } catch (UnsupportedLookAndFeelException e) { e.printStackTrace(); }
    }
    public LoginFrame() {
        lb=new JLabel(new ImageIcon("image/loginbackground.png"));
        mb=new JPanel();
        lb3=new JLabel(new ImageIcon("image/headimg.png"));
        username =new JTextField(20);
        password=new JPasswordField(20);
        lb1=new JLabel("<html><a href='http://192.168.43.152:8080/webs/register.html'>点击注册</a></html>");
        lb1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Runtime.getRuntime().exec("cmd.exe /c start " + "http://192.168.43.152:8080/webs/register.html");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        lb1.setForeground(new Color(28,134,238));
        lb1.setFont(new Font("微软雅黑",Font.PLAIN,16));
        lb1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lb2=new JLabel("<html><a >忘记密码?</a></html>");
        lb2.setForeground(new Color(28,134,238));
        lb2.setFont(new Font("微软雅黑",Font.PLAIN,16));
        lb2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        select1=new JCheckBox("记住密码");
        select1.setFont(new Font("微软雅黑",Font.PLAIN,15));
        select1.setBackground(Color.WHITE);
        select2=new JCheckBox("自动登录");
        select2.setFont(new Font("微软雅黑",Font.PLAIN,15));
        select2.setBackground(new Color(255,250,250));
        bt=new JButton("登录");
        bt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                User user = new User();
                user.setUsername(username.getText());
                user.setPassword(String.valueOf(password.getPassword()));
                System.out.println(user.getUsername()+user.getPassword());
                socketClient = new SocketClient("192.168.43.152", 1248, user.getUsername(), user.getPassword());
                socketClient.start();
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                if (loginFrame.getFlag()==1){
                    JOptionPane.showMessageDialog(loginFrame,"登录成功","登录成功",JOptionPane.WARNING_MESSAGE);
                    loginFrame.dispose();
                    ClientMainFrame layout = new ClientMainFrame(loginFrame.username.getText());
                    layout.setVisible(true);
                }
                if (loginFrame.getFlag()==-1){
                    JOptionPane.showMessageDialog(loginFrame,"密码错误","登录失败",JOptionPane.WARNING_MESSAGE);
                    password.setText("");
                }
                if (loginFrame.getFlag()==0){
                    JOptionPane.showMessageDialog(loginFrame,"用户名不存在","登录失败",JOptionPane.WARNING_MESSAGE);
                    username.setText("");
                    password.setText("");
                }
                if (loginFrame.getFlag()==886){
                    JOptionPane.showMessageDialog(loginFrame,"服务器错误","登录失败",JOptionPane.WARNING_MESSAGE);
                    username.setText("");
                    password.setText("");
                }
                
            }
        });

        mb.add(lb2);mb.add(lb1);mb.add(lb3);mb.add(select1);mb.add(select2);
        mb.add(bt);mb.add(username);mb.add(password);
        mb.setSize(540,190);

        mb.setLayout(null);
        mb.setBackground(Color.white);
        lb3.setBounds(43, 8, 100, 100);
        username.setBounds(160, 14, 250, 37);
        username.setFont(new Font("微软雅黑",Font.PLAIN,16));
        password.setBounds(160,48, 250, 37);
        password.setFont(new Font("微软雅黑",Font.PLAIN,16));
        lb1.setBounds(420,10,80,34);
        lb2.setBounds(420,50,80,34);
        select1.setBounds(160,90,120,20);
        select2.setBounds(320,90,120,20);
        bt.setBounds(160,130,250,37);
        bt.setFont(new Font("微软雅黑",Font.PLAIN,16));
        bt.setBackground(new Color(0,178,238));
        bt.setForeground(Color.white);

        this.add(lb,BorderLayout.NORTH);
        this.add(mb,BorderLayout.CENTER);
        this.setSize(540, 440);
        this.setTitle("登录WeTalk");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocation(620, 280);
        this.setResizable(false);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        loginFrame = new LoginFrame();
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
    
    public int getFlag(){
        return flag;
    }
}