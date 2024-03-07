import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class CrissCrossPuzzleServerImpl implements CrissCrossPuzzleServer {
    private Map<Integer, Game> games = new HashMap<>();

    public CrissCrossPuzzleServerImpl() throws RemoteException {
        super();
    }

    @Override
    public int login(int login_id) throws RemoteException {
        return 0;

    }

    @Override
    public String addWord(String word) throws RemoteException {
        return "";
    }

    @Override
    public String removeWord(String word) throws RemoteException {
        return "";
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
    public String endGame(int user_id) throws RemoteException {
        Game game = games.get(user_id);
        if (game != null) {
            return "sucess";
        } else {
            if (games.remove(user_id) != null) {
                return "sucess";
            }
        }
        return "clear";
    }

    @Override
    public String startGame(int user_id, int difficulty, int failed_attempts) throws RemoteException {
        Game game = games.get(user_id);
        if (game != null) {
            System.out.println("Game already exists for this user!");
            return "oof";
        } else {
            game = new Game(failed_attempts, difficulty);
            games.put(user_id, game);
            if (games.get(user_id) != null) {
                System.out.println("Successfully created");
            }
            System.out.println("New Game made for user " + user_id);
        }
        return game.toString();
    }

    @Override
    public String guessLetter(int user_id, char letter) throws RemoteException {
        Game game = games.get(user_id);
        if (game != null) {
            boolean gl = game.guessLetter(letter);
            if (gl) {
                return "correct guess";
            } else {
                return "incorrect guess";
            }
        } else {
            System.out.println("Game does not exist for this user");
        }
        return "letter";
    }

    @Override
    public String guessWord(int user_id, String word) throws RemoteException {
        Game game = games.get(user_id);
        if (game != null) {
            boolean gw = game.guessWord(word);
            if (gw) {
                return "correct guess";
            } else {
                return "incorrect guess";
            }
        } else {
            System.out.println("Game does not exist for this user");
        }
        return "word";
    }

    @Override
    public String displayGame(int userId) throws RemoteException {
        Game game = games.get(userId);
        if (game != null) {
            return game.toString();
        } else {
            System.out.println("Game does not exist for this user");
        }
        return "Game does not exist";
    }
}
