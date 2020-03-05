package cn.View;

import cn.Model.ServerConfig;
import cn.Service.VoiceServer;
import cn.Service.ReceiveNewFriend;
import cn.util.Filetools;
import cn.View.chatjpanel.ChatPanel;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.io.FileInputStream;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;


public class ClientMainFrame extends JFrame {
	private static final String Server_ip = "192.168.43.152";
	private static final int Port = 1242;
	private static Socket sendSocket;
	private static Socket receiveSocket;
	private static Socket getfriendSocket;
	private static Socket addfriendSocket;
	private static JList<File> FriendList = new JList<File>();
	private static DefaultListModel<File> friendmodel = new DefaultListModel<File>();
	private static JList<File> ChatList = new JList<File>();
	private static DefaultListModel<File> chatmodel = new DefaultListModel<File>();
	private static DefaultListModel<String> timemodel = new DefaultListModel<String>();
	private static DefaultListModel<String> newsmodel = new DefaultListModel<String>();
	private static DefaultListModel<Integer> warnmodel = new DefaultListModel<Integer>();
	public static Socket receiveAudioSocket;//接收音/视频
	private JScrollPane FriendJScrollPane,ChatRecord;
	private JPanel send,User_list,addfriendsPane,addfriendString;
	private static JPanel function;
	private JLabel User_id, User, addfriends, news, friends,send_news,separate_line1,separate_line2,
			bei_zhu,friend_name,addfriendJLabel;
	private static JLabel user_photo;
	private JTextField Search_Field;

	private static String username;//鐧诲綍鍒颁富椤甸潰鐨勭敤鎴峰悕
	private static String[] yonghu_name = new String[20];

	private String FriendList_Seleted=null,ChatList_Seleted = "abc";
	private int ChatList_SeletedIndex=-1;
	private int get_way=0;//用于判断当前获取用户头像的方式，0代表重服务器获取，1代表在本地获取
	private static int a=0,b=0;//用于一开始初始化自己头像，从服务器获取成功的标志，1代表获取成功
	public static int user_number=0;//用于记录当前好友个数

	public static FriendJPanel friendsJpanel;
	public static ChatJPanel chatJPanel;
	public static Search sousuo;
	public static ClientMainFrame clientMainFrame;//鎸囧悜ClientMainFrame绫荤殑涓�涓璞★紝鎵�鏈夌被鍙闂�
	public static ChatPanel[] chat = new ChatPanel[20];
	public static Reservename friendname;//瑕佸彂閫佺殑鐩爣ID
	public static InputJPanel inputJPanel;
	public static Filetools filetool;
	public static addfriend add;
	public static FriendPanel panel;

	public ImageIcon icon2 = new ImageIcon("D:\\Socket\\tubiao\\news2.png");
	public ImageIcon icon20 = new ImageIcon("D:\\Socket\\tubiao\\new3.png");
	public ImageIcon icon3 = new ImageIcon("D:\\Socket\\tubiao\\tong.png");
	public ImageIcon icon30 = new ImageIcon("D:\\Socket\\tubiao\\tong2.png");
	public ImageIcon icon4 = new ImageIcon("D:\\Socket\\tubiao\\jia2.png");
	public ImageIcon icon40 = new ImageIcon("D:\\Socket\\tubiao\\jia3.png");


	public Socket getSendSocket() {
		return sendSocket;
	}

	public Socket getReceiveSocket() {
		return receiveSocket;
	}

	public Socket getGetfriendSocket() {
		return getfriendSocket;
	}

	public Socket getAddfriendSocket() {
		return addfriendSocket;
	}

	public void add_message(String sender,String msg) throws IOException {
		if (sender.equals(username)) {
			int k = 0;
			for (int i = 0; i < 20; i++) {
				if(yonghu_name[i]!=null) {
					if (yonghu_name[i].equals(ChatList_Seleted)) {
						k = i;
						chat[k].addTextPanel(sender, msg);
						break;
					}
				}
			}
		}else if(!sender.equals(username)){
			int k1 = 0;
			for (int i = 0; i < 20; i++) {
				if (yonghu_name[i] != null) {
					if (yonghu_name[i].equals(sender)) {
						k1 = i;
						chat[k1].addTextPanel(sender, msg);
						break;
					}
				}
			}
		}
	}

	public void add_time(String sender,String time){
		if(sender.equals(username)) {
			int k = 0;
			for (int i = 0; i < 20; i++) {
				if (yonghu_name[i].equals(ChatList_Seleted)) {
					k = i;
					chat[k].addTimePanel(time);
					break;
				}
			}
		}
		else {
			int k1 = 0;
			for (int i = 0; i < 20; i++) {
				if (yonghu_name[i].equals(sender)) {
					k1 = i;
					chat[k1].addTimePanel(time);
					break;
				}
			}
		}
	}

	public void add_file(String sender,String filename,String filesize) throws IOException{
		int k = 0;
		for (int i = 0; i < 20; i++) {
			if (yonghu_name[i].equals(sender)) {
				k = i;
				chat[k].addFilePanel(sender,null,filename,filesize,"D:\\Socket\\File\\");
				break;
			}
		}
	}

