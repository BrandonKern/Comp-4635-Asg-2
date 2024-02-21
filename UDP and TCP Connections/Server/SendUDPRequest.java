import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class SendUDPRequest {

    private int port;

    private String response;

    public SendUDPRequest(int port) {
        this.port = port;
    }

    public int getPort() {
        return this.port;
    }
    public void setResponse(String message)
    {
        this.response = message;

    }
    public String getResponse()
    {
        return this.response;
    }



    public void sendUDPRequest(String message1) {
        int SERVER_PORT = this.port;

        try (DatagramSocket clientSocket = new DatagramSocket()) {
            // Server details
            InetAddress serverAddress = InetAddress.getLocalHost();

            // Message to be sent

            byte[] sendData = message1.getBytes();

            // Create a DatagramPacket to send the message to the server
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);

            // Send the packet
            clientSocket.send(sendPacket);

            System.out.println("Sent message to server: " + message1);

            // Set a timeout for receiving the response
            clientSocket.setSoTimeout(10000); // 10 seconds timeout

            // Receive the response from the server
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);

            // Process the response
            String serverResponse = new String(receivePacket.getData(), 0, receivePacket.getLength());
            //System.out.println("Received response from server: " + serverResponse);
            setResponse(serverResponse);
        } catch (SocketTimeoutException e) {
            setResponse("Unable to connect to microservice(s)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}

