package entities;

import utils.PointD;

public class Entity {
    protected int texture;
    protected double textureWidth;
    protected double textureHeight;
    protected int code;
    protected double textureY;
    protected int figuresNumber;
    protected PointD location;
    protected double width;
    protected double height;
    protected int scoreValue;
    protected boolean mortal;
    //protected boolean climbable;
    protected boolean visible;

    public Entity(int texture, int code, int figuresNumber, PointD location, double width, double height, int scoreValue, boolean mortal) {
        this.texture = texture;
        this.code = code;
        textureWidth = 192;
        textureHeight = 928;
        textureY = code * 32;
        this.figuresNumber = figuresNumber;
        this.location = location;
        this.width = width;
        this.height = height;
        this.scoreValue = scoreValue;
        this.mortal = mortal;
        visible = true;
    }

    public int getTexture() {
        return texture;
    }

    public int getCode() {
        return code;
    }

    public double getTextureWidth() {
        return textureWidth;
    }

    public double getTextureHeight() {
        return textureHeight;
    }

    public double getTextureY() {
        return textureY;
    }

    public int getFiguresNumber() {
        return figuresNumber;
    }

    public PointD getLocation() {
        return location;
    }

    public double getX() {
        return location.x;
    }

    public double getY() {
        return location.y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public int getScoreValue() {
        return scoreValue;
    }

    public boolean isMortal() {
        return mortal;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setX(double x) {
        location.x = x;
    }

    public void setY(double y) {
        location.y = y;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}