package game;

import entities.Entity;
import entities.EntityChain;
import entities.MovingEntity;
import utils.PointD;

import java.awt.*;
import java.util.*;

public class Level {
    public final static int TILES_ALONG_Y = 13;  // 1 SOTTO, DUE SOPRA IN PIù
    public final static int OFFSET_Y = 2;

    private final boolean[][] map;
    private final boolean[][] climbables;
    private final int width;
    private final Point spawnpoint;
    private final EntityChain[] entities;
    private final ArrayList<MovingEntity> movingEntities;
    private final Entity[][] entitiesMap;
    private final int texture;
    private int number;
    private Level next;
    private Level warpzone;
    private LevelType levelType;
    private boolean warpzoneCompleted;

    public Level(
            boolean[][] map,
            boolean[][] climbables,
            int width,
            Point spawnpoint,
            EntityChain[] entities,
            ArrayList<MovingEntity> movingEntities,
            Entity[][] entitiesMap,
            int texture
    ) {
        this.map = map;
        this.climbables = climbables;
        this.width = width;
        this.spawnpoint = spawnpoint;
        this.entities = entities;
        this.movingEntities = movingEntities;
        this.entitiesMap = entitiesMap;
        this.texture = texture;
        number = -1;
        next = null;
        warpzone = null;
        levelType = null;
        warpzoneCompleted = false;
    }

    public boolean checkPureCollision(double x, double y) {
        // Test bordo di un blocco
        double borderX = x / 32;
        double borderY = y / 32;
        if (borderX == (int) borderX || borderY == (int) borderY) return false;
        // Bordo mappa (orizzontale)
        if (x < 0 || x >= width * 32) return true;
        // Bordo mappa (verticale)
        if (y < 0) y += TILES_ALONG_Y * 32;
        if (y >= TILES_ALONG_Y * 32) y -= TILES_ALONG_Y * 32;
        // Controllo vero e proprio
        int mapX = (int) (x / 32);
        int mapY = (int) (y / 32);
        return map[mapX][mapY];
    }

    public boolean checkPureCollision(PointD point) {
        return checkPureCollision(point.x, point.y);
    }

    public boolean isTouchingWorldBorders(double x, double y) {
        return (x < 0 || x >= width * 32) || (y < 0 || y >= 14 * 32);
    }

    public boolean checkIfClimbable(double x, double y) {
        // Test bordo di un blocco
        double borderX = x / 32;
        double borderY = y / 32;
        if (borderX == (int) borderX || borderY == (int) borderY) return false;
        // Bordo mappa (orizzontale)
        if (x < 0 || x >= width * 32) return false;
        // Bordo mappa (verticale)
        if (y < 0) y += TILES_ALONG_Y * 32;
        if (y >= TILES_ALONG_Y * 32) y -= TILES_ALONG_Y * 32;
        // Controllo vero e proprio
        int mapX = (int) (x / 32);
        int mapY = (int) (y / 32);
        return climbables[mapX][mapY];
    }

    public boolean checkIfClimbable(PointD point) {
        return checkIfClimbable(point.x, point.y);
    }

    public int getWidth() {
        return width;
    }

    public Point getSpawnpoint() {
        return spawnpoint;
    }

    public int getTexture() {
        return texture;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Level getNext() {
        return next;
    }

    public void setNext(Level nextLevel) {
        next = nextLevel;
    }

    public Level getWarpzone() {
        return warpzone;
    }

    public LevelType getLevelType() {
        return levelType;
    }

    public void setLevelType(LevelType levelType) {
        this.levelType = levelType;
    }

    public EntityChain[] getEntities() {
        return entities;
    }

    public ArrayList<MovingEntity> getMovingEntities() {
        return movingEntities;
    }

    public Entity getEntity(int x, int y) {
        if (x < 0 || x == width) return null;
        if (y < 0) y += TILES_ALONG_Y;
        if (y == TILES_ALONG_Y) y = 0;
        return entitiesMap[x][y];
    }

    public void setWarpzone(Level warpzone) {
        this.warpzone = warpzone;
    }

    public boolean hasWarpzone() {
        return warpzone != null;
    }

    public boolean isWarpzoneCompleted() {
        return warpzoneCompleted;
    }

    public void toggleCompleteWarpzone() {
        warpzoneCompleted = true;
    }

    public void clearEntity(int x, int y) {
        entitiesMap[x][y] = null;
    }

    public void clearEntity(Entity entity) {
        entitiesMap[(int) entity.getX() / 32][(int) entity.getY() / 32] = null;
    }

    public void init(boolean resetWarpzoneState) {
        int x, y;

        for (x = 0; x < width; x++) {
            for (y = 0; y < 10; y++)
                entitiesMap[x][y] = null;
            reInitEntities(entities[x], x);
        }
        for (MovingEntity entity : movingEntities)
            entity.init();
        if (resetWarpzoneState) warpzoneCompleted = false;
    }

    public void reInitEntities(EntityChain entities, int x) {
        if (entities != null) {
            Entity entity;

            entity = entities.getEntity();
            entity.setVisible(true);
            entitiesMap[x][(int) entity.getY() / 32] = entity;
            reInitEntities(entities.getNext(), x);
        }
    }
}