package cn.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class VoicingPanel extends JFrame {
    private Container container;
    private JPanel centralJpanel;
    private JPanel buttonJpanel;
    private JLabel usernameJlabel;
    private JLabel headImageJlabel;
    private JLabel progressJlabel;
    private JButton stopJbutton;
    private static VoicingPanel voicingPanel;
    public VoicingPanel(String targetUsername,InputJPanel inputJPanel,AcceptListener acceptListener){
        voicingPanel=this;
        container=this.getContentPane();
        centralJpanel=new JPanel(new BorderLayout());
        buttonJpanel=new JPanel();
        usernameJlabel=new JLabel(targetUsername);
        JPanel usernameJpanel=new JPanel(new BorderLayout());
        usernameJpanel.add(usernameJlabel,BorderLayout.CENTER);

        URL resource1 = VoicingPanel.class.getResource("/img/headImage.jpg");
        ImageIcon imageIcon1 = new ImageIcon(resource1);
        headImageJlabel=new JLabel();
        headImageJlabel.setIcon(imageIcon1);
        JPanel headImageJpanel=new JPanel(new BorderLayout());
        headImageJpanel.add(headImageJlabel,BorderLayout.CENTER);

        progressJlabel=new JLabel();
        URL resource2 = VoicingPanel.class.getResource("/img/progressImage.png");
        ImageIcon imageIcon2 = new ImageIcon(resource2);
        progressJlabel.setIcon(imageIcon2);
        JPanel progressJpanel=new JPanel(new BorderLayout());
        progressJpanel.add(progressJlabel,BorderLayout.CENTER);

        centralJpanel.add(usernameJpanel,BorderLayout.NORTH);
        centralJpanel.add(headImageJpanel,BorderLayout.CENTER);
        centralJpanel.add(progressJpanel,BorderLayout.SOUTH);

        URL resource3 = VoicingPanel.class.getResource("/img/buttonImage.png");
        ImageIcon imageIcon3 = new ImageIcon(resource3);
        stopJbutton=new JButton();
        stopJbutton.setIcon(imageIcon3);
        stopJbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource()==stopJbutton){
                    inputJPanel.closeAudioClient();
                    System.out.println("已经关闭audioClient");
                    acceptListener.closeAudioServer();
                    System.out.println("已经关闭audioServer");
                    voicingPanel.dispose();
                }
            }
        });
        buttonJpanel.add(stopJbutton);

        container.setLayout(new BorderLayout());
        container.add(centralJpanel,BorderLayout.CENTER);
        container.add(buttonJpanel,BorderLayout.SOUTH);

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setVisible(true);
        this.setSize(250,342);
        GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle rect=ge.getMaximumWindowBounds();
        int w=rect.width;
        int h=rect.height;
        this.setLocation(w-250,h-342);
    }


}
