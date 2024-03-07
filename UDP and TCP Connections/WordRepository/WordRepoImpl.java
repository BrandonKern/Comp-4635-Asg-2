import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
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
    public boolean createWord(String word) throws RemoteException {
        lock.writeLock().lock();
        try {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("words.txt", true))) {
                bw.write(word);
                bw.newLine();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean removeWord(String word) throws RemoteException {
        lock.writeLock().lock();
        try {
            List<String> lines = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader("words.txt"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.trim().equalsIgnoreCase(word)) {
                        lines.add(line);
                    }
                }
            }
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("words.txt"))) {
                for (String line : lines) {
                    bw.write(line);
                    bw.newLine();
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean checkWord(String word) throws RemoteException {
        lock.readLock().lock();
        try {
            try (BufferedReader br = new BufferedReader(new FileReader("words.txt"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().equalsIgnoreCase(word)) {
                        return true;
                    }
                }
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public String getRandomWord(int length) throws RemoteException {
        lock.readLock().lock();
        try {
            List<String> words = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader("words.txt"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().length() == length) {
                        words.add(line.trim());
                    }
                }
            }
            if (!words.isEmpty()) {
                Random random = new Random();
                return words.get(random.nextInt(words.size()));
            } else {
                return "No words found with specified length";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error occurred while retrieving random word";
        } finally {
            lock.readLock().unlock();
        }
    }
}

