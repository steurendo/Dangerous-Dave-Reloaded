package game;

import entities.Entity;
import entities.EntityChain;
import entities.MovingEntity;
import entities.Player;
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
    private final Level next;
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
        return map[(int) (x > (int) x ? Math.ceil(x) : x) / 32][(int) (y > (int) y ? Math.ceil(y) : y) / 32];
    }

    public int checkCollisionX(double x, double y) {
        if (map[(int) ((x > (int) x ? Math.ceil(x) : x) + (Player.WIDTH / 2 - 1)) / 32][(int) (y + (Player.HEIGHT / 2 - 1)) / 32] ||
                map[(int) ((x > (int) x ? Math.ceil(x) : x) + (Player.WIDTH / 2 - 1)) / 32][(int) (y - Player.HEIGHT / 2) / 32])
            return 1;
        if (map[(int) (x - Player.WIDTH / 2) / 32][(int) (y + (Player.HEIGHT / 2 - 1)) / 32] ||
                map[(int) (x - Player.WIDTH / 2) / 32][(int) (y - Player.HEIGHT / 2) / 32])
            return -1;

        return 0;
    }

    public int checkCollisionX(PointD location) {
        return checkCollisionX(location.x, location.y);
    }

    public int checkCollisionY(double x, double y) {
        if (map[(int) (x + (Player.WIDTH / 2 - 1)) / 32][(int) ((y > (int) y ? Math.ceil(y) : y) + (Player.HEIGHT / 2 - 1)) / 32] ||
                map[(int) (x - Player.WIDTH / 2) / 32][(int) ((y > (int) y ? Math.ceil(y) : y) + (Player.HEIGHT / 2 - 1)) / 32])
            return 1;
        if (map[(int) (x + (Player.WIDTH / 2 - 1)) / 32][(int) (y - Player.HEIGHT / 2) / 32] ||
                map[(int) (x - Player.WIDTH / 2) / 32][(int) (y - Player.HEIGHT / 2) / 32])
            return -1;

        return 0;
    }

    public int checkCollisionY(PointD location) {
        return checkCollisionY(location.x, location.y);
    }

    //public boolean checkCollision(int x, int y) { return checkCollision(new Point(x, y)); }
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
        return entitiesMap[x][y];
    }

    public void setWarpzone(Level warpzone) {
        this.warpzone = warpzone;
    }

    public void clearEntity(int x, int y) {
        entitiesMap[x][y] = null;
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