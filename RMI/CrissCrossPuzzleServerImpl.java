import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@SuppressWarnings("serial")
public class CrissCrossPuzzleServerImpl extends UnicastRemoteObject implements CrissCrossPuzzleServer {
    private Map<Integer, Game> games = new HashMap<>();
    private WordRepo wordRepo;
    private UserAccounts userAccounts;
/**
 * Name: CrissCrossPuzzleServerImpl
 * Purpose: Constructor for the CrissCrossPuzzleServerImpl class.
 * Initializes the CrissCrossPuzzleServerImpl object and establishes connections to WordRepo and UserAccounts servers.
 * RemoteException if a communication-related exception occurs during remote method invocation
 */
    public CrissCrossPuzzleServerImpl() throws RemoteException {  
        super();
        try {
            String wordUrl = "rmi://" + InetAddress.getLocalHost().getHostAddress() + ":1099/WordRepo";
            wordRepo = (WordRepo) Naming.lookup(wordUrl);
            String userUrl  = "rmi://" + InetAddress.getLocalHost().getHostAddress() + ":1099/UserAccounts";
            userAccounts = (UserAccounts) Naming.lookup(userUrl);
        } catch (UnknownHostException | MalformedURLException | NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int login(int login_id) throws RemoteException {
        return 0;

    }

    @Override
    public String addWord(String word) throws RemoteException {
        if (wordRepo.addWord(word)) {
            return "Word added";
        }
        return "Word not added";
    }

    @Override
    public String removeWord(String word) throws RemoteException {
        if (wordRepo.deleteWord(word)) {
            return "Word deleted";
        }
        return "Word not deleted";
    }

    @Override
    public boolean checkWord(String word) throws RemoteException {
        boolean check = wordRepo.checkWord(word);
        System.out.println("Check word " + word + " is " + check);
        return check;
    }

    @Override
    public String checkScore(String user_id) throws RemoteException {
        return userAccounts.checkUserScore(user_id);
    }

    @Override
    public boolean checkUser(String user_id) throws RemoteException {
        return userAccounts.checkUser(user_id);
    }

    @Override
    public String updateUserScore(String user_id) throws RemoteException {
        return userAccounts.updateUserScore(user_id);
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
            game = new Game(failed_attempts, difficulty, wordRepo);
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

    @Override
    public boolean setUserInactive(String user_id) throws RemoteException {
        return userAccounts.setUserInactive(user_id);
    }
}

