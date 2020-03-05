package cn.View;


import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Reservename extends JPanel {

    private JLabel friend_name;
    private String friendname = "abc";

    public Reservename() {
        friend_name = new JLabel( );
        Font font = new Font("微软雅黑", Font.PLAIN, 18);
        friend_name.setBounds(29,16,250,45);
        friend_name.setBackground(new Color(245,245,245));
        friend_name.setFont(font);

        this.add(friend_name);
        this.setBounds(310,0,555,62);
        this.setBackground(new Color(240,240,240));
        this.setLayout(null);
    }

    public String getname(File imagefile) {
        String[] information = imagefile.getName().split(".png");
        return information[0];
    }

    public void setname(String name){
        friend_name.setText(name);
    }

    public void setfriendname(String name){
        friendname = name;
    }

    public String getfriendname(){
        return friendname;
    }
}
