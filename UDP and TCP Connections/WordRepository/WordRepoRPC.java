import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class WordRepoServerMain {
    public static void main(String[] args) {
        try {
            // Create and export the remote object
            WordRepository wordRepo = new WordRepoServer();

            // Start the RMI registry on port 1099
            LocateRegistry.createRegistry(1099);

            // Bind the remote object to the registry with the name "WordRepo"
            Naming.rebind("WordRepo", wordRepo);

            System.out.println("WordRepo Server is running.");
        } catch (Exception e) {
            System.err.println("WordRepo Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
