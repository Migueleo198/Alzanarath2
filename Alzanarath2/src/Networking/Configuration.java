package Networking;

public class Configuration {
    private int port;
    private String ip;

    public Configuration(int port, String ip) {
        this.port = port;
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setIP(String ip) {
        this.ip = ip;
    }

    public String getIP() {
        return ip;
    }
}
