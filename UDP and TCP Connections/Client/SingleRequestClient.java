import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

public class SingleRequestClient {
	private static final String USAGE = "java SingleRequestClient [host] [port] [request]";
	private final Socket clientSocket;
	private PrintStream out;
	private BufferedReader in;

	public SingleRequestClient(String host, int port) throws IOException {
		clientSocket = new Socket(host, port);
		out = new PrintStream(clientSocket.getOutputStream());
		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	}

	public void sendRequest(String request) {
		System.out.println("\nSending the request: " + request + " to the server!");
		out.println(request);
		out.flush(); // Flush the output stream to ensure data is sent immediately
	}

	public void readAndPrintResponse() {
		System.out.println("\nWaiting for reply from the server!");
		try {
			String line = in.readLine();
			System.out.println(line);

			
		} catch (IOException e) {
			System.out.println("Error reading response from the server: " + e.getMessage());
		}
	}

	public String returnMessage() {
		String line = "";
		try { 
			line = in.readLine();
		} catch (IOException e) {
			System.out.println("Error reading response from ther server: " + e.getMessage());
		}
		return line;
	}



	public void close() {
		try {
			if (clientSocket != null) {
				clientSocket.close();
			}
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		} catch (IOException e) {
			System.out.println("Error closing resources: " + e.getMessage());
		}
	}




}
