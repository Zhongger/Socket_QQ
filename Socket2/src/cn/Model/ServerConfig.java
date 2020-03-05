package cn.Model;

public class ServerConfig {
    private static final String host="192.168.43.152";
    private static final String voiceServerHost="192.168.43.192";
    private static final int loginPort=1248;
    private static final int sendPort=1246;
    private static final int receivePort=1244;
    private static final int friendPort=1242;
    public static final int addPort=1240;

    public static String getVoiceServerHost() {
        return voiceServerHost;
    }

    public static String getHost() {
        return host;
    }

    public static int getLoginPort() {
        return loginPort;
    }

    public static int getSendPort() {
        return sendPort;
    }

    public static int getReceivePort() {
        return receivePort;
    }

    public static int getFriendPort() {
        return friendPort;
    }

    public static int getAddPort() {
        return addPort;
    }
}
