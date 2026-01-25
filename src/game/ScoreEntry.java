package game;

import java.io.Serializable;

public class ScoreEntry implements Serializable {
    private String playerName;
    private final int score;
    private final int level;

    public ScoreEntry(String playerName, int score, int level) {
        this.playerName = playerName;
        this.score = score;
        this.level = level;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public int getLevel() {
        return level;
    }
}
