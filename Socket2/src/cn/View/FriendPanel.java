package cn.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class FriendPanel extends JPanel {
    private JPanel title;
    private JLabel addfriend,User_id,separate_line1,separate_line2,bei_zhu,friend_name,add;
    public ImageIcon icon;
    public String username;
    public ClientMainFrame clientMainFrame;

    public FriendPanel(String username,ClientMainFrame clientMainFrame){
        this.clientMainFrame=clientMainFrame;
        this.username=username;
        addfriend = new JLabel("添加好友");
        Font font = new Font("微软雅黑", Font.PLAIN, 18);
        addfriend.setBounds(29,16,250,45);
        addfriend.setBackground(new Color(245,245,245));
        addfriend.setFont(font);
        title = new JPanel();
        title.add(addfriend);
        title.setBounds(0,0,555,62);
        title.setBackground(new Color(240,240,240));
        title.setLayout(null);

        User_id = new JLabel(" ");
        User_id.setBounds(183, 100, 180, 180);
        separate_line1 = new JLabel(" ");
        separate_line1.setBounds(158,300,230,1);
        separate_line1.setOpaque(true);
        separate_line1.setBackground(new Color(225,225,225));
        bei_zhu = new JLabel("用户名",JLabel.CENTER);
        Font font1 = new Font("微软雅黑", Font.PLAIN, 14);
        bei_zhu.setFont(font1);
        bei_zhu.setForeground(new Color(160, 160, 160));
        bei_zhu.setBounds(208,316,42,25);
        friend_name = new JLabel(" ",JLabel.LEFT);
        Font font2 = new Font("微软雅黑", Font.PLAIN, 15);
        friend_name.setFont(font2);
        friend_name.setBounds(278,314,80,30);
        separate_line2 = new JLabel(" ");
        separate_line2.setBounds(158,356,230,1);
        separate_line2.setOpaque(true);
        separate_line2.setBackground(new Color(225,225,225));
        add = new JLabel("添  加",JLabel.CENTER);
        add.setBounds(203, 397, 140, 35);
        add.setOpaque(true);
        Font font3 = new Font("微软雅黑", Font.PLAIN, 15);
        add.setFont(font3);
        add.setForeground(Color.white);
        add.setBackground(new Color(25, 180 ,30));
        add.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                JOptionPane.showMessageDialog(clientMainFrame,"请求成功","添加好友",JOptionPane.WARNING_MESSAGE);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                add.setBackground(new Color(25, 160 ,30));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                add.setBackground(new Color(25, 180 ,30));
            }
        });

        this.add(User_id);
        this.add(separate_line1);
        this.add(bei_zhu);
        this.add(friend_name);
        this.add(separate_line2);
        this.add(add);
        this.add(title);
        this.setBackground(new Color(245,245,245));
        this.setLayout(null);
        this.setBounds(310, 0, 555, 631);
    }
    //设置图标和用户名
    public void setIdFriendname(String name){
        System.out.println();
        ImageIcon icon = new ImageIcon("D:\\Socket\\"+username+"_chatrecord\\"+name+".png.png");
        icon.setImage(icon.getImage().getScaledInstance(180,180,Image.SCALE_SMOOTH));
        User_id.setIcon(icon);
        friend_name.setText(name);
    }
}

