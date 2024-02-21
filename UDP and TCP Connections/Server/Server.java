import java.io.*;
import java.net.*;
import java.util.Arrays;

public class Server {
    /*
     * Name: Main
     * Purpose: Creates an instance of ServerSocket and listens for incoming messages. It also creates instances of SendUDPRequest for both the accounts and word repository microservices.
     * It creates threads for each user(or client of Server.java) that joins this server. The Threads run concurrently.
     * Input: String [] args.
     * Output: None (void).
     */
    public static void main(String[] args) {
        final int serverPort = 8080;
        final int ACCOUNT =9879;
        final int WORD_REPO =9876;

       // instances of SendUDPRequest for both microservices.
        SendUDPRequest sendAccount = new SendUDPRequest(ACCOUNT);
        SendUDPRequest sendWordRepo = new SendUDPRequest(WORD_REPO);

        try (ServerSocket serverSocket = new ServerSocket(serverPort)) { // creates server socket
            System.out.println("Server listening on port " + serverPort);

            while (true) { // listens for incoming connections
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket, sendAccount, sendWordRepo); // creates a thread for a specific connection.
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.out.println();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private SendUDPRequest sendAccount;
        private SendUDPRequest sendWordRepo;
        private Puzzle game;

        /*
         * Name: ClientHandler
         * Purpose: To initialize fields so class can make use of the Socket, SendUDPRequest, and Puzzle objects.
         * Input: A Socket object called clientSocket. SendUDPRequest objects called sendAccount and sendWordRepo. ClientSocket is used to read and send messages back to the user(client), and both
         * sendAccount and sendWordRepo are used to send messages to the appropriate microservices.
         * Output: None (void).
         */
        public ClientHandler(Socket clientSocket, SendUDPRequest sendAccount, SendUDPRequest sendWordRepo) {
            this.clientSocket = clientSocket;
            this.sendAccount = sendAccount;
            this.sendWordRepo = sendWordRepo;
            this.game = new Puzzle(2,3); // this is just a dummy game, it is created so the run method does not create multiple instances of the game causing problems.
        }

        /*
         * Name: run
         * Purpose: Reads all incoming messages that the serverSocket receives and parses them to see what the messages are asking.
         * Some are relayed to the appropriate microservices or are handled within Server process. Puzzle related messages are handled within Server process while microservice related messages
         * are sent to the appropriate microservice
         * Input: None.
         * Output: None (void).
         */
        @Override
        public void run() {
            try (
                    BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // used to read the message that the server received from a client.

            ) {
                boolean master = true;

                while (master) {
                    String clientData = input.readLine();

                    if (clientData == null) {
                        break; // Exit loop if no more data from client
                    }

                    String[] currentInput = parseIntoVariableParts(clientData); // parses the String message that the server received from the user(or one of Server.java's clients).
                    System.out.println(Arrays.toString(currentInput));
                        switch (currentInput[0]) {
                            case "ru": // This case deals with sending the user ID to the userAccounts microservice and then a message from the microservice is sent back to the user. It checks if the user exists.
                                SendBackToClient(handleAccount(replaceCommaWithSpace("cu" + "," + currentInput[1]), sendAccount), clientSocket);
                                break;
                            case "Start": // This case deals with starting the game. It resets the dummy game to the user's preferences. This happens inside the Server process.
                                game.setGame(Integer.parseInt(currentInput[2]),Integer.parseInt(currentInput[1]));
                                SendBackToClient(game.toString(), clientSocket);
                                game.displayPuzzle();
                                break;
                            case "cs": // This case deals with sending the user ID to the userAccounts microservice and then a message from the microservice is sent back to the user. It sends back the user's high score.
                                SendBackToClient(handleAccount(replaceCommaWithSpace("us" + "," + currentInput[1]), sendAccount), clientSocket);

                                break;
                            case "cw": // This case deals with sending a word to the word repository microservice and then a message from the microservice is sent back to the user. It checks if the word is in the word repository.
                                SendBackToClient(handleWordRepo(replaceCommaWithSpace(clientData), sendWordRepo), clientSocket);
                                SendBackToClient(handleWordRepo(replaceCommaWithSpace(clientData), sendWordRepo), clientSocket);
                                break;
                            case "aw": // This case deals with sending a word to the word repository microservice and then a message from the microservice is sent back to the user. It adds a word to the word repository.
                                SendBackToClient(handleWordRepo(replaceCommaWithSpace(clientData), sendWordRepo), clientSocket);
                                break;
                            case "dw": // This case deals with sending a word to the word repository microservice and then a message from the microservice is sent back to the user. It deletes a word from the word repository.
                                SendBackToClient(handleWordRepo(replaceCommaWithSpace(clientData), sendWordRepo), clientSocket);
                                break;
                            case "su": // This case deals with updating the clients high score, it sends the message to the userAccounts microservice.
                                handleAccount(replaceCommaWithSpace(clientData),sendAccount);
                            case "gl": // This case deals with a guess about a letter in the puzzle made by the user. This is handled within the Server process.
                                Boolean gl = game.guessLetter(currentInput[1].charAt(0)); // this checks if the guess was correct
                                if (gl) {
                                boolean won = game.checkWin(); // This checks if that latest guess wins the user the game.
                                if(won) // The user wins the game and a W is added to end of the puzzle string to signify that.
                                {
                                    SendBackToClient(game.toString()+",W" ,clientSocket);
                                }
                                else{ // The user has not won yet, game continues and a C is added to the end of the puzzle string to signify that.
                                    SendBackToClient(game.toString() +",C",clientSocket);
                                }
                                } else {
                                    boolean loss = game.checkLoss(); // This checks if the latest guess makes the user lose the game.
                                    if(loss) // The user loses the game and an L is added to end of the puzzle string to signify that.
                                    {
                                        SendBackToClient(game.toString() + ","+ "L", clientSocket);
                                    }
                                    else{ // The user does not lose the game yet, game continues and a C is added to the end of the puzzle string to signify that.
                                        SendBackToClient(game.toString()+",C",clientSocket);
                                    }
                                }
                                break;
                            case "gw": // This case deals with a guess about a word in the puzzle made by the user. This is handled within the Server process.
                                Boolean gw = game.guessWord(currentInput[1]); // this checks if the guess was correct
                                if (gw) {
                                    boolean won = game.checkWin(); // This checks if the latest guess wins the user the game.
                                    if(won)
                                    { // The user wins the game and a W is added to end of the puzzle string to signify that.

                                        SendBackToClient(game.toString()+",W" ,clientSocket);
                                    }
                                    else{  // The user has not won yet, game continues and a C is added to the end of the puzzle string to signify that.
                                        SendBackToClient(game.toString() +",C",clientSocket);

                                    }
                                } else {
                                    boolean loss = game.checkLoss(); // This checks if the latest guess makes the user lose the game.
                                    if(loss)
                                    { // The user loses the game and an L is added to end of the puzzle string to signify that.


                                    SendBackToClient(game.toString() + ","+ "L", clientSocket);
                                    }
                                    else{ // The user does not lose the game yet, game continues and a C is added to the end of the puzzle string to signify that.
                                    SendBackToClient(game.toString()+",C",clientSocket);

                                    }
                                }
                                break;
                            case "Q":
                                master = false;
                                break;
                        }
                }
            } catch (SocketException e) {
                System.out.println("Client Connection Severed");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    
                    e.printStackTrace();

                }
                System.out.println("Client disconnected: " + clientSocket); // specifies which client of the many connected has disconnected.
            }
        }


        /*
        Name: handleWordRepo
        Purpose: This method sends a message to the word repository microservice and returns the response as a string.
        Input: String message. The actual message content
        Output: String message. The response that the word repository gives.
         */
        public static String handleWordRepo(String message, SendUDPRequest send)
        {

            send.sendUDPRequest(message);
            return send.getResponse();


        }
        /*
          Name: handleAccount
          Purpose: This method sends a message to the Accounts microservice and returns the response as a string.
          Input: String message. The actual message content
          Output: String message. The response that the Accounts gives.
           */
        public static String  handleAccount(String message, SendUDPRequest send)
        {

            send.sendUDPRequest(message);
            return send.getResponse();

        }
        /*
        Name: SendBackToClient
        purpose: relays message back to the client from microservices.
        Input: String response, Socket ClientSocket.
        Output: None.
         */
        public static void SendBackToClient(String response, Socket clientSocket) throws IOException {
            OutputStream output = clientSocket.getOutputStream();
            response += "\n"; // Append a newline character to the response
            output.write(response.getBytes());
            output.flush(); // Flush the output stream to ensure data is sent immediately
           System.out.println(response + "this is replying");
        }

        /*
        Name: replaceCommaWithSpace
        Purpose: converts the client to server communication protocol to server to microservice protocol.
        Input: String input. The client to server communication protocol message.
        Output: String. The server to microservice communication protocol message.
         */
        public static String replaceCommaWithSpace(String input) {
            return input.replace(",", " ");
        }

        /*
         * Method 3: parseIntoVariableParts
         * Purpose: Parse the input string into as many parts as needed based on " " (up to 50 parts).
         * Input: A String representing the input to be parsed.
         * Output: An array of Strings containing variable parts separated by " ". Maximum 50 parts.
         */
        public static String[] parseIntoVariableParts(String input) {
            return input.split(",", 50);
        }





    }
}
