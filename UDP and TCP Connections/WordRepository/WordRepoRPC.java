import java.net.Inet4Address;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class WordRepoRPC {
    public static void main(String[] args) {
        try {
            // Create and export the remote object
            WordRepo wordRepo = new WordRepoImpl();
            // WordRepo exportObject = (WordRepo) UnicastRemoteObject.exportObject(wordRepo, 0);

            // Start the RMI registry on port 1099
            try {
                LocateRegistry.getRegistry(1099).list();
            } catch (RemoteException e) {
                LocateRegistry.createRegistry(1099);
            }

            // Bind the remote object to the registry with the name "WordRepo"
            String URL = "rmi://" + InetAddress.getLocalHost().getHostAddress() + ":1099/WordRepo";
            Naming.rebind(URL, wordRepo);

            System.out.println("WordRepo Server is running.");
        } catch (Exception e) {
            System.err.println("WordRepo Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
