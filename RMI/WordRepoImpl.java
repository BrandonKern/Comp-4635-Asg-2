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

public class WordRepoImpl extends UnicastRemoteObject implements WordRepo {
    private static final long serialVersionUID = 1L;
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();

    protected WordRepoImpl() throws RemoteException {
        super();
    }

    
    @Override
    public Boolean checkWord(String word) throws RemoteException {
        lock.readLock().lock();
        try {
            try (BufferedReader br = new BufferedReader(new FileReader("words.txt"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().equalsIgnoreCase(word)) {
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            return false;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Boolean deleteWord(String word) throws RemoteException {
        lock.writeLock().lock();
        try {
            Boolean wordFound = false;

            try (BufferedReader br = new BufferedReader(new FileReader("words.txt"))) {
                StringBuilder fileContent = new StringBuilder();
                String line;

                // Iterate through each line in the "words.txt" file
                while ((line = br.readLine()) != null) {
                    // Check if the trimmed line matches the word to be deleted (case-insensitive)
                    if (line.trim().equalsIgnoreCase(word)) {
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
                return false;
            }

            return wordFound;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Boolean addWord(String word) throws RemoteException {
        lock.writeLock().lock();
        try {

            try (BufferedReader br = new BufferedReader(new FileReader("words.txt"))) {
                String line;

                // Iterate through each line in the "words.txt" file
                while ((line = br.readLine()) != null) {
                    // Check if the trimmed line matches the word to be added (case-insensitive)
                    if (line.trim().equalsIgnoreCase(word)) {
                        return false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            // The word does not exist, so add it to the file
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("words.txt", true))) {
                bw.write(word);
                bw.newLine(); // Add a newline character after the word
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public String requestWord(String constraints) throws RemoteException {
        lock.readLock().lock();
        try {
            System.out.println(constraints);
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
                    return selectedWord;
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


