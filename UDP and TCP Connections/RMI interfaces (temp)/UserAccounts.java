import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UserAccounts extends Remote {
    public String UpdateScore(int userId) throws RemoteException;
    
    public String CheckUser(int userId) throws RemoteException;
    
    public String CheckUserScore(int userId) throws RemoteException;
}