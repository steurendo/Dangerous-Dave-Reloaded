package game;

import entities.Entity;
import entities.EntityChain;
import entities.MovingEntity;
import entities.Player;
import utils.Functions;
import utils.PointD;

import java.awt.*;
import java.util.*;

public class Level {
    public final static int TILES_ALONG_Y = 13;  // 1 SOTTO, DUE SOPRA IN PIù
    public final static int OFFSET_Y = 2;

    private final boolean[][] map;
    private final int width;
    private final Point spawnpoint;
    private final EntityChain[] entities;
    private final ArrayList<MovingEntity> movingEntities;
    private final Entity[][] entitiesMap;
    private final int texture;
    private final int number;
    private Level next;
    private Level warpzone;

    public Level(boolean[][] map, int width, Point spawnpoint, EntityChain[] entities, ArrayList<MovingEntity> movingEntities, Entity[][] entitiesMap, int texture, int number, Level next) {
        this.map = map;
        this.width = width;
        this.spawnpoint = spawnpoint;
        this.entities = entities;
        this.movingEntities = movingEntities;
        this.entitiesMap = entitiesMap;
        this.texture = texture;
        this.number = number;
        this.next = next;
        warpzone = null;
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

    public Level getNext() {
        return next;
    }

    public void setNext(Level nextLevel) {
        next = nextLevel;
    }

    public Level getWarpzone() {
        return warpzone;
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

    public void clearEntity(int x, int y) {
        entitiesMap[x][y] = null;
    }

    public void clearEntity(Entity entity) {
        entitiesMap[(int) entity.getX() / 32][(int) entity.getY() / 32] = null;
    }

    public void init() {
        int x, y;

        for (x = 0; x < width; x++) {
            for (y = 0; y < 10; y++)
                entitiesMap[x][y] = null;
            reInitEntities(entities[x], x);
        }
        for (MovingEntity entity : movingEntities)
            entity.init();
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