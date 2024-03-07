import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

@SuppressWarnings("serial")
public class CrissCrossPuzzleServerImpl implements CrissCrossPuzzleServer {


    public CrissCrossPuzzleServerImpl() throws RemoteException {
        super();
    }

    @Override
    public int login(int login_id) throws RemoteException {
        return login_id;

    }

    @Override
    public void addWord(String word) throws RemoteException {

    }

    @Override
    public void removeWord(String word) throws RemoteException {

    }

    @Override
    public boolean checkWord(String word) throws RemoteException {
        return false;
    }

    @Override
    public int checkScore(int user_id) throws RemoteException {
        return 0;
    }

    @Override
    public String startGame(int difficulty, int failed_attempts) throws RemoteException {
        return null;
    }

    @Override
    public String guessLetter(int user_id, char letter) throws RemoteException {
        return null;
    }

    @Override
    public String guessWord(int user_id, String word) throws RemoteException {
        return null;
    }
}
