import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class ServerRPC {
    public  static void  main (String [] args) {
        try {
            final int serverPort = 7777;
            CrissCrossPuzzleServerImpl connection = new CrissCrossPuzzleServerImpl();
            CrissCrossPuzzleServerImpl exportObject = (CrissCrossPuzzleServerImpl) UnicastRemoteObject.exportObject(connection, 0);
            try {
                LocateRegistry.getRegistry(serverPort).list();
            } catch (RemoteException e) {
                LocateRegistry.createRegistry(serverPort);
            }

            String URL = "rmi://" + InetAddress.getLocalHost().getHostAddress() + ":7777/CrissCrossPuzzleServer";
            Naming.rebind(URL,exportObject);
            System.out.println("waiting for the client");
        }catch (RemoteException | UnknownHostException | MalformedURLException e)
        {
            e.printStackTrace();
            System.out.println("Error while trying to connect to client object");

        }


    }
}
