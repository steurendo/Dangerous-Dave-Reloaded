package entities;

import utils.PointD;

import static entities.Directions.STILL;

public class Shoot {
    private final static double SPEED = 5;

    private PointD location;
    private int direction;

    public Shoot() {
        location = new PointD();
        direction = STILL;
    }

    public double getX() {
        return location.x;
    }

    public double getY() {
        return location.y;
    }

    public PointD getLocation() {
        return location;
    }

    public int getDirection() {
        return direction;
    }

    public boolean isVisible() {
        return direction != STILL;
    }

    public void setLocation(PointD location) {
        this.location = location;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public void update() {
        location.x += direction * SPEED;
    }

    public void reset() {
        direction = STILL;
    }
}