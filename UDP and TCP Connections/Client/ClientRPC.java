import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class Client2 {
    public static void main(String [] args)
    {
        try {
            Scanner scan = new Scanner(System.in);
            String Url = "rmi://" + InetAddress.getLocalHost().getHostAddress() + ":7777/CrissCrossPuzzleServer";
            CrissCrossPuzzleServer connection = (CrissCrossPuzzleServer) Naming.lookup(Url);


            System.out.println("current amount in Bank account" + connection.login(scan.nextInt()));
            while(true);

        } catch (UnknownHostException | MalformedURLException | NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
