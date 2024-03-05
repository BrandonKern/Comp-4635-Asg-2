import java.rmi.Remote;
import java.rmi.RemoteException;

public interface WordRepo extends Remote {
    public String CheckWord(String word) throws RemoteException;
    
    public String DeleteWord(String userId) throws RemoteException;
    
    public String AddWord(String word) throws RemoteException;

    public String RequestWord(int minLength, char startingLetter, char endingLetter) throws RemoteException;
}