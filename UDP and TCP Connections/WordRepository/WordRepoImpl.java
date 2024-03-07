import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class WordRepositoryImpl extends UnicastRemoteObject implements WordRepository {
    private static final long serialVersionUID = 1L;
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();

    protected WordRepositoryImpl() throws RemoteException {
        super();
    }

    @Override
    public String checkWord(String msg) throws RemoteException {
        lock.readLock().lock();
        try {
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
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public String deleteWord(String msg) throws RemoteException {
        lock.writeLock().lock();
        try {
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
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public String addWord(String msg) throws RemoteException {
        lock.writeLock().lock();
        try {
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
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public String requestWord(String constraints) throws RemoteException {
        lock.readLock().lock();
        try {
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
        } finally {
            lock.readLock().unlock();
        }
    }
}


