import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class UserAccountServerMain {
    public static void main(String[] args) {
        try {
            final int serverPort = 9879;
            UserAccountServerImpl server = new UserAccountServerImpl();
            LocateRegistry.createRegistry(serverPort);
            Naming.rebind("//localhost:" + serverPort + "/UserAccountServer", server);
            System.out.println("UserAccountServer is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
