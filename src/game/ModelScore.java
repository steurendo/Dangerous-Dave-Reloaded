package game;

import java.io.*;
import java.util.ArrayList;

public class ModelScore {
    private static final String FILE = "scores.dat";
    private static final int MAX_SIZE = 5;

    private ArrayList<ScoreEntry> scoreTable;

    private String playerName;
    private int level;
    private int score;

    public ModelScore() {
        resetNewScore();
        try {
            FileInputStream fis = new FileInputStream(FILE);
            ObjectInputStream ois = new ObjectInputStream(fis);
            scoreTable = (ArrayList<ScoreEntry>) ois.readObject();
            System.out.println();
        } catch (FileNotFoundException e) {
            scoreTable = new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isValidCandidate(int newScore) {
        if (scoreTable.size() < MAX_SIZE) return true;
        return scoreTable.stream()
                .anyMatch(score -> newScore > score.getScore());
    }

    private void resetNewScore() {
        playerName = "";
        level = 0;
        score = 0;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void type(String newEntry, boolean delete) {
        if (delete) playerName = playerName.substring(0, playerName.length() - 1);
        else playerName += newEntry;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void insertNew() {
        StringBuilder sb = new StringBuilder();
        while (playerName.length() < 3) sb.append(" ");
        sb.append(playerName);
        playerName = sb.toString();
        ScoreEntry newScoreEntry = new ScoreEntry(playerName, level, score);
        scoreTable.add(newScoreEntry);
        scoreTable.sort((s1, s2) -> Integer.compare(s2.getScore(), s1.getScore()));
        if (scoreTable.size() > MAX_SIZE) scoreTable.remove(scoreTable.size() - 1);
        persistScoreTable();
        resetNewScore();
    }

    private void persistScoreTable() {
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