	/*动态更新聊天记录列表里的时间和消息。type代表进行的操作类型，1代表插入一条信息(默认
插入操作时插入在最前面)，0代表更改某条信息；index是进行更改信息时的元素索引值;time和msg
是更改信息时要用*/
	public void updateTimeNews(String friend,int type,int index,String date,String msg){
		switch (type){
			case 1:
				/*两种情况，date与msg都为空是执行从文本里面读信息；date与msg不为空，为
				好友发送文件或消息过来的情况*/
				if ((date==null)&&(msg==null)) {
					String time1, judge_type, Msg;
					int lines;
					lines = filetool.file_lines("D:\\Socket\\" + username + "_chatrecord\\" + friend + ".txt");
					judge_type = filetool.file_reader_targetLine("D:\\Socket\\" + username + "_chatrecord\\" + friend + ".txt", lines - 1);
					Msg = filetool.file_reader_targetLine("D:\\Socket\\" + username + "_chatrecord\\" + friend + ".txt", lines);
					if (judge_type != null ) {
						String[] text = judge_type.split("&&");
						time1 = filetool.get_targetTimeContent(text[2]);
						if (text[0].equals("file")) {
							newsmodel.add(0, "[文件]");
							timemodel.add(0, time1);
						} else {
							newsmodel.add(0, Msg);
							timemodel.add(0, time1);
						}
					} else {
						newsmodel.add(0, "0");
						timemodel.add(0, "0");
					}
				}else if ((date!=null)&&(msg!=null)){
					newsmodel.add(0,"[文件]");
					timemodel.add(0,filetool.get_targetTimeContent(date));
				}
				break;

			case 0:
				if ((date==null)&&(msg==null)) {
					String time2,judge_type2,Msg2;
					int lines2;
					lines2 = filetool.file_lines("D:\\Socket\\" + username + "_chatrecord\\" + friend + ".txt");
					judge_type2 = filetool.file_reader_targetLine("D:\\Socket\\" + username + "_chatrecord\\" + friend + ".txt", lines2 - 1);
					Msg2 = filetool.file_reader_targetLine("D:\\Socket\\" + username + "_chatrecord\\" + friend + ".txt", lines2);
					if (judge_type2 != null) {
						String[] text2 = judge_type2.split("&&");
						time2 = filetool.get_targetTimeContent(text2[2]);
						if (text2[0].equals("file")) {
							newsmodel.addElement( "[文件]");
							timemodel.addElement(time2);
						} else {
							newsmodel.addElement(Msg2);
							timemodel.addElement(time2);
						}
					} else {
						newsmodel.addElement("0");
						timemodel.addElement("0");
					}
				}else if ((date!=null)&&(msg!=null)){
					newsmodel.remove(index);
					newsmodel.add(index, msg);
					timemodel.remove(index);
					timemodel.add(index, filetool.get_targetTimeContent(date));
				}
				break;

			default:
				System.out.println("错误操作");
		}
	}

