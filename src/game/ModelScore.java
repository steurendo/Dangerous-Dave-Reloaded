package game;

import java.io.*;
import java.util.ArrayList;

public class ModelScore {
    private static final String FILE = "scores.dat";
    private static final int MAX_SIZE = 5;

    private ArrayList<ScoreEntry> scoreTable;
    private int currentIndex;

    public ModelScore() {
        currentIndex = -1;
        try {
            FileInputStream fis = new FileInputStream(FILE);
            ObjectInputStream ois = new ObjectInputStream(fis);
            scoreTable = (ArrayList<ScoreEntry>) ois.readObject();
        } catch (FileNotFoundException e) {
            scoreTable = new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public String getPlayerName() {
        return scoreTable.get(currentIndex).getPlayerName();
    }

    public boolean isValidCandidate(int newScore) {
        if (scoreTable.size() < MAX_SIZE) return true;
        return scoreTable.stream()
                .anyMatch(score -> newScore > score.getScore());
    }

    public ArrayList<ScoreEntry> getScoreTable() {
        return scoreTable;
    }

    public void type(String newEntry, boolean delete) {
        String playerName = scoreTable.get(currentIndex).getPlayerName();
        if (delete) playerName = playerName.substring(0, playerName.length() - 1);
        else playerName += newEntry;
        scoreTable.get(currentIndex).setPlayerName(playerName);
    }

    public void insertNew(ScoreEntry newScoreEntry) {
        scoreTable.add(newScoreEntry);
        scoreTable.sort((s1, s2) -> Integer.compare(s2.getScore(), s1.getScore()));
        if (scoreTable.size() > MAX_SIZE) scoreTable.remove(scoreTable.size() - 1);
        currentIndex = scoreTable.indexOf(newScoreEntry);
    }

    public void persistScoreTable() {
        try {
            FileOutputStream fos = new FileOutputStream(FILE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(scoreTable);
        } catch (FileNotFoundException e) {
            scoreTable = new ArrayList<>();
        } catch (IOException e) {
            System.out.println("persistScoreTable: failed to write data");
            e.printStackTrace();
        }
    }
}
