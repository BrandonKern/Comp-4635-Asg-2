import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UserAccounts extends Remote {
    String checkUser(String user_id) throws RemoteException;
    String checkUserScore(String user_id) throws RemoteException;
    String updateUserScore(String user_id) throws RemoteException;
}
