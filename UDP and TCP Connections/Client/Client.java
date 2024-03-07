
import java.io.IOException;
import java.rmi.ConnectIOException;
import java.util.Scanner;

public class Client {

    /*
     * Name: main
     * Purpose: Entry point of the program. Calls the primaryHandler method to start the main functionality of the program.
     * Input: Command-line arguments (if any).
     * Output: None.
     */

    public static void main(String[] args) throws IOException {
        try {
            SingleRequestClient user = new SingleRequestClient("localhost", 8080); // user is the clientSocket for this specific client.
            primaryHandler(user);
        } catch (IOException e) {
            System.out.println("Unable to connect to server.");            
        }
        


    }


    /*
     * Name: loginPrompt
     * Purpose: Prompts the user to enter their login ID for accessing the crossword puzzle game.
     * Input: A SingleRequestClient object called user represents a single user's clientSocket
     * Output: The user's entered login ID as an integer.
     */
    public static int loginPrompt(SingleRequestClient user) {

        Scanner scan = new Scanner(System.in);

        System.out.println(" Hi Welcome to the Best Crossword Puzzle of the Century!!! The Puzzler");

         // (1) Ask for the user's first name
        System.out.print("Please enter your first name: ");
        String firstName = scan.nextLine();

          // (2) Ask for the user's last name
        System.out.print("Please enter your last name: ");
        String lastName = scan.nextLine();

        // (3) Ask for the user's age
        int age = 0;
        boolean validAge = false;
        while (!validAge) {
            try {
                System.out.print("Please enter your age: ");
                age = Integer.parseInt(scan.nextLine());
                validAge = true;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer for age.");
            }
        }

        // (4) Create a unique login id based on the provided formula
        int login_id = createLoginId(firstName, lastName, age);

        // (5) Print the login id to the user
        System.out.println("Your unique login id is: " + login_id);
        
       // System.out.print(" Please enter your Login ID: ");
       //  int login_id = scan.nextInt();
        sendRequestToServer(user,createMessage("ru", String.valueOf(login_id)));

        return login_id;

    }

      public static int createLoginId(String firstName, String lastName, int age) {
        // Your formula for creating a unique login id based on first name, last name, and age
        // For example:
        int hashCode = firstName.hashCode() + lastName.hashCode() + age + firstName.length() + lastName.length();
        return Math.abs(hashCode % 1000); // Ensuring the login id is within 0 to 999
    }

