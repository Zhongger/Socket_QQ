package cn.Service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class VoiceServer {
    public static int receivePort = 1111;
    public static int pushPort = 2222;
    private ServerSocket receiveserverSocket;
    private ServerSocket pushserverSocket;
    private HashMap<String, Socket> receiveSocketMap;
    private HashMap<String, Socket> pushSocketMap;

    public VoiceServer() {
        try {
            //接收连接请求和客户端发来的消息
            receiveserverSocket = new ServerSocket(receivePort);
            //将客户端A发来的消息推送给客户端B
            pushserverSocket = new ServerSocket(pushPort);
            receiveSocketMap = new HashMap<>();
            pushSocketMap = new HashMap<>();
            new waitConnection(receiveserverSocket, pushserverSocket).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 等待连接
     */
    private class waitConnection extends Thread {
        private ServerSocket receiveserverSocket;
        private ServerSocket pushserverSocket;
        private Socket receivesocket;
        private Socket pushSocket;
        private DataInputStream inputStream;


        public waitConnection(ServerSocket receiveserverSocket, ServerSocket pushserverSocket) throws IOException {
            this.receiveserverSocket = receiveserverSocket;
            this.pushserverSocket = pushserverSocket;

        }

        @Override
        public void run() {
            while (true) {
                try {
                    pushSocket = this.pushserverSocket.accept();
                    System.out.println("连接了推送Socket");
                    receivesocket = this.receiveserverSocket.accept();
                    System.out.println("连接了接收Socket");

                    if (receivesocket!=null){
                        Thread thread = new Thread(new NotifyHandler(receivesocket, pushSocket, receiveSocketMap, pushSocketMap));
                        thread.setDaemon(true);
                        thread.start();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * 处理消息的类
     */
    private class NotifyHandler extends Thread{
        private Socket receivesocket=null;
        private Socket pushSocket=null;
        private HashMap<String,Socket> receiveSocketMap=null;
        private HashMap<String,Socket> pushSocketMap=null;
        private DataInputStream inputStream=null;
        private DataOutputStream outputStream=null;
        public NotifyHandler(Socket receivesocket,Socket pushSocket,HashMap<String,Socket> receiveSocketMap,HashMap<String,Socket> pushSocketMap){
            this.receivesocket=receivesocket;
            this.pushSocket=pushSocket;
            this.receiveSocketMap=receiveSocketMap;
            this.pushSocketMap=pushSocketMap;
        }

        @Override
        public void run() {
            try {
                while (true){
                    Thread.sleep(200);
                    inputStream=new DataInputStream(receivesocket.getInputStream());
                    if (inputStream.available()>0){
                        String msg = inputStream.readUTF();
                        String[] splitMsg = msg.split("&&");
                        if (splitMsg.length == 1) {
                            //处理连接到服务器时，客户端会把自己的用户名发过来
                            receiveSocketMap.put(splitMsg[0], receivesocket);

                            pushSocketMap.put(splitMsg[0], pushSocket);



                            System.out.println("连接服务端口1111"+receiveSocketMap);
                            System.out.println("连接服务端口2222"+pushSocketMap);
                        }else if (splitMsg.length==3){
                            System.out.println(msg);
                            //处理请求者发送的消息
                            //消息的格式为type&&requesterUsername&&targetUsername
                            String type = splitMsg[0];
                            System.out.println(type);
                            String requesterUsername = splitMsg[1];
                            System.out.println(requesterUsername);
                            String targetUsername = splitMsg[2];
                            System.out.println(targetUsername);
                            if ("voice".equals(type)){
                                //转发消息
                                //获取到目标用户的Socket
                                Socket targetSocket = pushSocketMap.get(targetUsername);
                                outputStream = new DataOutputStream(targetSocket.getOutputStream());
                                outputStream.writeUTF(type+"&&"+requesterUsername); //服务器将请求转发给另外一个客户端
                                outputStream.flush();
                                System.out.println("转发成功");
                            }
                        }else if (splitMsg.length==4){
                            //处理目标用户发来的同意请求
                            String ip=splitMsg[0];//目标用户的ip地址
                            System.out.println(ip);
                            String port=splitMsg[1];//目标用户的端口
                            System.out.println(port);
                            String requesterUsername=splitMsg[2];//请求方的用户名
                            System.out.println(requesterUsername);
                            String flag=splitMsg[3];//true/false，用于占位
                            System.out.println(flag);
                            if ("false".equals(flag)){
                                Socket requesterSocket = pushSocketMap.get(requesterUsername);
                                outputStream = new DataOutputStream(requesterSocket.getOutputStream());
                                outputStream.writeUTF("no");
                            }
                            if ("true".equals(flag)){
                                //若目标用户同意通话，则将其ip和port发送给请求方，让两者建立连接
                                Socket requesterSocket = pushSocketMap.get(requesterUsername);
                                System.out.println(requesterSocket);
                                System.out.println(msg);
                                outputStream = new DataOutputStream(requesterSocket.getOutputStream());
                                outputStream.writeUTF(ip+"&&"+port);
                                outputStream.flush();
                                System.out.println("ip和端口推送成功");
                            }
                        }
                    }else {
                        continue;
                    }
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public static void main(String[] args) {
        new VoiceServer();
    }
}
                    /*if (receivesocket!=null){
                        inputStream=new DataInputStream(receivesocket.getInputStream());
                        String msg = inputStream.readUTF();
                        String[] splitMsg = msg.split("&&");
                        if (splitMsg.length==1){
                            //处理连接到服务器时，客户端会把自己的用户名发过来
                            receiveSocketMap.put(splitMsg[0],receivesocket);
                            pushSocketMap.put(splitMsg[0],pushSocket);

                        }else if (splitMsg.length==3){
                            System.out.println(msg);
                            //处理请求者发送的消息
                            //消息的格式为type&&requesterUsername&&targetUsername
                            String type = splitMsg[0];
                            System.out.println(type);
                            String requesterUsername = splitMsg[1];
                            System.out.println(requesterUsername);
                            String targetUsername = splitMsg[2];
                            System.out.println(targetUsername);
                            if ("voice".equals(type)){
                                //转发消息
                                //获取到目标用户的Socket
                                Socket targetSocket = pushSocketMap.get(targetUsername);
                                DataOutputStream outputStream = new DataOutputStream(targetSocket.getOutputStream());
                                outputStream.writeUTF(type+"&&"+requesterUsername); //服务器将请求转发给另外一个客户端
                                outputStream.flush();
                                System.out.println("转发成功");
                            }
                        }else if (splitMsg.length==4){
                            //处理目标用户发来的同意请求
                            String ip=splitMsg[0];//目标用户的ip地址
                            String port=splitMsg[1];//目标用户的端口
                            String requesterUsername=splitMsg[2];//请求方的用户名
                            String flag=splitMsg[3];//true/false，用于占位
                            if ("false".equals(flag)){
                                Socket requesterSocket = pushSocketMap.get(requesterUsername);
                                DataOutputStream outputStream = new DataOutputStream(requesterSocket.getOutputStream());
                                outputStream.writeUTF("no");
                            }
                            //若目标用户同意通话，则将其ip和port发送给请求方，让两者建立连接
                            Socket requesterSocket = pushSocketMap.get(requesterUsername);
                            DataOutputStream outputStream = new DataOutputStream(requesterSocket.getOutputStream());
                            outputStream.writeUTF(ip+"&&"+port);
                            outputStream.flush();
                        }

                    }*/

    /*

    private class receiveMsg extends Thread{
        //private ServerSocket serverSocket;
        private Socket socket;
        private DataInputStream inputStream;
        private DataOutputStream outputStream;
        private String socketUsername;
        public receiveMsg(Socket socket,String socketUsername){

            this.socket=socket;
            this.socketUsername=socketUsername;
        }

        @Override
        public void run() {
            while (true){
                try {
                    //Thread.sleep(500);

                    inputStream=new DataInputStream(socket.getInputStream());
                    String targetUsername = inputStream.readUTF();

                    String[] split = targetUsername.split("&&");

                    if (split.length!=1){
                        if (split[0].equals("yes")){
                            //outputStream.writeUTF("");

                            byte[] bos=new byte[2048];
                            byte[] bis=new byte[2048];
                            for (String key:pushSocketMap.keySet()){
                                if (key.equals(split[2])){
                                    Socket socket = pushSocketMap.get(key);
                                    outputStream=new DataOutputStream(socket.getOutputStream());
                                    outputStream.writeUTF("对方已经同意你的请求了");
                                    outputStream.flush();
                                }
                                if (key.equals(split[1])){
                                    Socket socket = pushSocketMap.get(key);//获取到对方的推送Socket,准备推送语音
                                    *//*TargetDataLine targetDataLine = AudioUtils.getTargetDataLine();
                                    SourceDataLine sourceDataLine = AudioUtils.getSourceDataLine();
                                    *//*
                                    outputStream=new DataOutputStream(socket.getOutputStream());
                                    int i=0;
                                    while (true){
                                        *//*System.out.println("server开始发了");
                                        //获取音频流
                                        int writeLen = targetDataLine.read(bos,0,bos.length);
                                        //推送将音频出去
                                        if (bos!=null){
                                            outputStream.write(bos,0,writeLen);
                                        }
                                        int read = inputStream.read(bis);
                                        //将音频获取到
                                        if (bis!=null){
                                            //播放对方发送来的音频
                                            System.out.println("服务端接收音频");
                                            sourceDataLine.write(bis, 0, read);
                                        }*//*
                                        String s = inputStream.readUTF();
                                        System.out.println("接收了:"+s);

                                        outputStream.writeUTF("发给你了"+i);
                                        i++;
                                    }

                                }

                            }
                            continue;
                        }
                        if (split[0].equals("no")){
                            System.out.println("refuse");
                            continue;
                        }
                    }
                    else {
                        for (String key:pushSocketMap.keySet()){
                            if (key.equals(targetUsername)){
                                Socket socket = pushSocketMap.get(key);//获取到对方的推送Socket
                                outputStream=new DataOutputStream(socket.getOutputStream());
                                outputStream.writeUTF("audio&&"+socketUsername);
                                outputStream.flush();
                                System.out.println("消息接收并推送成功");
                                break;
                            }
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }*/
