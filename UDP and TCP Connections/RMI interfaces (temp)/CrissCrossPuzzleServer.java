import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CrissCrossPuzzleServer extends Remote {
    int login(int login_id) throws RemoteException;
    void addWord(String word) throws RemoteException;
    void removeWord(String word) throws RemoteException;
    boolean checkWord(String word) throws RemoteException;
    int checkScore(int user_id) throws RemoteException;
    String startGame(int difficulty, int failed_attempts) throws RemoteException;
    String guessLetter(int user_id, char letter) throws RemoteException;
    String guessWord(int user_id, String word) throws RemoteException;
}
