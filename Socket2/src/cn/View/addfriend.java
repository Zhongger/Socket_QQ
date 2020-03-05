package cn.View;

import cn.Service.SendMsg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class addfriend extends JDialog {
    private static JPanel delete_JPanel = new JPanel();
    private JTextField textField;
    private JLabel search,cansel;

    public ClientMainFrame clientMainFrame;
    public String name=null;
    public SendMsg sendMsg;
    public String username;

    public addfriend(ClientMainFrame clientMainFrame,JFrame owner,String username){
        super(owner,"Add Friend",false);
        this.username=username;
        this.clientMainFrame=clientMainFrame;
        textField = new JTextField("");
        textField.setBounds(5,10,185,30);
        Font font = new Font("微软雅黑", Font.PLAIN, 14);
        textField.setFont(font);
        textField.setBackground(Color.white);
        textField.setBorder(null);
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                textField.setText("");
                textField.setForeground(Color.black);
            }

            @Override
            public void focusLost(FocusEvent e) {
                String text = "  请输入好友名称";
                textField.setForeground(Color.gray);
                textField.setText(text);
            }

        });

        delete_JPanel.setBounds(190, 10, 25, 30);
        delete_JPanel.setBackground(Color.white);

        search = new JLabel("查找",JLabel.CENTER);
        search.setBounds(25,50,75,30);
        search.setOpaque(true);
        search.setBackground(new Color(25, 180 ,30));
        Font font3 = new Font("微软雅黑", Font.PLAIN, 14);
        search.setFont(font3);
        search.setForeground(Color.white);
        search.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                search.setBackground(new Color(25, 160 ,30));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                search.setBackground(new Color(25, 180 ,30));
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                String text =textField.getText();
                if (text.trim().length()!=0){
                    System.out.println("Socket:  "+clientMainFrame.getSendSocket());
                    sendMsg = new SendMsg(clientMainFrame.getSendSocket(),"addfriend",text,null,clientMainFrame);
                    sendMsg.start();
                }else {
                    JOptionPane.showMessageDialog(owner,"发送信息不能为空","发送失败",JOptionPane.WARNING_MESSAGE);
                }
            }

        });


        cansel = new JLabel("清空",JLabel.CENTER);
        cansel.setBounds(115,50,75,30);
        cansel.setOpaque(true);
        cansel.setBackground(new Color(25, 180 ,30));
        Font font2 = new Font("微软雅黑", Font.PLAIN, 14);
        cansel.setFont(font2);
        cansel.setForeground(Color.white);
        cansel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                cansel.setBackground(new Color(25, 160 ,30));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                cansel.setBackground(new Color(25, 180 ,30));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                textField.setText("");
            }
        });

        this.add(textField);
        this.add(delete_JPanel);
        this.add(search);
        this.add(cansel);
        this.setPreferredSize(new Dimension(225,120));
        this.setLocation(335,325);
        this.setResizable(false);
        this.setLayout(null);
        this.setBackground(new Color(235, 235, 235));
        this.pack();
    }

    public String getAddfriendname(){
        return name;
    }
}
