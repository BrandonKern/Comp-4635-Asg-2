import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class CrissCrossPuzzleServerImpl {
    private Map<Integer, Game> games = new HashMap<>();

    public CrissCrossPuzzleServerImpl() {
        super();
    }

    public String startGame(int userId, int number_of_words, int failed_attemps_factor) {
        Game game = games.get(userId);
        if (game != null) {
            System.out.println("Game already exists for this user!");
            return "oof";
        } else {
            game = new Game(failed_attemps_factor, number_of_words);
            games.put(userId, game);
            if (games.get(userId) != null) {
                System.out.println("Successfully created");
            }
            System.out.println("New Game made for user " + userId);
        }
        return game.toString();
    }

    //note unsure when to implement win and loss checking or communicate it
    public String guessLetter(int userId, char letter) {
        Game game = games.get(userId);
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

    //note unsure when to implement win and loss checking or communicate it
    public String guessWord(int userId, String word) {
        Game game = games.get(userId);
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

    public String restartGame(int userId) { //not sure if needed
        return "restart";
    }

    public String endGame(int userId) {
        Game game = games.get(userId);
        if (game != null) {
            return "sucess";
        } else {
            if (games.remove(userId) != null) {
                return "sucess";
            }
        }
        return "clear";
    }

    public void displayGame(int userId) {
        Game game = games.get(userId);
        if (game != null) {
            game.displayPuzzle();
        } else {
            System.out.println("Game does not exist for this user");
        }
    }
}
