package game;

import entities.Player;
import utils.Callback;
import utils.LevelsLoader;
import utils.PointD;
import utils.Textures;
import utils.Timing;

public class Model {
    private static final double START_DELAY = 0.5;
    private double startDelay;
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
        newHighScore = false;
        waitCommand = false;
        prepareForStart();
    }

    public boolean canStart() {
        return startDelay <= 0;
    }

    public void update() {
        if (startDelay > 0) startDelay -= Timing.scale() / 60;
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
        startDelay = START_DELAY;
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