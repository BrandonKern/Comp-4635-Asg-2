import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UserAccounts extends Remote {
    String checkUser(String command) throws RemoteException;
    String checkUserScore(String command) throws RemoteException;
    String updateUserScore(String command) throws RemoteException;
}
