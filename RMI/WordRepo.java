import java.rmi.Remote;
import java.rmi.RemoteException;

public interface WordRepo extends Remote {
    Boolean checkWord(String word) throws RemoteException;
    Boolean deleteWord(String word) throws RemoteException;
    Boolean addWord(String word) throws RemoteException;
    String requestWord(String constraints) throws RemoteException;
}

