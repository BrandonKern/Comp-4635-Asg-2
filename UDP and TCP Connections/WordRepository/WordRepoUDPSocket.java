import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.*;
import java.util.Random;

public class WordRepoUDPSocket {

    public static void main(String[] args) {
        final int PORT = 9876;

        try (DatagramSocket serverSocket = new DatagramSocket(PORT)) {
            System.out.println("UDP Server is running on port " + PORT);

            while (true) {
                byte[] receiveData = new byte[1024];

                // Receive packet from client
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                // Create a new thread to handle the client
                Thread clientHandlerThread = new Thread(() -> handleClient(serverSocket, receivePacket));
                clientHandlerThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   // private static void handleClient(DatagramSocket serverSocket, DatagramPacket receivePacket) {
     //   try {
            // Process client request (You can replace this with your own logic)
         //   String clientData = new String(receivePacket.getData(), 0, receivePacket.getLength());
         //   System.out.println("Received from client at " + receivePacket.getAddress().getHostAddress() + ": " + clientData);

            // Prepare response (You can replace this with your own logic)
        //    String responseData = "Hello, client! You sent: " + clientData;
        //    byte[] sendData = responseData.getBytes();

            // Send response back to the client
       //     DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
       //     serverSocket.send(sendPacket);

      //      System.out.println("Sent response to client at " + receivePacket.getAddress().getHostAddress() + ":" + receivePacket.getPort());
    //    } catch (Exception e) {
    //        e.printStackTrace();
   //     }
//    }

private static void handleClient(DatagramSocket serverSocket, DatagramPacket receivePacket) {
    try {
        // Process client request
        String clientData = new String(receivePacket.getData(), 0, receivePacket.getLength());
        System.out.println("Received from client at " + receivePacket.getAddress().getHostAddress() + ": " + clientData);

        // Parse the data message
        String[] parts = clientData.split("[,\\s]+");

        // Check if the parsed message is in the expected format
        if (parts.length < 1) {
            System.err.println("Invalid request format");
            return;
        }

        // Determine the command
        String command = parts[0];

        // Prepare response based on the command
        String response = HandleRequest(command, clientData);

        // Send response back to the client
        byte[] sendData = response.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
        serverSocket.send(sendPacket);

        System.out.println("Sent response to client at " + receivePacket.getAddress().getHostAddress() + ":" + receivePacket.getPort());
    } catch (Exception e) {
        e.printStackTrace();
    }
}

/*
 * Parses the msg and directs to appropriate function returns new message to send reply
 */
private static String HandleRequest(String command, String msg) {
    String response = "";

    switch (command) {
        case "cw":
            response = CheckWord(msg);
            break;
        case "dw":
            response = DeleteWord(msg);
            break;
        case "aw":
            response = AddWord(msg);
            break;
        case "rw":
            response = RequestWord(msg);
            break;
        default:
            response = "Invalid request";
    }

    return response;
}

/**
* Name: CheckWord
* Purpose: Checks if a specified word exists in the "words.txt" file.
* Input: The input message in the format "cw <word>".
* Output: A message indicating whether the specified word exists or not.
*/
       private static String CheckWord(String msg) {
            String[] parts = msg.split(" ");

            if (parts.length != 2 || !parts[0].equals("cw")) {
                return "Invalid request";
            }

            String wordToCheck = parts[1];

            try (BufferedReader br = new BufferedReader(new FileReader("words.txt"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().equalsIgnoreCase(wordToCheck)) {
                        return wordToCheck + " exists";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Error occurred while checking the word";
            }

            return wordToCheck + " does not exist";
        }

/**
 * Name: DeleteWord
 * Purpose: Deletes a specified word from the "words.txt" file if it exists.
 * Input: The input message in the format "dw <word>".
 * Output: A message indicating whether the specified word has been deleted or not.
 */
       private static String DeleteWord(String msg) {
            String[] parts = msg.split(" ");

            if (parts.length != 2 || !parts[0].equals("dw")) {
                return "Invalid request";
            }

            String wordToDelete = parts[1];
            Boolean wordFound = false;

            try (BufferedReader br = new BufferedReader(new FileReader("words.txt"))) {
                StringBuilder fileContent = new StringBuilder();
                String line;

                // Iterate through each line in the "words.txt" file
                while ((line = br.readLine()) != null) {
                    // Check if the trimmed line matches the word to be deleted (case-insensitive)
                    if (line.trim().equalsIgnoreCase(wordToDelete)) {
                        fileContent.append("*"); // Replace the word with "*"
                        wordFound = true;
                    } else {
                        fileContent.append(line); // Keep the existing line
                    }
                    fileContent.append(System.lineSeparator()); // Add newline character
                }

                // Update the file with the modified content
                try (BufferedWriter bw = new BufferedWriter(new FileWriter("words.txt"))) {
                    bw.write(fileContent.toString());
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "Error occurred while deleting the word";
            }

            if (wordFound) {
                return wordToDelete + " deleted";
            }
            else {
                return wordToDelete + " not deleted";
            }
        }
/**
         * Name: AddWord
         * Purpose: Adds a specified word to the "words.txt" file if it does not already exist.
         * Input: The input message in the format "aw <word>".
         * Output: A message indicating whether the specified word has been added or not.
         */
        private static String AddWord(String msg) {
            String[] parts = msg.split(" ");

            if (parts.length != 2 || !parts[0].equals("aw")) {
                return "Invalid request";
            }

            String wordToAdd = parts[1];

            try (BufferedReader br = new BufferedReader(new FileReader("words.txt"))) {
                String line;

                // Iterate through each line in the "words.txt" file
                 while ((line = br.readLine()) != null) {
                    // Check if the trimmed line matches the word to be added (case-insensitive)
                    if (line.trim().equalsIgnoreCase(wordToAdd)) {
                        return wordToAdd + " already exists, not added";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Error occurred while checking the word";
            }

            // The word does not exist, so add it to the file
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("words.txt", true))) {
                bw.write(wordToAdd);
                bw.newLine(); // Add a newline character after the word
            } catch (Exception e) {
                e.printStackTrace();
                return "Error occurred while adding the word";
            }

            return wordToAdd + " added";
        }
   /**
         * Name: RequestWord
         * Purpose: Requests a word from the "words.txt" file based on given constraints.
         * Input: The input message in the format "rw,sl,<starting letter>,el,<ending letter>,wl,<min word length>".
         * Output: A message indicating the word found or a message indicating that no word meets the specified constraints.
         */
        private static String RequestWord(String constraints) {
        String[] parts = constraints.split(",");

        // Check if the input message is in the expected format
        if (parts.length != 7 || !parts[0].equals("rw")) {
            return "Invalid request";
        }

        String category = parts[1];
        String startLetter = parts[2];
        String endLetter = parts[4];
        String minWordLength = parts[6];

        List<String> wordsList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("words.txt"))) {
            String line;

            // Iterate through each line in the "words.txt" file
            while ((line = br.readLine()) != null) {
                // Check if the word meets the specified constraint
                if ((parts[1].equals("sl") && line.startsWith(startLetter)) ||
                        (parts[3].equals("el") && line.endsWith(endLetter)) ||
                        (parts[5].equals("wl") && !parts[6].equals("0") && line.length() >= Integer.parseInt(minWordLength))) {
                    wordsList.add(line);
                }
            }

            if (!wordsList.isEmpty()) {
                // Randomly select a word from the list
                Random random = new Random();
                String selectedWord = wordsList.get(random.nextInt(wordsList.size()));
                return "rw " + selectedWord;
            } else {
                return "No word found that meets the specified constraints";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred while searching for the word";
        }
    }

}
