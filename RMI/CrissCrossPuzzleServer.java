import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CrissCrossPuzzleServer extends Remote {
    int login(int login_id) throws RemoteException;
    String addWord(String word) throws RemoteException;
    String removeWord(String word) throws RemoteException;
    boolean checkWord(String word) throws RemoteException;
    String checkScore(int user_id) throws RemoteException;
    boolean checkUser(int user_id) throws RemoteException;
    String updateUserScore(int user_id) throws RemoteException;
    boolean endGame(int user_id) throws RemoteException;
    String startGame(int user_id, int difficulty, int failed_attempts) throws RemoteException;
    boolean guessLetter(int user_id, char letter) throws RemoteException;
    boolean guessWord(int user_id, String word) throws RemoteException;
    boolean checkWin(int user_id) throws RemoteException;
    boolean checkLoss(int user_id) throws RemoteException;
    String displayGame(int userId) throws RemoteException;
    boolean setUserInactive(String user_id) throws RemoteException;
}
