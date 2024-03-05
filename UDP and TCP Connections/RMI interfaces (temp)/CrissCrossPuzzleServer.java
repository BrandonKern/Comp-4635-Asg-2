import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CrissCrossPuzzleServer extends Remote {
    public String startGame(int userId, int number_of_words, int failed_attemps_factor) throws RemoteException;
    
    public String guessLetter(int userId, char letter) throws RemoteException;
    
    public String guessWord(int userId, String word) throws RemoteException;
    
    public String restartGame(int userId) throws RemoteException;
    
    public String endGame(int userId) throws RemoteException;
    
    public void displayGame(int userId) throws RemoteException;
}  