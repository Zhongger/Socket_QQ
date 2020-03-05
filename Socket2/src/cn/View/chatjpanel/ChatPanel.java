package cn.View.chatjpanel;
import cn.View.chatjpanel.listener.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.net.URL;

/*
 * author:Tan
 * date:2019-11-05
 * class:滑动面板
 */
public class ChatPanel extends JScrollPane{
    String user=null,friend=null;
    public JScrollPane jScrollPane;//滑动面板
    public MainPanel panel;//主面板
    final int chatPanel_width=555;
    public String url_user,url_friend;
    public ChatPanel(String user,String friend)
    {
        this.user=user;
        this.friend=friend;
//******************************************************************************************
        //用户和好友头像的路径(需要修改)
        url_user= "D:\\Socket\\"+user+"_chatrecord\\"+user+".png.png";
        url_friend= "D:\\Socket\\"+user+"\\"+friend+".png.png";
//********************************************************************************************

        //主面板
        panel=new MainPanel();
        //滑动面板
        jScrollPane=this;
        jScrollPane.setBorder(null);
        jScrollPane.getViewport().add(panel);
        jScrollPane.setBounds(310, 62, chatPanel_width, 399);
        jScrollPane.getVerticalScrollBar().setUnitIncrement(20);//滑轮速度
        jScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane.getVerticalScrollBar().setBackground(new Color(245,245,245));
        jScrollPane.getVerticalScrollBar().setBorder(new EmptyBorder(0,0,0,0));
    }

    //
    //主面板mainPanel类
    private class MainPanel extends JPanel{

        public MainPanel()
        {
            //添加盒式布局管理器（垂直布局）
            this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
            //设置不透明
            this.setOpaque(true);
            this.setBackground(new Color(245,245,245));
            //打开聊天文件
            if(friend!=null)
            try {

//****************************************************************************************************
                //聊天记录的路径(需要修改)
                String record_path="D:\\Socket\\"+user+"_chatrecord\\"+friend+".txt";
                BufferedReader reader=new BufferedReader(new FileReader(record_path));
//****************************************************************************************************

                String str_1;
                while((str_1=reader.readLine())!=null)
                {
                    if(!str_1.contains("&&"))
                        continue;
                    String temp[]=str_1.split("&&");
                    System.out.println(str_1);
                    if(temp[0]!=null)
                    {
                        String type=temp[0];
                        //创建时间标签
                        if(temp[2]!=null)
                        {
                            this.add(TimePanel(temp[2]));
                        }
                        //文字消息
                        if(type.equals("text"))
                        {
                            String sender=temp[1];
                            String msg=reader.readLine();
                            this.add(textPanel(sender,msg));
                            this.repaint();
                        }
                        //文件消息
                        if(type.equals("file"))
                        {
                            String sender=temp[1];
                            String file_name=temp[3];
                            String file_size=temp[4];
                            String file_dir=null;
                            if(temp[1].equals(user))
                                file_dir=reader.readLine();
                            File_Panel file_panel=new File_Panel(sender,null,file_name,file_size,file_dir);
                            this.add(file_panel.getFilePanel());
                            this.repaint();
                        }
                    }
                }
                reader.close();
            } catch (FileNotFoundException e) {e.printStackTrace(); } catch (IOException e) {e.printStackTrace(); }
        }
    }


