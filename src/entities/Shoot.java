package entities;

import utils.PointD;

public class Shoot {
    private final static double SPEED = 5;

    private PointD location;
    private int direction;

    public Shoot() {
        location = new PointD();
        direction = 0;
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
        return direction != 0;
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
}