    /*
     * Name: startGamePrompt
     * Purpose: Asks the user to specify the difficulty level and the number of failed attempts allowed for the game.
     * Input: None.
     * Output: An integer array containing two elements - the first element represents the difficulty level (number of words in the crossword puzzle), and the second element represents the number of failed attempts allowed.
     */
    public static int[] startGamePrompt() {
        // the first entry in the array is difficulty
        // the second entry in the array is failed attempts allowed


        int[] playerDecisions = new int[2];
        Scanner scan = new Scanner(System.in);
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

    public static void promptBeforeStartingGame(int user_id, SingleRequestClient user) {
        Scanner scan = new Scanner(System.in);
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

                    sendRequestToServer(user,createMessage("aw", scan.next()));
                    break;

                case "R":
                    System.out.println();
                    System.out.print(" Please enter the word you would like to remove: ");

                    sendRequestToServer(user,createMessage("dw", scan.next()));
                    break;

                case "C":
                    System.out.println();
                    System.out.print(" Please enter the word you would like to check: ");

                    sendRequestToServer(user,createMessage("cw", scan.next()));
                    break;

                case "S":

                    sendRequestToServer(user,createMessage("cs",String.valueOf(user_id)) );

                    break;

                case "G":
                    gameHandler(user, user_id);
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
    public static void primaryHandler(SingleRequestClient user) {
        int id = loginPrompt(user);
       promptBeforeStartingGame(id, user);


    }


    /*
     * Name: gameHandler
     * Purpose: Manages the game after the user selects to start the game.
     * Input: A SingleRequestClient object called user represents a single user's clientSocket. An integer variable that contains the user's id.
     * Output: None (void). Initiates game setup and interaction with the server.
     * Details: This function prompts the user to specify the difficulty level and the number of failed attempts allowed for the game. It then sends a request to the server with these parameters to initialize the game. After setting up the game, it enters a loop where it displays the game menu to the user and handles user inputs until the game is completed or the user quits.
     */
    public static void gameHandler(SingleRequestClient user, int user_id) {
        int[] arr = startGamePrompt();

        //Sending start to server and reading/displaying game puzzle
        user.sendRequest("Start"+","+ createMessage(String.valueOf(arr[0]), String.valueOf(arr[1])));
        String returnMessage = user.returnMessage();
        String[] parts = returnMessage.split(",");
        printGame(parts);

        Boolean exit = false;
        do {
            exit = gameMenu(user, user_id);

        } while (!exit);
        promptBeforeStartingGame(user_id,user);

    }

    /*
     * Name: sendRequestToServer
     * Purpose: Sends a TCP message to the server and reads the response.
     * Input: A SingleRequestClient object called user represents a single user's clientSocket. A string message to be sent to the server.
     * Output: None (void).
     */
    public static void sendRequestToServer(SingleRequestClient user, String Message) {
        // Write the message to the server
        user.sendRequest(Message);
       readAndPrintResponse(user); // Added to read and print the response from the server
    }

    /*
     * Name: readAndPrintResponseToGAME
     * Purpose: Sends a TCP message to the server and reads the game response.
     * Input: user - A SingleRequestClient object called user represents a single user's clientSocket
     *        Message - string message to be sent to the server
     *        user_id - integer representing the client to be sent in a potential message 
     */
    public static Boolean readAndPrintResponseToGAME(SingleRequestClient user, String Message, int user_id) {
        user.sendRequest(Message);
        String returnMessage = user.returnMessage();
        String[] parts = returnMessage.split(",");
        int length = parts.length;
        Boolean gameOver = false;
        try {
            for (int i = 0; i < length -1; i++ ) {
                System.out.println(parts[i]);
            }
            if (parts[length-1].equals("W")) {
                gameOver = true;
                System.out.println("You won the game!");
                user.sendRequest(createMessage("su", String.valueOf(user_id)));
            } else if (parts[length-1].equals("L")) {
                gameOver = true;
                System.out.println("Out of attempts, you lost the game.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gameOver;
    }


    /*
     * Name: printGame
     * Purpose: Takes an array of strings and out puts each on its own line.
     * Input: game - array of strings that makes up the game.
     * Return: none
    */
    public static void printGame(String[] game) {
        int length = game.length;
        for (int i = 0; i < length; i++) {
            System.out.println(game[i]);
        }
    }

    /*
     * Name: readAndPrintResponse
     * Purpose: Reads and prints the response received from the server.
     * Input: A SingleRequestClient object called user represents a single user's clientSocket
     * Output: None (void). Prints the response received from the server.
     */
    public static void readAndPrintResponse(SingleRequestClient user) {
        user.readAndPrintResponse();
    }

    /*
     * Name: createMessage
     * Purpose: Concatenates two string values separated by a comma to create a message.
     * Input: Two string values (value1 and value2) to be concatenated.
     * Output: A single string containing the concatenated message.
     */

    public static String createMessage(String value1, String value2) {
        return value1 + "," + value2;
    }




    /*
     * Name: gameMenu
     * Purpose: Displays the game menu options, reads user input, and performs corresponding actions based on the choice.
     * Input: A SingleRequestClient object called user represents a single user's clientSocket.
     * Output: A string representing the user's menu choice.
     */

    public static Boolean gameMenu(SingleRequestClient user, int user_id) {
        Scanner scan = new Scanner(System.in);
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
                exit = readAndPrintResponseToGAME(user,createMessage("gl", scan.next()), user_id);

                break;
            case "W":
                System.out.println();
                System.out.print(" Please enter the word you would like to check: ");
                exit =readAndPrintResponseToGAME(user,createMessage("gw", scan.next()), user_id);
                break;
            case "C":
                System.out.println();
                System.out.print(" Please enter the word you would like to check: ");
                sendRequestToServer(user,createMessage("cw", scan.next()));
                break;
            case "Q":
                exit = true;
            //break;
        }

        return exit;


    }

// Please note that there are 3 parsing methods for the messages that the cient receives from the server. If you know that the
// incoming message has only 2 components (eg. "[new/returning] <userName>") you may choose to use parseIntoTwoParts.
// If you know that the incoming message has only 3 components (eg. “gl <character> [incorrect/correct]) you may choose to use
// parseIntoThreeParts.
//If you are not sure how many compoments there may be, go ahead with parseIntoVariableParts (eg. the puzzle maker as we are not sure how many
// words there may be in the puzzle.

    /*
     * Method 1: parseIntoTwoParts
     * Purpose: Parse the input string into 2 parts separated by " ".
     * Input: A String representing the input to be parsed.
     * Output: An array of Strings containing 2 parts separated by the first occurrence of " ".
     */
    public static String[] parseIntoTwoParts(String input) {
        return input.split(" ", 2);
    }

    /*
     * Method 2: parseIntoThreeParts
     * Purpose: Parse the input string into 3 parts separated by " ".
     * Input: A String representing the input to be parsed.
     * Output: An array of Strings containing 3 parts separated by the first two occurrences of " ".
     */
    public static String[] parseIntoThreeParts(String input) {
        return input.split(" ", 3);
    }

    /*
     * Method 3: parseIntoVariableParts
     * Purpose: Parse the input string into as many parts as needed based on " " (up to 50 parts).
     * Input: A String representing the input to be parsed.
     * Output: An array of Strings containing variable parts separated by " ". Maximum 50 parts.
     */
    public static String[] parseIntoVariableParts(String input) {
        return input.split(" ", 50);
    }

}
