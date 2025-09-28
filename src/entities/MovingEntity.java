package entities;

import utils.Functions;
import utils.PointD;

import java.awt.*;

public class MovingEntity extends Entity {
    public final static int DEAD_COUNTER = 150;
    private final static int SHOOT_RELOAD = 200;

    private final PointD spawnpoint;
    private final Shoot shoot;
    private double movingNumber;
    private boolean alive;
    private double deadCounter;
    private double shootReload;

    public MovingEntity(int texture, int code, int figuresNumber, PointD spawnpoint, double width, double height, int scoreValue, boolean mortal) {
        super(texture, code, figuresNumber, spawnpoint.clone(), width, height, scoreValue, mortal);

        this.spawnpoint = spawnpoint;
        textureY = 58;
        if (code >= 1)
            textureY += 30;
        if (code >= 2)
            textureY += 34;
        if (code >= 3)
            textureY += 42;
        if (code >= 4)
            textureY += 40;
        if (code >= 5)
            textureY += 10;
        if (code >= 6)
            textureY += 16;
        if (code >= 7)
            textureY += 30;
        if (code >= 8)
            textureY += 32;
        if (code >= 9)
            textureY += 8;
        textureWidth = 720;
        textureHeight = 304;
        movingNumber = 0;
        alive = true;
        deadCounter = DEAD_COUNTER;
        shoot = new Shoot();
        shootReload = 0;
    }

    public boolean isAlive() {
        return alive;
    }

    public Shoot getShoot() {
        return shoot;
    }

    public void init() {
        visible = true;
        alive = true;
        deadCounter = DEAD_COUNTER;
        movingNumber = 0;
        location = spawnpoint.clone();
    }

    public void die() {
        alive = false;
    }

    public boolean checkCollisionWithShoot(Shoot shoot) {
        PointD shootLocation;

        shootLocation = shoot.getLocation();
        return (Math.abs(location.x - shootLocation.x) < width / 2 + 8 &&
                Math.abs(location.y - shootLocation.y) < height / 2 + 3);
    }

    public void update(double deltaT, PointD playerLocation) {
        if (!alive && deadCounter >= 0)
            deadCounter -= deltaT * 60;
        if (deadCounter <= 0)
            visible = false;
        if (shoot.isVisible())
            shoot.update(deltaT);
        if (alive) {
            if (Math.abs(shoot.getX() - location.x) > 400)
                shoot.setDirection(0);
            if (shootReload > 0)
                shootReload -= deltaT * 60;
            if (shootReload <= 0) {
                int direction = location.x > playerLocation.x ? Directions.LEFT : Directions.RIGHT;
                shoot.setLocation(new PointD(location.x + (width / 2 * direction), location.y));
                shoot.setDirection(direction);
                shootReload = SHOOT_RELOAD;
            }

            PointD delta = new PointD();
            if (code == 0) {
                delta.x += Math.cos(movingNumber * 1.8) * 4;
                delta.y += Math.sin(movingNumber * 10) * 2;
            } else if (code == 1) {
                delta.x -= Math.sin(movingNumber * 2) * 2.6;
                delta.y += Math.cos(movingNumber * 2) * 0.8;
            } else if (code == 2) {
                delta.x -= Math.cos(movingNumber * 4) * 2;
                delta.y += Math.sin(movingNumber * 4) * 2;
                delta.x -= Math.cos(movingNumber * 15) * 1;
                delta.y += Math.sin(movingNumber * 15) * 1;
            } else if (code == 3)
                delta.x += Math.cos(movingNumber * 4) > 0 ? 4 : -4;
            else if (code == 4) {
                delta.x += Math.cos(movingNumber * 4) * 1.4;
                delta.y += Math.sin(movingNumber * 4) * 3.4;
            } else if (code == 5) {
                delta.x -= Math.cos(movingNumber * 4) * 2;
                delta.y += Math.sin(movingNumber * 4) * 2;
                delta.x -= Math.cos(movingNumber * 15) * 1;
                delta.y -= Math.sin(movingNumber * 15) * 1;
            } else if (code == 6) {
                delta.x += Math.cos(movingNumber * 4) > 0 ? 4 : -4;
                delta.y += Math.sin(movingNumber * 60) * 2;
            } else if (code == 7)
                delta.x += Math.cos(movingNumber * 4) * 4;
            location = location.plus(delta.scale(deltaT * 60));
            movingNumber = (movingNumber + 0.01 * deltaT * 60) % 360;
        }
    }
}