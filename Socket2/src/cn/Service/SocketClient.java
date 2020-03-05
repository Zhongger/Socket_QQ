package cn.Service;

import cn.View.LoginFrame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SocketClient extends Thread{

    Socket socket=null;
    private String username;
    private String password;
    private int flag=0;
    private loginMsg lsg=null;
    public SocketClient(String host,int port,String username,String password){
        try {
            socket = new Socket(host, port);
            this.username=username;
            this.password=password;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();

        DataOutputStream dataOutputStream=null;
        DataInputStream dataInputStream=null;
        try {
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            dataInputStream = new DataInputStream(socket.getInputStream());

            dataOutputStream.writeUTF(username+"&&"+password);
            dataOutputStream.flush();
            String s=null;
            while (true){
                Thread.sleep(500);
                s = dataInputStream.readUTF();
                if (s!=null){
                    break;
                }
            }
            ExecutorService executorService = Executors.newFixedThreadPool(1);
            loginMsg loginMsg = new loginMsg(s);
            Future<Integer> submit = executorService.submit(loginMsg);
            Integer integer = submit.get();
            LoginFrame.loginFrame.setFlag(integer);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                dataOutputStream.close();
                dataInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    public class loginMsg implements Callable<Integer>{
        private String msg;
        public loginMsg(String msg){
            this.msg=msg;
        }
        @Override
        public Integer call() throws IOException {
            if (msg.equals("login successfully")){
                socket.close();
                return 1;
            }
            if (msg.equals("password incrrect")){
                socket.close();
                return -1;
            }
            if (msg.equals("username not exist")){
                socket.close();
                return 0;
            }
            return 886;
        }
    }

}
