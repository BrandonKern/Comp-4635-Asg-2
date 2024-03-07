import java.rmi.Remote;
import java.rmi.RemoteException;

public interface WordRepo extends Remote {
    boolean createWord(String word) throws RemoteException;
    boolean removeWord(String word) throws RemoteException;
    boolean checkWord(String word) throws RemoteException;
    String getRandomWord(int length) throws RemoteException;
}