    //建文本消息面板的方法
    public JPanel textPanel(String sender,String msg) throws IOException {

        //新建小面板
        JPanel p=new JPanel();
        p.setOpaque(false);
        //添加盒式布局管理器（水平布局）
        p.setLayout(new BoxLayout(p,BoxLayout.X_AXIS));
        //设置边距
        p.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        if(msg.length()<=14)
            p.setMaximumSize(new Dimension(chatPanel_width,65));
        else p.setMaximumSize(new Dimension(chatPanel_width,60+(msg.length()/14+1)*20));
        //用户头像标签
        ImageIcon userIcon=new ImageIcon(url_user);
        userIcon.setImage(userIcon.getImage().getScaledInstance(30,30,Image.SCALE_DEFAULT));
        JLabel user_img=new JLabel();
        user_img.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        user_img.setIcon(userIcon);
        //好友头像
        ImageIcon friendIcon=new ImageIcon(url_friend);
        friendIcon.setImage(friendIcon.getImage().getScaledInstance(30,30,Image.SCALE_DEFAULT));
        JLabel friend_img=new JLabel();
        friend_img.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        friend_img.setIcon(friendIcon);
        //文本消息的文本域
        JTextArea msg_area;
        msg_area=new JTextArea();
        msg_area.setText(msg);
        msg_area.setEditable(false);
        msg_area.setSelectionColor(new Color(12,120,255));
        msg_area.setSelectedTextColor(new Color(255, 255, 255));
        msg_area.setLineWrap(true);
        msg_area.setAlignmentX(1);
        msg_area.setOpaque(true);//设置标签不透明
        if(sender.equals(user))
            msg_area.setBackground(new Color(150,255,150));
        else
            msg_area.setBackground(new Color(255,255,255));
        msg_area.setForeground(Color.BLACK);//字体颜色
        msg_area.setFont(new Font("微软雅黑",0,16));
        msg_area.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        //
        //计算布局
        int font_len=21;
        int base_len=80;
        int white_len=0;
        if(msg.length()<=5)
            white_len=chatPanel_width-(base_len+5*font_len+10);
        if(msg.length()>5&&msg.length()<=12)
            white_len=chatPanel_width-(base_len+font_len*msg.length());
        if(msg.length()>12)
            white_len=chatPanel_width-(base_len+font_len*12);

        //布局
        if(sender.equals(friend))//该消息发送者是好友
        {
            p.add(friend_img);
            p.add(msg_area);
            p.add(Box.createHorizontalStrut(white_len));
        }
        if(sender.equals(user))//该消息发送者是自己
        {
            p.add(Box.createHorizontalStrut(white_len));
            p.add(msg_area);
            p.add(user_img);
        }

        //返回该小面板
        return p;
    }

    //********************************************
    //**文件消息处理类(文件消息较复杂，所以建个类)
    //********************************************
    private class File_Panel {
        public String sender;
        public SendFile_Thread thread;
        public String file_name, file_size, file_dir;
        public JPanel file_panel;
        public JProgressBar bar;

