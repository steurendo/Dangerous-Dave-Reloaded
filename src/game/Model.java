package game;

import entities.Player;
import utils.LevelsLoader;
import utils.PointD;
import utils.Textures;

public class Model {
    private final Level levelsRoot;
    private Level currentLevel;
    private final Player player;
    private int state;

    public Model(Textures textures) {
        state = 0;
        LevelsLoader loader = new LevelsLoader(textures);
        levelsRoot = loader.loadLevelsStructure();
        currentLevel = levelsRoot.getNext().getNext().getNext().getNext().getNext().getNext().getNext().getNext().getNext();
        player = new Player();
        player.setLocation(new PointD(currentLevel.getSpawnpoint().x * 32 + 16, currentLevel.getSpawnpoint().y * 32 + (32 - Player.HEIGHT / 2)));
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

    public void reset() {
        state = 0;
        player.reset();
        currentLevel = levelsRoot;
        currentLevel.init();
        player.setLocation(new PointD(currentLevel.getSpawnpoint().x * 32 + 16, currentLevel.getSpawnpoint().y * 32 + (32 - Player.HEIGHT / 2)));
    }

    public void start() {
        state = 1;
    }

    public void nextLevel() {
        currentLevel = currentLevel.getNext();
        nextGenericLevel();
    }

    public void nextWarpzone() {
        currentLevel = currentLevel.getWarpzone();
        nextGenericLevel();
    }

    public void nextGenericLevel() {
        currentLevel.init();
        player.passLevel();
        player.setLocation(new PointD(currentLevel.getSpawnpoint().x * 32 + 16, currentLevel.getSpawnpoint().y * 32 + (32 - Player.HEIGHT / 2)));
    }
}