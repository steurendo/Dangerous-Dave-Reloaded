package game;

import entities.Player;
import utils.Callback;
import utils.LevelsLoader;
import utils.PointD;
import utils.Textures;

import java.util.Timer;
import java.util.TimerTask;

public class Model {
    private boolean canStart;
    private final Level levelsRoot;
    private Level currentLevel;
    private final Player player;
    private int state;
    private final int levelsCount;
    private boolean gameFinished;
    private boolean newHighScore;
    private boolean waitCommand;

    public Model(Textures textures) {
        state = 0;
        LevelsLoader loader = new LevelsLoader(textures);
        levelsRoot = loader.loadLevelsStructure();
        levelsCount = loader.getLevelsCount();
        currentLevel = levelsRoot;
        player = new Player();
        player.setLocation(new PointD(currentLevel.getSpawnpoint().x * 32 + 16, currentLevel.getSpawnpoint().y * 32 + (32 - Player.HEIGHT / 2)));
        gameFinished = false;
        canStart = false;
        newHighScore = false;
        waitCommand = false;
        prepareForStart();
    }

    public boolean canStart() {
        return canStart;
    }

    public void setGameFinished(boolean gameFinished) {
        this.gameFinished = gameFinished;
    }

    public boolean isGameFinished() {
        return currentLevel.getLevelType() == LevelType.ENDGAME || gameFinished;
    }

    public int getState() {
        return state;
    }

    public Player getPlayer() {
        return player;
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public int getRemainingLevels() {
        return levelsCount - currentLevel.getNumber();
    }

    private void prepareForStart() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                canStart = true;
            }
        }, 500);
    }

    public void reset() {
        state = 0;
        player.reset();
        currentLevel = levelsRoot;
        currentLevel.init(true);
        // Player nello spawnpoint
        PointD spawnpoint = new PointD(
                currentLevel.getSpawnpoint().x * 32 + 16,
                currentLevel.getSpawnpoint().y * 32 + (32 - Player.HEIGHT / 2));
        player.setLocation(spawnpoint);
        gameFinished = false;
        canStart = false;
        newHighScore = false;
        waitCommand = false;
        prepareForStart();
    }

    public boolean isWaitingForCommand() {return waitCommand;}
    public void setWaitCommand() {
        waitCommand = true;
    }

    public void start() {
        state = 1;
    }

    public boolean isNewHighScore() {
        return newHighScore;
    }

    public void setNewHighScore(boolean newHighScore) {
        this.newHighScore = newHighScore;
    }

    public void nextLevel(Callback afterTransitionCallback) {
        currentLevel = currentLevel.getNext();
        nextGenericLevel();
        afterTransitionCallback.execute();
    }

    public void nextLevel() {
        nextLevel(() -> {
        });
    }

    public void nextWarpzone() {
        currentLevel.toggleCompleteWarpzone();
        currentLevel = currentLevel.getWarpzone();
        nextGenericLevel();
    }

    public void nextGenericLevel() {
        currentLevel.init(false);
        player.passLevel();
        player.setLocation(new PointD(currentLevel.getSpawnpoint().x * 32 + 16, currentLevel.getSpawnpoint().y * 32 + (32 - Player.HEIGHT / 2)));
    }
}