        public File_Panel(String sender, SendFile_Thread thread, String file_name, String file_size, String file_dir) {
            this.thread = thread;
            this.sender = sender;
            this.file_name = file_name;
            this.file_size = file_size;
            this.file_dir = file_dir;

            //新建文件面板
            file_panel = new JPanel();
            file_panel.setOpaque(false);
            //添加盒式布局管理器（水平布局）
            file_panel.setLayout(new BoxLayout(file_panel, BoxLayout.X_AXIS));
            //设置边距
            file_panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            file_panel.setMaximumSize(new Dimension(chatPanel_width,180));
            //用户头像标签
            ImageIcon userIcon=new ImageIcon(url_user);
            userIcon.setImage(userIcon.getImage().getScaledInstance(30,30,Image.SCALE_DEFAULT));
            JLabel user_img=new JLabel();
            user_img.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            user_img.setIcon(userIcon);
            //好友头像
            ImageIcon friendIcon=new ImageIcon(url_friend);
            friendIcon.setImage(friendIcon.getImage().getScaledInstance(30,30,Image.SCALE_DEFAULT));
            JLabel friend_img=new JLabel();
            friend_img.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            friend_img.setIcon(friendIcon);

            //底层面板p2
            JPanel p2 = new JPanel();
            p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
            p2.setOpaque(true);
            p2.setBackground(new Color(255, 255, 255));

            //标签层面板p3
            JPanel p3 = new JPanel();
            p3.setLayout(new BoxLayout(p3, BoxLayout.Y_AXIS));
            p3.setBorder(new EmptyBorder(10, 10, 10, 10));
            p3.setOpaque(false);
            p3.setAlignmentX(0);

            //标签
            JLabel filename = new JLabel(file_name);
            filename.setPreferredSize(new Dimension(120, 30));
            filename.setAlignmentX(0);
            filename.setForeground(Color.BLACK);//字体颜色
            filename.setFont(new Font("微软雅黑", 0, 16));
            filename.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 4));
            filename.setOpaque(false);
            //
            JLabel filesize = new JLabel(file_size);
            filesize.setPreferredSize(new Dimension(120, 30));
            filesize.setAlignmentX(0);
            filesize.setForeground(new Color(150, 150, 150));//字体颜色
            filesize.setFont(new Font("微软雅黑", 0, 12));
            filesize.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 4));
            filesize.setOpaque(false);
            //
            bar = new JProgressBar(0, 100);
            bar.setMaximumSize(new Dimension(400, 30));
            bar.setOpaque(true);
            bar.setBackground(new Color(250, 250, 250));
            bar.setStringPainted(false);
            bar.setBorderPainted(false);
            if(thread==null&&sender.equals(user))
                bar.setValue(100);
            if(file_dir==null&&thread==null&&sender.equals(friend))
            {
                File file=new File("D:\\Socket\\File\\"+file_name);
                if(file.exists())
                {
                    int progress=(int)(100*file.length()/Long.parseLong(file_size));
                    if(progress<100)
                    {
                        bar.setStringPainted(true);
                        bar.setValue(progress);
                        bar.setForeground(new Color(0xFFC628));
                    }
                    else
                    {
                        bar.setValue(progress);
                    }
                }
            }
            //
            p3.add(filename);
            p3.add(filesize);
            p3.add(bar);

            //
            ImageIcon file_icon = new ImageIcon(ChatPanel.class.getResource("resourse" + File.separatorChar + "file.png"));
            file_icon.setImage(file_icon.getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT));
            JLabel fileIcon_label = new JLabel();
            fileIcon_label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            fileIcon_label.setIcon(file_icon);
            fileIcon_label.setAlignmentX(0);
            //
            p2.add(p3);
            p2.add(fileIcon_label);
            //p2面板添加鼠标监听器
            if(sender.equals(user))
            {
                File file=new File(file_dir);
                p2.addMouseListener(new Panel_SendFileListener(p2,friend,thread,file,bar));
            }
            else
            {
                p2.addMouseListener(new Panel_RecFileListener(friend,p2,bar,file_name,file_size));
            }

            //布局
            if (sender.equals(friend))//该消息发送者是好友
            {
                file_panel.add(friend_img);
                file_panel.add(p2);
                file_panel.add(Box.createHorizontalGlue());
            }
            if (sender.equals(user))//该消息发送者是自己
            {
                file_panel.add(Box.createHorizontalGlue());
                file_panel.add(p2);
                file_panel.add(user_img);
            }
        }

        //
        public JPanel getFilePanel() {
            return file_panel;
        }

        //
        public JProgressBar getProgressBar() {
            return bar;
        }

    }

    //时间标签
    public JLabel TimePanel(String time)
    {
        String date_time=time.substring(5,16);
        JLabel time_panel=new JLabel(date_time);
        time_panel.setOpaque(true);//设置标签不透明
        time_panel.setBackground(new Color(216,216,216));
        time_panel.setForeground(Color.WHITE);//字体颜色
        time_panel.setFont(new Font("微软雅黑",0,14));
        time_panel.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
        time_panel.setAlignmentX((float) 0.5);
        return time_panel;
    }

    //更新聊天面板，使得滑动条滑到底部
    public void updateChatPanel()
    {
        jScrollPane.getVerticalScrollBar().setValue(panel.getHeight());
    }


    //增加文本消息到聊天面板
    public void addTextPanel(String sender,String msg) throws IOException
    {
        panel.add(textPanel(sender,msg));
        panel.repaint();
        jScrollPane.getViewport().add(panel);
        updateChatPanel();
    }


    //增加文件消息到聊天面板
    public JProgressBar addFilePanel(String sender,SendFile_Thread thread,String file_name,String file_size,String file_dir) throws IOException
    {
        File_Panel file_panel=new File_Panel(sender,thread,file_name,file_size,file_dir);
        panel.add(file_panel.getFilePanel());
        panel.repaint();
        jScrollPane.getViewport().add(panel);
        updateChatPanel();
        return file_panel.getProgressBar();
    }

    //增加时间标签的方法
    public void addTimePanel(String time)
    {
        panel.add(TimePanel(time));
        panel.repaint();
        jScrollPane.getViewport().add(panel);
        updateChatPanel();
    }

}
