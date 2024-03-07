import java.rmi.Remote;
import java.rmi.RemoteException;

public interface WordRepository extends Remote {
    String checkWord(String msg) throws RemoteException;
    String deleteWord(String msg) throws RemoteException;
    String addWord(String msg) throws RemoteException;
    String requestWord(String constraints) throws RemoteException;
}