    //获取ChatRecordJList某个元素的索引值
	public int getChatRecord_index(String friend){
		int index;
		File file = new File("D:\\Socket\\"+username+"\\"+friend+".png.png");
		index =chatmodel.indexOf(file);
		return index;
	}
	//用于搜索聊天记录时切换面板
	public void search_ChatRecord(String name){
		int k = 0;
		for (int i=0;i<20;i++){
			if (yonghu_name[i].equals(name)){
				k=i;
				break;
			}
		}
		ChatList_Seleted=name;
		friendname.setname(name);
		news.setIcon(icon20);
		friends.setIcon(icon3);
		addfriends.setIcon(icon4);
		clientMainFrame.remove(friendsJpanel);
		clientMainFrame.remove(send);
		clientMainFrame.getContentPane().add(chat[k]);
		inputJPanel.setUsername(name);
		inputJPanel.setChatPane(chat[k]);
		chat[k].updateChatPanel();
		clientMainFrame.getContentPane().add(inputJPanel);
		clientMainFrame.getContentPane().add(friendname);
		clientMainFrame.getContentPane().add(chatJPanel);
		clientMainFrame.pack();
		clientMainFrame.repaint();
	}
    //消息提醒设置
	public void set_warn(int type,int index){
		if (type==1){
		if (warnmodel.getElementAt(index)!=1){
			warnmodel.remove(index);
			warnmodel.add(index,1);
		     }
		ChatList.setCellRenderer(new ChatCellRender());
		}else {
			warnmodel.remove(index);
			warnmodel.add(index,0);
			ChatList.setCellRenderer(new ChatCellRender());
		}
	}
	//语音提醒
	public void voice_warn(){
		try {
			FileInputStream fileau = new FileInputStream("D:\\Socket\\tubiao\\yuyin.wav");
			AudioStream as = new AudioStream(fileau);
			AudioPlayer.player.start(as);		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	//用于外部类访问该类内部类——获取单个用户头像
	public void getSingle(String name,String dir){
		new get_user_photo(name,dir).start();
	}
	//用于查找好友时切换面板
	public void search_friend(String name){
		int k = 0;
		for (int i=0;i<20;i++){
			if (yonghu_name[i]!=null) {
				if (yonghu_name[i].equals(ChatList_Seleted)) {
					k = i;
					break;
				}
			}
		}

		while (true){
			if (a==1){
				panel.setIdFriendname(name);
				break;
			}
		}

		friends.setIcon(icon30);
		news.setIcon(icon2);
		addfriends.setIcon(icon4);
		add.setVisible(false);
		clientMainFrame.getContentPane().add(friendsJpanel);
		//clientMainFrame.getContentPane().add(panel);
		clientMainFrame.remove(chatJPanel);
		clientMainFrame.remove(send);
		clientMainFrame.remove(chat[k]);
		clientMainFrame.remove(inputJPanel);
		clientMainFrame.remove(friendname);
		clientMainFrame.pack();
		clientMainFrame.repaint();
	}

	public ClientMainFrame(String username) {
		super(username + "的主界面");
		clientMainFrame = this;

		this.username = username;
		try {
			sendSocket = new Socket(ServerConfig.getHost(), ServerConfig.getSendPort());//
			receiveSocket = new Socket(ServerConfig.getHost(), ServerConfig.getReceivePort());//
			getfriendSocket = new Socket(ServerConfig.getHost(), ServerConfig.getFriendPort());//
			addfriendSocket = new Socket(ServerConfig.getHost(), ServerConfig.getAddPort());//
			receiveAudioSocket = new Socket(ServerConfig.getVoiceServerHost(),VoiceServer.pushPort);//登录第一步，先连接好接收音频请求的Socket
		} catch (IOException e) {
			e.printStackTrace();
		}
		filetool = new Filetools(username);
		init_user();
		new ReceiveMsg();
		new ReceiveNewFriend(clientMainFrame.getAddfriendSocket(),clientMainFrame,username).start();

        File file = new File("D:\\Socket\\"+username+"_chatrecord\\"+username+".png.png");
        if (file.exists()) {
			System.out.println("用户头像文件存在");
		}else {
			get_user_photo t =new get_user_photo(username,"D:\\Socket\\"+username+"_chatrecord\\");
			t.start();
			while (b == 0) {
				System.out.println(b);
			}
		}

		panel =new FriendPanel(username,clientMainFrame);

		add = new addfriend(clientMainFrame,ClientMainFrame.this,username);
		user_photo = new JLabel();
		ImageIcon icon = new ImageIcon("D:\\Socket\\"+username+"_chatrecord\\"+username+".png.png");
		icon.setImage(icon.getImage().getScaledInstance(33, 33,Image.SCALE_SMOOTH));
		user_photo.setBorder((BorderFactory.createEmptyBorder(20, 15, 0, 15)));
		user_photo.setIcon(icon);

		send = new JPanel();
		friendname = new Reservename();
		inputJPanel = new InputJPanel(username, clientMainFrame);
		sousuo = new Search();
		friendsJpanel = new FriendJPanel();
		chatJPanel = new ChatJPanel();


		function = new JPanel();
		function.setLayout(new BoxLayout(function, BoxLayout.Y_AXIS));
		function.setBounds(0, 0, 60, 625);
		function.setBackground(new Color(50, 50, 50));

		news = new JLabel();
		icon20.setImage(icon20.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
		icon2.setImage(icon2.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
		news.setIcon(icon2);
		news.setBorder((BorderFactory.createEmptyBorder(25, 16, 10, 15)));
		news.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				news.setIcon(icon20);
				friends.setIcon(icon3);
				addfriends.setIcon(icon4);
				int k = 0;
				for (int i=0;i<20;i++){
					if (!ChatList_Seleted.equals("abc")) {
						if (yonghu_name[i].equals(ChatList_Seleted)) {
							k = i;
							break;
						}
					}
				}
				clientMainFrame.remove(panel);
				clientMainFrame.remove(friendsJpanel);
				clientMainFrame.remove(send);
				clientMainFrame.getContentPane().add(chatJPanel);
				if (!ChatList_Seleted.equals("abc")) {
					clientMainFrame.getContentPane().add(chat[k]);
					chat[k].updateChatPanel();
					clientMainFrame.getContentPane().add(friendname);
					clientMainFrame.getContentPane().add(inputJPanel);
				}
				clientMainFrame.repaint();
				clientMainFrame.pack();
			}

			public void mouseEntered(MouseEvent e) {
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			public void mouseExited(MouseEvent e) {
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

		});

		friends = new JLabel();
		friends.setSize(40, 40);
		friends.setIcon(icon3);
		icon30.setImage(icon30.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH));
		icon3.setImage(icon3.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH));
		friends.setBorder((BorderFactory.createEmptyBorder(25, 15, 10, 15)));
		friends.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				friends.setIcon(icon30);
				news.setIcon(icon2);
				addfriends.setIcon(icon4);
				int k = 0;
				for (int i=0;i<20;i++){
					if (!ChatList_Seleted.equals("abc")) {
						if (yonghu_name[i].equals(ChatList_Seleted)) {
							k = i;
							break;
						}
					}
				}
				clientMainFrame.remove(panel);
				if (chat[k]!=null) {
					clientMainFrame.remove(chat[k]);
				}
				clientMainFrame.remove(friendname);
				clientMainFrame.remove(inputJPanel);
				clientMainFrame.remove(chatJPanel);
				clientMainFrame.getContentPane().add(friendsJpanel);
				if (FriendList_Seleted!=null){
					clientMainFrame.getContentPane().add(send);
				}
				clientMainFrame.repaint();
				clientMainFrame.pack();
			}

			public void mouseEntered(MouseEvent e) {
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			public void mouseExited(MouseEvent e) {
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		});

		addfriends = new JLabel();
		addfriends.setIcon(icon4);
		icon40.setImage(icon40.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
		icon4.setImage(icon4.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
		addfriends.setBorder((BorderFactory.createEmptyBorder(25, 20, 0, 15)));
		addfriends.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				addfriends.setIcon(icon40);
				news.setIcon(icon2);
				friends.setIcon(icon3);
				add.setVisible(true);
			}

			public void mouseEntered(MouseEvent e) {
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			public void mouseExited(MouseEvent e) {
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

		});
		function.add(user_photo);
		function.add(news);
		function.add(Box.createVerticalStrut(10));
		function.add(friends);
		function.add(Box.createVerticalStrut(10));
		function.add(addfriends);


		FriendList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int k = 0;
				for (int i=0;i<20;i++){
					if (!ChatList_Seleted.equals("abc")) {
						if (yonghu_name[i].equals(ChatList_Seleted)) {
							k = i;
							break;
						}
					}
				}
				clientMainFrame.remove(panel);
				if(chat[k]!=null){
					clientMainFrame.remove(chat[k]);
				}
				clientMainFrame.remove(friendname);
				clientMainFrame.remove(inputJPanel);

				File file = FriendList.getSelectedValue();
				String name = friendname.getname(file);
				FriendList_Seleted=name;
				friendname.setname(name);
				friendname.setfriendname(name);
				inputJPanel.setUsername(name);

				ImageIcon icon = new ImageIcon("D:\\Socket\\"+username+"\\"+FriendList_Seleted+".png.png");
				icon.setImage(icon.getImage().getScaledInstance(180,180,Image.SCALE_SMOOTH));
				User_id.setIcon(icon);
				friend_name.setText(name);

				clientMainFrame.getContentPane().add(send);
				clientMainFrame.repaint();
				clientMainFrame.pack();
			}
		});

		User_id = new JLabel(" ");
		User_id.setBounds(183, 100, 180, 180);
		separate_line1 = new JLabel(" ");
		separate_line1.setBounds(158,300,230,1);
		separate_line1.setOpaque(true);
		separate_line1.setBackground(new Color(225,225,225));
        bei_zhu = new JLabel("备   注",JLabel.CENTER);
		Font font = new Font("微软雅黑", Font.PLAIN, 14);
		bei_zhu.setFont(font);
		bei_zhu.setForeground(new Color(160, 160, 160));
        bei_zhu.setBounds(208,316,40,25);
        friend_name = new JLabel(" ",JLabel.LEFT);
		Font font2 = new Font("微软雅黑", Font.PLAIN, 15);
        friend_name.setFont(font2);
        friend_name.setBounds(278,314,80,30);
		separate_line2 = new JLabel(" ");
		separate_line2.setBounds(158,356,230,1);
		separate_line2.setOpaque(true);
		separate_line2.setBackground(new Color(225,225,225));
		send_news = new JLabel("发信息",JLabel.CENTER);
		send_news.setBounds(203, 397, 140, 35);
		send_news.setOpaque(true);
		Font font3 = new Font("微软雅黑", Font.PLAIN, 14);
		send_news.setFont(font3);
		send_news.setForeground(Color.white);
		send_news.setBackground(new Color(25, 180 ,30));
		send_news.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				news.setIcon(icon20);
				friends.setIcon(icon3);
				addfriends.setIcon(icon4);
		        int k = 0;
		        for (int i=0;i<20;i++){
			    if (yonghu_name[i].equals(FriendList_Seleted)){
				    k=i;
				    break;
			      }
		        }
		        ChatList_Seleted=FriendList_Seleted;
		        friendname.setname(ChatList_Seleted);
		        inputJPanel.setChatPane(chat[k]);

                File file = new File("D:\\Socket\\"+username+"\\"+FriendList_Seleted+".png.png");
                //判断JList里面是否有某个元素
                if (!chatmodel.contains(file)){
                	chatmodel.add(0,file);
					warnmodel.add(0,0);
                	ChatList_SeletedIndex=0;
                	updateTimeNews(FriendList_Seleted,1,0,null,null);
					ChatList.setModel(chatmodel);
                	ChatList.setCellRenderer(new ChatCellRender());
				}else {
                	ChatList_SeletedIndex = chatmodel.indexOf(file);
				}

				ChatList.setSelectedIndex(ChatList_SeletedIndex);//自动设置指定选择的index
				clientMainFrame.remove(send);
				clientMainFrame.remove(friendsJpanel);
				clientMainFrame.getContentPane().add(chatJPanel);
		        clientMainFrame.getContentPane().add(chat[k]);
				inputJPanel.setUsername(ChatList_Seleted);
				chat[k].updateChatPanel();
		        clientMainFrame.getContentPane().add(friendname);
	          	clientMainFrame.getContentPane().add(inputJPanel);
		        clientMainFrame.repaint();
		        clientMainFrame.pack();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				send_news.setBackground(new Color(25, 160 ,30));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				send_news.setBackground(new Color(25, 180 ,30));
			}
		});
		send.setBounds(310, 0, 555, 631);
		send.add(User_id);
		send.add(separate_line1);
		send.add(bei_zhu);
		send.add(friend_name);
		send.add(separate_line2);
		send.add(send_news);
		send.setBackground(new Color(245,245,245));
		send.setLayout(null);

		ChatList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				File file = ChatList.getSelectedValue();
				String name = friendname.getname(file);
				friendname.setname(name);
				inputJPanel.setUsername(name);
				int k = 0,j=0;
				for (int i = 0; i < 20; i++) {
					if (!name.equals("abc")&&yonghu_name[i]!=null) {
						if (yonghu_name[i].equals(ChatList_Seleted)) {
							k = i;
						}
					}
					if (!name.equals("abc")&&yonghu_name[i]!=null){
						if (yonghu_name[i].equals(name)) {
							j = i;
						}
				    }
				}

				clientMainFrame.remove(panel);
				ChatList_Seleted = name;
				friendname.setname(ChatList_Seleted);
				inputJPanel.setUsername(ChatList_Seleted);
				clientMainFrame.remove(chat[k]);
				clientMainFrame.getContentPane().add(chat[j]);
				inputJPanel.setChatPane(chat[k]);
				chat[j].updateChatPanel();
				clientMainFrame.getContentPane().add(inputJPanel);
				clientMainFrame.getContentPane().add(friendname);
				clientMainFrame.pack();
				clientMainFrame.repaint();
			}
		});

		this.getContentPane().add(function);
		this.getContentPane().add(friendsJpanel);
		this.getContentPane().add(sousuo);
		this.setPreferredSize(new Dimension(865, 631));
		this.setLocation(250, 50);
		this.setLayout(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
		this.pack();
		this.setResizable(false);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				try {
					File file = new File("D:\\Socket\\" + username + "_chatrecord\\chat.txt");
					FileWriter fr = new FileWriter(file);
					fr.write("");
					fr.flush();
					fr.close();
					int size = chatmodel.getSize();
					if (size!=0) {
						for (int i = 0; i < size; i++) {
							String[] text = chatmodel.getElementAt(i).getName().split(".png");
							String name = text[0];
							filetool.file_writer("D:\\Socket\\" + username + "_chatrecord\\chat.txt", name);
						}
					}
				}catch (IOException b){
					b.printStackTrace();
				}
			}
		});
	}

	//用户列表面板
	public class FriendJPanel extends JPanel {
		public FriendJPanel() {
			FriendJScrollPane = new JScrollPane(FriendList);
			FriendList.setBackground(new Color(235, 235, 235));
			FriendList.setFixedCellHeight(60);
			FriendList.setBorder(new EmptyBorder(0, 0, 0, 0));
			FriendJScrollPane.setBounds(0, 40, 250, 525);
			FriendJScrollPane.setOpaque(false);
			FriendJScrollPane.setBorder(null);
			FriendJScrollPane.getViewport().setOpaque(false);

			User_list = new JPanel();
			User_list.setBounds(0, 0, 250, 40);
			User = new JLabel("朋友列表");
			Font font = new Font("微软雅黑", Font.PLAIN, 12);
			User.setFont(font);
			User.setForeground(new Color(160, 160, 160));
			User.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 15));
			User_list.add(User);
			User_list.setLayout(new BoxLayout(User_list, BoxLayout.X_AXIS));
			User_list.setBackground(new Color(235, 235, 235));

			this.add(FriendJScrollPane);
			this.add(User_list);
			this.setBounds(60, 60, 250, 565);
			this.setLayout(null);
		}
	}

	//聊天记录列表
	public class ChatJPanel extends JPanel{
		public ChatJPanel(){
			ChatRecord = new JScrollPane(ChatList);
			ChatList.setBackground(new Color(235, 235, 235));
			ChatList.setFixedCellHeight(65);
			ChatList.setBorder(new EmptyBorder(0, 0, 0, 0));
			ChatRecord.setBounds(0,0,250,565);
			ChatRecord.setOpaque(false);
			ChatRecord.setBorder(null);
			ChatRecord.getViewport().setOpaque(false);

			this.add(ChatRecord);
			this.setBounds(60,60,250,565);
			this.setLayout(null);
		}
	}
	//搜索好友功能面板
	public class Search extends JPanel {
		private ImageIcon icon_detele = new ImageIcon("D:\\Socket\\tubiao\\shanchu.png");
		private ImageIcon icon_search = new ImageIcon("D:\\Socket\\tubiao\\sousuo2.png");
		private ImageIcon icon_search2 = new ImageIcon("D:\\Socket\\tubiao\\tianjia.png");
		private JPanel delete_JPanel, search_JPanel, search_JPanel2;
		private JLabel search_JLabel, delete_JLabel, search_JLabel2;

		public Search() {

			delete_JPanel = new JPanel();
			search_JPanel = new JPanel();

			search_JPanel.setBounds(10, 24, 24, 23);
			search_JLabel = new JLabel();
			search_JLabel.setOpaque(true);
			search_JLabel.setBounds(8, 7, 16, 16);
			icon_search.setImage(icon_search.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
			search_JLabel.setIcon(icon_search);
			search_JPanel.add(search_JLabel);

			Search_Field = new JTextField();
			Font font = new Font("微软雅黑", Font.PLAIN, 12);
			Search_Field.setFont(font);
			Search_Field.setBorder(null);
			Search_Field.setBounds(34, 24, 143, 23);
			Search_Field.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent e) {
					Search_Field.setText("");
					Search_Field.setBackground(Color.white);
					Search_Field.setForeground(Color.black);
					search_JLabel.setBackground(Color.white);
					search_JPanel.setBackground(Color.white);
					delete_JLabel.setBackground(Color.white);
					delete_JPanel.setBackground(Color.white);
					delete_JPanel.add(delete_JLabel);
				}

				@Override
				public void focusLost(FocusEvent e) {
					String text = "搜索";
					Search_Field.setForeground(Color.gray);
					Search_Field.setBackground(new Color(215, 215, 215));
					Search_Field.setText(text);
					search_JLabel.setBackground(new Color(215, 215, 215));
					search_JPanel.setBackground(new Color(215, 215, 215));
					delete_JPanel.setBackground(new Color(215, 215, 215));
					delete_JPanel.remove(delete_JLabel);
				}
			});

			delete_JPanel.setBounds(177, 24, 26, 23);
			delete_JLabel = new JLabel();
			delete_JLabel.setOpaque(true);
			delete_JLabel.setBounds(10, 5, 13, 13);
			delete_JLabel.addMouseListener(new MouseAdapter() {
				public void mouseEntered(MouseEvent e) {
					setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}

				public void mouseExited(MouseEvent e) {
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}

				@Override
				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);
					Search_Field.setText("");
				}
			});
			icon_detele.setImage(icon_detele.getImage().getScaledInstance(12, 12, Image.SCALE_SMOOTH));
			delete_JLabel.setIcon(icon_detele);

			search_JPanel2 = new JPanel();
			search_JPanel2.setBounds(213, 24, 25, 23);
			search_JPanel2.setBackground(new Color(215, 215, 215));
			search_JLabel2 = new JLabel();
			search_JLabel2.setOpaque(true);
			search_JLabel2.setBounds(15, 12, 11, 11);
			search_JLabel2.setBackground(new Color(215, 215, 215));
			search_JLabel2.addMouseListener(new MouseAdapter() {
				public void mouseEntered(MouseEvent e) {
					setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}

				public void mouseExited(MouseEvent e) {
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}

				@Override
				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);
					Color a =new Color(215, 215, 215);
					String text = Search_Field.getText();
					if (text.trim().length()!=0&&!Search_Field.getBackground().toString().equals(new Color(215, 215, 215).toString())){
						int k = 0;
						for (int i=0;i<20;i++){
							if (yonghu_name[i]!=null) {
								if (yonghu_name[i].equals(text)) {
									k = 1;
									break;
								}
							}
						}
						if (k==0){
							JOptionPane.showMessageDialog(clientMainFrame,"没有该用户的聊天记录","搜索失败",JOptionPane.WARNING_MESSAGE);
						}else if(k==1) {
							File file = new File("D:\\Socket\\"+username+"\\"+text+".png.png");
							if (!chatmodel.contains(file)){
								chatmodel.add(0,file);
								warnmodel.add(0,0);
								ChatList.setModel(chatmodel);
								ChatList_SeletedIndex=0;
								updateTimeNews(text,1,0,null,null);
								ChatList.setCellRenderer(new ChatCellRender());
							}else {
								ChatList_SeletedIndex = chatmodel.indexOf(file);
							}
							ChatList.setSelectedIndex(ChatList_SeletedIndex);
							clientMainFrame.search_ChatRecord(text);
							Search_Field.setText("");
						}
					}else{
						JOptionPane.showMessageDialog(clientMainFrame,"搜索信息不能为空","搜索失败",JOptionPane.WARNING_MESSAGE);
					}
				}
			});
			icon_search2.setImage(icon_search2.getImage().getScaledInstance(11, 11, Image.SCALE_SMOOTH));
			search_JLabel2.setIcon(icon_search2);
			search_JPanel2.add(search_JLabel2);

			this.add(search_JPanel);
			this.add(Search_Field);
			this.add(delete_JPanel);
			this.add(search_JPanel2);
			this.setBounds(60, 0, 250, 60);
			this.setBackground(new Color(235, 235, 235));
			this.setLayout(null);
		}
	}

	//初始化用户方法
	public void init_user() {
		if (filetool.isdirectory("D:\\Socket")) {
			System.out.println("Socket文件夹已存在（存储图片、聊天记录等各种资源）");
		} else {
			filetool.create_directory("D:\\Socket");
			System.out.println("Socket文件夹已创建（存储图片、聊天记录等各种资源）");
		}
		if (filetool.isdirectory("D:\\Socket\\File")) {
			System.out.println("File文件夹已存在（存储图片、聊天记录等各种资源）");
		} else {
			filetool.create_directory("D:\\Socket\\File");
			System.out.println("File文件夹已创建（存储图片、聊天记录等各种资源）");
		}
		if (filetool.isdirectory("D:\\Socket\\"+username+"_chatrecord")) {
			System.out.println("聊天记录文件夹已存在");
		} else {
			filetool.create_directory("D:\\Socket\\"+username+"_chatrecord");
			filetool.create_file("D:\\Socket\\"+username+"_chatrecord\\chat.txt");
			System.out.println("聊天记录文件夹已创建");
		}
		if (filetool.isdirectory("D:\\Socket\\" + username)) {
			System.out.println("用户头像文件夹已存在");
			getfriends_file();
			init_ChatRecord();
		} else {
			filetool.create_directory("D:\\Socket\\" + username);
			new getfriends();
			System.out.println("用户头像文件夹已创建");
		}
	}
	//初始化聊天记录列表
	public void init_ChatRecord(){
		File file = new File("D:\\Socket\\"+username+"_chatrecord\\chat.txt");
		String[] text = filetool.file_reader("D:\\Socket\\"+username+"_chatrecord\\chat.txt");
		System.out.println("chat:  "+text[0]);
		if (!text[0].equals("0")){
			int lines = Integer.parseInt(text[0]);
			for(int i=1 ;i<=lines;i++) {
				File file1 = new File("D:\\Socket\\"+username+"\\"+text[i]+".png.png");
				chatmodel.addElement(file1);
				warnmodel.addElement(0);
				updateTimeNews(text[i],0,0,null,null);
			}
			ChatList.setModel(chatmodel);
			ChatList.setCellRenderer(new ChatCellRender());
		}
	}

	//从本地获取用户头像
	public void getfriends_file() {
		File[] files = new File("D:\\Socket\\" + username).listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.getName().endsWith("png");
			}
		});
		int lenth = files.length;
		if (lenth!=0) {
			for (int i = 0; i < lenth; i++) {
				user_number++;
				friendmodel.addElement(files[i]);
				String[] text = files[i].getName().split(".png");
				yonghu_name[i] = text[0];
				chat[i] = new ChatPanel(username, text[0]);
			}
		}
		FriendList.setModel(friendmodel);
		FriendList.setCellRenderer(new ImageCellRender());
	}

	//获取登录账号的头像
	public static class get_user_photo extends Thread {
		private Socket socket;
		public String friendname,dir;
		public get_user_photo(String name,String dir){
			this.friendname=name;
			this.dir=dir;
			a=0;
		}
		public void run() {
			while (true) {
				try {
					socket = new Socket(Server_ip, Port);
					if (socket != null) {
						break;
					}
				} catch (IOException e) {
					e.printStackTrace();
					continue;
				}
				try {
					Thread.sleep(150);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
			System.out.println("连接获取好有列表成功");

			try {
				DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
				outputStream.writeUTF("single&&" + friendname);
				outputStream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}

			while (true) {
				try {
					DataInputStream input = new DataInputStream(socket.getInputStream());
					if (input.available() > 0) {
						String text = input.readUTF();
						if(text.equals("&&end")) {
							input.close();
							return;
						}
						String[] information = text.split("&&");
						String user;
						long length;
						user = information[0];
						length = Long.parseLong(information[1]);
						if (user != null && length != 0) {
							filetool.create_file(dir+ user + ".png");
							File file = new File(dir+ user + ".png");
							FileOutputStream fos = new FileOutputStream(file);
							byte[] inputByte = new byte[1024];
							long imglength = 0;
							int readlength;
							while (true) {
								if (input.available()>0) {
									readlength = input.read(inputByte, 0, inputByte.length);
									fos.write(inputByte, 0, readlength);
									fos.flush();
									imglength += readlength;
									if (imglength >= length) {
										fos.close();
										System.out.println("传输完成");
										if (dir.equals("D:\\Socket\\" + username + "\\")) {
											friendmodel.addElement(file);
											filetool.create_file("D:\\Socket\\" + username + "_chatrecord\\" + friendname + ".txt");
											yonghu_name[user_number] = friendname;
											chat[user_number] = new ChatPanel(username, friendname);
											user_number++;
										} else {
											b = 1;
										}
										break;
									}
								}
							}
							a=1;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	//对用户列表进行渲染
	public static class ImageCellRender extends  JPanel implements ListCellRenderer{
		//继承面板必须对所加的组件进行固定
		private final JLabel photo = new JLabel();
		private final JLabel LatestNews = new JLabel();
		@Override
		public Component getListCellRendererComponent(JList list,Object value,int index,boolean isSelected,boolean cellHasFocus) {
			if (value instanceof File) {
				File imageFile = (File) value;
				try {
				ImageIcon icon = new ImageIcon(imageFile.toURI().toURL());
			    icon.setImage(icon.getImage().getScaledInstance(35, 35, Image.SCALE_DEFAULT));
			    String[] text = imageFile.getName().split(".png");
			    Font font = new Font("微软雅黑", Font.PLAIN, 15);//创建1个字体实例
			    photo.setIcon(icon);
			    photo.setIconTextGap(13);
			    photo.setText(text[0]);
			    photo.setFont(font);//设置JLabel的字体
			    photo.setVerticalTextPosition(SwingConstants.CENTER);
				photo.setHorizontalTextPosition(SwingConstants.RIGHT);
				photo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10 ,10));

			}catch(Exception e) {
				e.printStackTrace();
			}
	}
			Color background = null;
	        Color foreground = new Color(0, 0, 0);
	        /*if(warn_signal[index]==1){
	                        background = Color.CYAN;
	                        foreground = Color.WHITE;           
	                
	        }
	       */
	        if(isSelected){
	            background = new Color(210, 210, 210);
	        }    
	        setBackground(background);
	        setForeground(foreground);
	        this.add(photo);
	        this.setLayout(new BoxLayout(this,BoxLayout.X_AXIS));//为面板设置布局才可以设置边距
	        return this;
	            } 
		}

	//对聊天记录列表进行渲染
	public static class ChatCellRender extends JPanel implements ListCellRenderer{
		private final JLabel photo = new JLabel();
		private final JLabel LatestNews = new JLabel("",JLabel.LEFT);
		private final JLabel name = new JLabel("",JLabel.LEFT);
		private final JLabel time = new JLabel("",JLabel.RIGHT);
		private final JLabel warn = new JLabel();
		private final ImageIcon icon1 = new ImageIcon("D:\\Socket\\tubiao\\tixing.png");

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			if (value instanceof File){
				File imagefile = (File) value;
				try{
					ImageIcon icon = new ImageIcon(imagefile.toURI().toURL());
					icon.setImage(icon.getImage().getScaledInstance(40, 40, Image.SCALE_DEFAULT));
					photo.setIcon(icon);
					photo.setBounds(10,12,40,40);
					String[] text = imagefile.getName().split(".png");
					Font font = new Font("微软雅黑", Font.PLAIN, 15);//创建1个字体实例
					name.setText(text[0]);
					name.setFont(font);
					name.setBounds(60,10,80,20);
					Font font1 = new Font("微软雅黑", Font.PLAIN, 12);
					LatestNews.setFont(font1);
					LatestNews.setForeground(new Color(160, 160, 160));
					    try {
							if (newsmodel.getElementAt(index).equals("0")) {
								LatestNews.setText("");
							} else {
								LatestNews.setText(newsmodel.getElementAt(index));
							}
						}catch (ArrayIndexOutOfBoundsException e){
					    	e.printStackTrace();
						}

					LatestNews.setBounds(60,32,150,20);
					Font font2 = new Font("微软雅黑", Font.PLAIN, 12);
					time.setForeground(new Color(160, 160, 160));
					try {
						if (newsmodel.getElementAt(index).equals("0")){
							time.setText("");
						}else {
							time.setText(timemodel.getElementAt(index));
						}
					}catch (ArrayIndexOutOfBoundsException e){
						e.printStackTrace();
					}

					time.setFont(font2);
					time.setBounds(158,7,80,20);
				}catch (Exception e){
					e.printStackTrace();
				}
			}
			icon1.setImage(icon1.getImage().getScaledInstance(13,13,Image.SCALE_SMOOTH));
			warn.setBounds(45,2,13,13);
			warn.setIcon(icon1);
			warn.setVisible(false);
			Color background = null;

			if(isSelected){
				background = new Color(210, 210, 210);
				warn.setVisible(false);
				clientMainFrame.set_warn(0,index);
			}
			try {
				if (warnmodel.getElementAt(index) == 1) {
					warn.setVisible(true);
				}
			}catch (ArrayIndexOutOfBoundsException e){
				e.printStackTrace();
			}
			this.setBackground(background);
			this.add(photo);
			this.add(name);
			this.add(LatestNews);
			this.add(time);
			this.add(warn);
            this.setLayout(null);
			return this;
		}
	}


	//向服务器获取用户头像
	public static class getfriends extends Thread{
		private Socket socket;
		public getfriends() {
			this.start();
		}
		public void run(){
			while(true) {
				try {
					socket = new Socket(Server_ip,Port);
					if(socket!=null) {
						break;
					}
				}catch(IOException e) {
					e.printStackTrace();
					continue;
				}
				try {
					Thread.sleep(100);
				}catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("连接获取好有列表成功");

			try {
				DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
				outputStream.writeUTF("all&&");
				outputStream.flush();
			}catch (IOException e){
				e.printStackTrace();
			}
			int i=0;
			while(true) {
				try {
					DataInputStream input = new DataInputStream(socket.getInputStream());
					if (input.available() > 0) {
						String text = input.readUTF();
						if (!text.equals("&&")){
							System.out.println("huoqu:2 " + text);
						if (text.equals("&&end")) {
							input.close();
							return;
						}
						String[] information = text.split("&&");
						String user;
						long length;
						user = information[0];
						length = Long.parseLong(information[1]);
						if (user != null && length != 0) {
							filetool.create_file("D:\\Socket\\" + username + "\\" + user + ".png");
							File file = new File("D:\\Socket\\" + username + "\\" + user + ".png");
							FileOutputStream fos = new FileOutputStream(file);
							byte[] inputByte = new byte[1024];
							long imglength = 0;
							int readlength = 0;
							while (true) {
								if (input.available()>0)
								{
									readlength = input.read(inputByte, 0, inputByte.length);
									fos.write(inputByte, 0, readlength);
									fos.flush();
									imglength += readlength;
									if (imglength >= length) {
										fos.close();
										System.out.println("传输成功");
										break;
									}
								}
							}
							user_number++;
							System.out.println("当前获取到的头像个数："+user_number);
							String[] user2 = user.split(".png");
							filetool.create_file("D:\\Socket\\" + username + "_chatrecord\\" + user2[0] + ".txt");
							chat[i] = new ChatPanel(username, user2[0]);
							chat[i].updateChatPanel();
							yonghu_name[i] = user2[0];
							i++;
							friendmodel.addElement(file);
							FriendList.setModel(friendmodel);
							FriendList.setCellRenderer(new ImageCellRender());
						}
					}
				}
				}catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	//接收好友发过来的信息
	public static class ReceiveMsg extends Thread{
	    public ReceiveMsg(){
	        this.start();
	    }
	    @Override
	    public void run() {
	        super.run();
	        while (true){
	            try {
	                Thread.sleep(100);
	                String msg = null;
	                DataInputStream dataInputStream= new DataInputStream(receiveSocket.getInputStream());
	                DataOutputStream dataOutputStream = new DataOutputStream(receiveSocket.getOutputStream());
	                if(dataInputStream.available()>0)
	                {
	                    msg=dataInputStream.readUTF();
	                    System.out.println(msg);
	                }
	                
	                if(!"&&".equals(msg)&&msg!=null) {
	                	System.out.println("接收的信息为："+msg);
	                    String type;//消息类型
	                    String sender;//发送者
	                    String time;//时间
	                    String content;//内容
						String size;//文件大小
	                    String[] infos = msg.split("&&");
	                    type=infos[0];
						sender=infos[1];
						time=infos[2];
						content=infos[3];
						size=infos[4];
	                    System.out.println("接收成功");
	                    if (type.equals("add_return")){
	                    	System.out.println(sender);
                           clientMainFrame.getSingle(sender,"D:\\Socket\\"+username+"\\");
						}

	                    if (type.equals("text")){
							try {
								clientMainFrame.add_time(sender,time);
								filetool.file_writer("D:\\Socket\\"+username+"_chatrecord\\" + sender + ".txt", "text&&" + sender + "&&" + time);
								clientMainFrame.add_message(sender,content);
								filetool.file_writer("D:\\Socket\\"+username+"_chatrecord\\"+ sender + ".txt", content);
								File file = new File("D:\\Socket\\"+username+"\\"+sender+".png.png");
								//判断JList里面是否有某个元素
								if (!chatmodel.contains(file)){
									chatmodel.add(0,file);
									warnmodel.add(0,1);
									clientMainFrame.voice_warn();
									clientMainFrame.updateTimeNews(sender,1,0,time,content);
									ChatList.setModel(chatmodel);
									ChatList.setCellRenderer(new ChatCellRender());
								}else {
									int index =clientMainFrame.getChatRecord_index(sender);
									clientMainFrame.updateTimeNews(sender,0,index,time,content);
									clientMainFrame.voice_warn();
									clientMainFrame.set_warn(1,index);
								}
							}catch (IOException a){
								a.printStackTrace();
							}
	                    }
	                    if (type.equals("file")){
	                    	try {
	                    		clientMainFrame.add_time(sender,time);
                                clientMainFrame.add_file(sender,content,size);
								filetool.file_writer("D:\\Socket\\"+username+"_chatrecord\\"+sender+".txt","file&&"
										+sender+"&&"+time+"&&"+content+"&&"+size);
								filetool.file_writer("D:\\Socket\\"+username+"_chatrecord\\"+sender+".txt",content);
								File file = new File("D:\\Socket\\"+username+"\\"+sender+".png.png");
								//判断JList里面是否有某个元素
								if (!chatmodel.contains(file)){
									chatmodel.add(0,file);
									warnmodel.add(0,1);
									ChatList.setModel(chatmodel);
									clientMainFrame.voice_warn();
									clientMainFrame.updateTimeNews(sender,1,0,time,"[文件]");
									ChatList.setCellRenderer(new ChatCellRender());
								}else {
									int index =clientMainFrame.getChatRecord_index(sender);
									clientMainFrame.updateTimeNews(sender,0,index,time,"[文件]");
									clientMainFrame.voice_warn();
									clientMainFrame.set_warn(1,index);
								}
							}catch (IOException b){
	                    		b.printStackTrace();
							}
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
}