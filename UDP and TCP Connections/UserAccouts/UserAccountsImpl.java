import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UserAccountServerImpl extends UnicastRemoteObject implements UserAccountServer {
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();

    protected UserAccountServerImpl() throws RemoteException {
        super();
    }

    @Override
    public String checkUser(String command) throws RemoteException {
        String[] parts = command.split(" ");

        if (parts.length != 2 || !parts[0].equals("cu")) {
            return "Invalid Request";
        }

        String userId = parts[1];

        lock.readLock().lock();
        try {
            try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] lineParts = line.trim().split(" ");

                    if (lineParts[0].equalsIgnoreCase(userId)) {
                        return userId + " exists";
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
                return "Error occurred while adding the user";
            }

            return userId + " does not exist, added user";
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public String checkUserScore(String command) throws RemoteException {
        String[] parts = command.split(" ");

        if (parts.length != 2 || !parts[0].equals("us")) {
            return "Invalid Request";
        }

        String userId = parts[1];

        lock.readLock().lock();
        try {
            try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] lineParts = line.trim().split(" ");

                    if (lineParts[0].equalsIgnoreCase(userId)) {
                        return userId + " score: " + lineParts[1];
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Error occurred while checking the userId";
            }

            return userId + " score not found";
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public String updateUserScore(String command) throws RemoteException {
        String[] parts = command.split(" ");

        if (parts.length != 2 || !parts[0].equals("su")) {
            return "Invalid Request";
        }

        String userId = parts[1];

        lock.writeLock().lock();
        try {
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
                        fileContent.append(userId).append(" ").append(userScore);
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
        } finally {
            lock.writeLock().unlock();
        }
    }
}