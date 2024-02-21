import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UserAccountsUDPSocket {

    public static void main(String[] args) {
        final int PORT = 9879;

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
            case "cu":
                response = CheckUser(msg);
                break;
            case "us":
                response = CheckUserScore(msg);
                break;
            case "su":
                response = UpdateScore(msg);
                break;
            default:
                response = "Invalid request";
        }

        return response;
    }
    

    /*
     * 
     */
    private static String UpdateScore(String command) {
        String[] parts = command.split(" ");

        if (parts.length != 2 || !parts[0].equals("su")) {
            return "Invalid Request";
        }

        String userId = parts[1];

        try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
            StringBuilder fileContent = new StringBuilder();
            String line;

            boolean changed = false;
            // Iterate through each line in the "users.txt" file
            while ((line = br.readLine()) != null) {
                String[] lineParts = line.trim().split(" ");
                
                if (lineParts[0].equalsIgnoreCase(userId)) {
                    changed = true;
                    int userScore = Integer.parseInt(lineParts[1]) + 1;
                    fileContent.append(userId + " " + Integer.toString(userScore));
                } else {
                    fileContent.append(line); // Keep the existing line
                }
                fileContent.append(System.lineSeparator()); // Add newline character
            }



            // Update the file with the modified content
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("users.txt"))) {
                bw.write(fileContent.toString());
            }
            if (changed) {
                return userId + " score updated";
            } else {
                return userId + " score not updated";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred while updating score";
        }
    }

    /*
     * Name: CheckUser
     * Purpose: Check if the User Id is already exists, if it does not add it
     * Input: The input message in the format "cu <userId>"
     * Return: A message indicating whether the specified user exists or not.
     */
    private static String CheckUser(String command) {
        
        String[] parts = command.split(" ");

        if (parts.length != 2 || !parts[0].equals("cu")) {
            return "Invalid Request";
        }

        String userId = parts[1];

        try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] lineParts = line.trim().split(" ");
                
                if (lineParts[0].equalsIgnoreCase(userId)) {
                    return userId + " did exist";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred while checking the userId";
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("users.txt", true))) {
            bw.write(userId + " 0");
            bw.newLine(); // Add a newline character after the word
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred while adding the word";
        }

        return userId + " does not exist adding user";
    }


    /*
     * Name: CheckUserScore
     * Purpose: Check and return the score of a user with a given userId
     * Input: The input message in the format "us <userId>"
     * Return: A message with the format "<userId> score <userScore>" or an error message
     */
    private static String CheckUserScore(String command) {

        String[] parts = command.split(" ");

        if (parts.length != 2 || !parts[0].equals("us")) {
            return "Invalid Request";
        }
        
        String userId = parts[1];

        try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] lineParts = line.trim().split(" ");
                
                if (lineParts[0].equalsIgnoreCase(userId)) {
                    return userId + " score " + lineParts[1];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred while checking the userId";
        }



        return "Error occured while checking score";
    }

}
