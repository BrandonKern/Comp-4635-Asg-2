import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

// import Server.CrissCrossPuzzleServer; not sure if we need

public class ClientRPC {
    public static void main(String [] args)
    {
        try {
            Scanner scan = new Scanner(System.in);
            String Url = "rmi://" + InetAddress.getLocalHost().getHostAddress() + ":1099/CrissCrossPuzzleServer";
            CrissCrossPuzzleServer connection = (CrissCrossPuzzleServer) Naming.lookup(Url);
            primaryHandler(scan,connection);


        } catch (UnknownHostException | MalformedURLException | NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }


    /*
     * Name: loginPrompt
     * Purpose: Prompts the user to enter their login ID for accessing the crossword puzzle game.
     * Input: A SingleRequestClient object called user represents a single user's clientSocket
     * Output: The user's entered login ID as an integer.
     * There is also a restriction, where if the user's login is already active, then they cannot proceed with the game. 
     */
    public static int loginPrompt( Scanner scan, CrissCrossPuzzleServer connection) throws RemoteException {


        System.out.println(" Hi Welcome to the Best Crossword Puzzle of the Century!!! The Puzzler");
        System.out.print(" Please enter your Login ID: ");
        int login_id = scan.nextInt();
         //System.out.print(connection.login(login_id));

        if (connection.checkUser(String.valueOf(login_id)))
        {
            return login_id;
        }

        return -1;

    }

    /*
     * Name: startGamePrompt
     * Purpose: Asks the user to specify the difficulty level and the number of failed attempts allowed for the game.
     * Input: None.
     * Output: An integer array containing two elements - the first element represents the difficulty level (number of words in the crossword puzzle), and the second element represents the number of failed attempts allowed.
     */
    public static int[] startGamePrompt(Scanner scan ) {
        // the first entry in the array is difficulty
        // the second entry in the array is failed attempts allowed


        int[] playerDecisions = new int[2];;
        System.out.println();
        System.out.print(" Please specify a difficulty by choosing the number of words in the crossword puzzle: ");
        int difficulty = scan.nextInt();
        System.out.println();
        System.out.print(" Please specify the number of failed attempts allowed: ");
        int failed_attempts = scan.nextInt();

        playerDecisions[0] = difficulty;
        playerDecisions[1] = failed_attempts;

        return playerDecisions;
    }


    /*
     * Name: promptBeforeStartingGame
     * Purpose: Displays a menu of options to the user before starting the game and performs corresponding actions based on user input.
     * Input: An integer representing the user's ID. A SingleRequestClient object called user represents a single user's clientSocket
     * Output: None (void). Displays a menu of options and performs actions based on user input.
     * Menu Options:
     *  <A> - Add a Word to Repository
     *  <R> - Remove a Word from the Repository
     *  <C> - Check a Word from the Repository
     *  <S> - Check your Score
     *  <G> - Start game
     *  <Q> - Quit
     */

    public static void promptBeforeStartingGame(int user_id,  Scanner scan, CrissCrossPuzzleServer connection) throws RemoteException {
        String option = "";
        do {
            System.out.println("\nPlease choose one of the following options:");
            System.out.println();
            System.out.println(" <A> - Add a Word to Repository");
            System.out.println();
            System.out.println(" <R> - Remove a Word from the Repository");
            System.out.println();
            System.out.println(" <C> - Check a Word from the Repository");
            System.out.println();
            System.out.println(" <S> - Check your Score");
            System.out.println();
            System.out.println(" <G> - Start game");
            System.out.println();
            System.out.println(" <Q> - Quit");
            System.out.println();
            System.out.print(" Enter your choice here: ");

            option = scan.next().toUpperCase();
            switch (option) {
                case "A":
                    System.out.println();
                    System.out.print(" Please enter the word you would like to add: ");

                    System.out.println(connection.addWord(scan.next()));
                    break;

                case "R":
                    System.out.println();
                    System.out.print(" Please enter the word you would like to remove: ");

                    System.out.println(connection.removeWord(scan.next()));
                    break;

                case "C":
                    System.out.println();
                    System.out.print(" Please enter the word you would like to check: ");

                    System.out.println(connection.checkWord(scan.next()));
                    break;

                case "S":

                    System.out.println(connection.checkScore(String.valueOf(user_id)));

                    break;

                case "G":
                    gameHandler( user_id,scan,connection);
                    break;
                case "Q":
                    break;


            }
        } while (!(option.equals("Q")) && !(option.equals("G")));


    }

    /*
     * Name: primaryHandler
     * Purpose: Orchestrates the sequence of functions to display necessary information to the user.
     * Input: A SingleRequestClient object called user represents a single user's clientSocket.
     * Output: None (void). Calls functions to display login prompt, greet the user, and prompt for game options.
     */
    public static void primaryHandler( Scanner scan, CrissCrossPuzzleServer connection) throws RemoteException {
        int id = loginPrompt( scan,connection);

       if (id != -1) {
         promptBeforeStartingGame(id, scan,connection);
        }

        else { System.out.print("User is already active or error occured in registering the user."); }

    }


    /*
     * Name: gameHandler
     * Purpose: Manages the game after the user selects to start the game.
     * Input: A SingleRequestClient object called user represents a single user's clientSocket. An integer variable that contains the user's id.
     * Output: None (void). Initiates game setup and interaction with the server.
     * Details: This function prompts the user to specify the difficulty level and the number of failed attempts allowed for the game. It then sends a request to the server with these parameters to initialize the game. After setting up the game, it enters a loop where it displays the game menu to the user and handles user inputs until the game is completed or the user quits.
     */
    public static void gameHandler( int user_id, Scanner scan, CrissCrossPuzzleServer connection) throws RemoteException {
        int[] arr = startGamePrompt(scan);

        //Sending start to server and reading/displaying game puzzle
        connection.startGame(user_id,arr[0],arr[1]);
        System.out.println(connection.displayGame(user_id));


        Boolean exit = false;
        do {
            exit = gameMenu( user_id,scan,connection);

        } while (!exit);
        promptBeforeStartingGame(user_id, scan, connection);

    }




    /*
     * Name: gameMenu
     * Purpose: Displays the game menu options, reads user input, and performs corresponding actions based on the choice.
     * Input: A SingleRequestClient object called user represents a single user's clientSocket.
     * Output: A string representing the user's menu choice.
     */

    public static Boolean gameMenu(int user_id, Scanner scan,CrissCrossPuzzleServer connection ) throws RemoteException {

        System.out.println();
        System.out.println();
        System.out.print("  <L> - guess letter");
        System.out.print("  <W> - guess word");
        System.out.print("  <C> - Check word");
        System.out.println("  <Q> - Quit");
        System.out.print(" Enter your choice here: ");
        String option = scan.next().toUpperCase();
        System.out.println();
        Boolean exit = false;

        switch (option) {
            case "L":
                System.out.println();
                System.out.print(" Please enter the letter your are guessing: ");
                //exit = readAndPrintResponseToGAME(user,createMessage("gl", scan.next()), user_id);


                break;
            case "W":
                System.out.println();
                System.out.print(" Please enter the word you would like to check: ");
                // exit =readAndPrintResponseToGAME(user,createMessage("gw", scan.next()), user_id);
                break;
            case "C":
                System.out.println();
                System.out.print(" Please enter the word you would like to check: ");
                //sendRequestToServer(user,createMessage("cw", scan.next()));
                break;
            case "Q":
                System.out.println(connection.setUserInactive(String.valueOf(user_id)));
                exit = true;
                //break;
        }

        return exit;


    }



}
