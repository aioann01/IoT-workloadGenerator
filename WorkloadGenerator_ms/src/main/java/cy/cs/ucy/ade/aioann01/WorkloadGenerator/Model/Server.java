package cy.cs.ucy.ade.aioann01.WorkloadGenerator.Model;

public class Server {

    private String serverIp;

    private String serverPort;

    public Server() {
    }

    public Server(String serverIp, String serverPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }
}
