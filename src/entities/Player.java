package entities;

import utils.PointD;

import java.awt.*;

public class Player {
    public final static int WIDTH = 22;
    public final static int HEIGHT = 30;
    public final static double SPEED_FAST = 2;
    public final static double JUMP_POWER = -4;
    public final static double JUMP_SPEED_X = 2;
    public final static int JUMP_COOLDOWN = 5;
    public final static double SPEED_SLOW = 1.8;
    public final static double GRAVITY = 0.1177;
    public final static double GRAVITY_MAX = 2;
    public final static int SCORE_LIFE = 20000;
    public final static int DEAD_COUNTER = 150;
    public final static int RIGHT = 1;
    public final static int LEFT = -1;
    public final static int FIGURE_SPEED = 5;

    private PointD location;
    private int lives;
    private int score;
    private double speedX;
    private double speedY;
    private double jetpackValue;
    private boolean alive;
    private int directionX;
    private int directionY;
    private boolean onJetpack;
    private boolean jetpackToggle;
    private boolean jumping;
    private int jumpCooldown;
    private boolean falling;
    private boolean climbing;
    private Shoot shoot;
    private boolean hasGun;
    private boolean hasTrophy;
    private boolean passedLevel;
    private int constantFigureNumber;
    private int maxFigureNumber;
    private int figureNumber;
    private int deadCounter;

    public Player() {
        figureNumber = 0;
        shoot = new Shoot();
        reset();
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

    public int getLives() {
        return lives;
    }

    public int getScore() {
        return score;
    }

    public double getSpeedX() {
        return speedX;
    }

    public double getSpeedY() {
        return speedY;
    }

    public double getJetpackValue() {
        return jetpackValue;
    }

    public boolean isAlive() {
        return alive;
    }

    public int getDirectionX() {
        return directionX;
    }

    public int getDirectionY() {
        return directionY;
    }

    public boolean isOnJetpack() {
        return onJetpack;
    }

    public boolean getJetpackToggle() {
        return jetpackToggle;
    }

    public boolean isJumping() {
        return jumping;
    }

    public boolean isFalling() {
        return falling;
    }

    public boolean isClimbing() {
        return climbing;
    }

    public boolean canShoot() {
        return (hasGun && !shoot.isVisible());
    }

    public boolean getIfHasGun() {
        return hasGun;
    }

    public Shoot getShoot() {
        return shoot;
    }

    public boolean getIfHasTrophy() {
        return hasTrophy;
    }

    public int getPureFigureNumber() {
        return figureNumber;
    }

    public int getFigureNumber() {
        return (figureNumber / FIGURE_SPEED) + constantFigureNumber;
    }

    public int getMaxFigureNumber() {
        return maxFigureNumber;
    }

    public int getDeadCounter() {
        return deadCounter;
    }

    public int getJumpCooldown() {
        return jumpCooldown;
    }

    public boolean hasPassedLevel() {
        return passedLevel;
    }

    public void addScore(int score) {
        if ((this.score + score) / SCORE_LIFE > this.score / SCORE_LIFE)
            if (lives < 4)
                lives++;
        this.score += score;
    }

    public void setJumpCooldown(int jumpCooldown) {
        this.jumpCooldown = jumpCooldown;
    }

    public void setFigureNumber(int figureNumber) {
        this.figureNumber = figureNumber;
    }

    public void setIfIsJumping(boolean jumping) {
        if (jumping != this.jumping) {
            this.jumping = jumping;
            figureNumber = 0;
        }
    }

    public void setIfIsFalling(boolean falling) {
        if (falling != this.falling) {
            this.falling = falling;
            figureNumber = 0;
        }
    }

    public void setSpeedX(double speedX) {
        this.speedX = speedX;
    }

    public void setSpeedY(double speedY) {
        this.speedY = speedY;
    }

    public void setLocation(Point location) {
        setLocation(new PointD(location));
    }

    public void setLocation(PointD location) {
        this.location.x = location.x;
        this.location.y = location.y;
    }

    public void setX(double x) {
        location.x = x;
    }

    public void setY(double y) {
        location.y = y;
    }

    public void moveX() {
        location.x += speedX;
    }

    public void moveY() {
        location.y += speedY;
    }

    //RICARICA IL VALORE DEL JETPACK
    public void rechargeJetpack() {
        jetpackValue = 60;
    }

    //MODIFICA LO STATO DEL GIOCATORE, SE VIVO O MORTO
    public void setAlive(boolean alive) {
        this.alive = alive;
        if (!alive) {
            lives--;
            deadCounter = 60;
        }
    }

    //SBLOCCA LA POSSIBILIT� DI ATTIVARE IL JETPACK
    public void jetpackToggleUnlock() {
        jetpackToggle = true;
    }

    //ATTIVA O DISATTIVA IL JETPACK
    public void triggerJetpackToggle() {
        onJetpack = !onJetpack;
        if (onJetpack) {
            jumping = false;
            falling = false;
        } else
            falling = true;
        figureNumber = 0;
        jetpackToggle = false;
    }

    //MOTIFICA LA DIREZIONE DEL GIOCATORE
    public void setDirectionX(int directionX) {
        if (this.directionX != directionX) {
            this.directionX = directionX;
            figureNumber = 0;
        }
    }

    public void setDirectionY(int directionY) {
        this.directionY = directionY;
    }

    //FUNZIONE USATA ALLA PARTENZA, PER COMINCIARE UNA NUOVA PARTITA
    public void reset() {
        location = new PointD();
        lives = 3;
        score = 0;
        speedX = SPEED_SLOW;
        speedY = 0;
        jetpackValue = 0;
        alive = true;
        directionX = 0;
        directionY = 0;
        onJetpack = false;
        jetpackToggle = true;
        jumping = false;
        jumpCooldown = 0;
        falling = false;
        climbing = false;
        shoot.setDirection(0);
        hasGun = false;
        hasTrophy = false;
        passedLevel = false;
        constantFigureNumber = 0;
        maxFigureNumber = 1;
        figureNumber = 0;
        deadCounter = -1;
    }

    //FUNZIONE UTILIZZATA PER RIPARTIRE IN UN LIVELLO
    public void restart() {
        alive = true;
        lives--;
        directionX = 0;
        directionY = 0;
        speedX = SPEED_SLOW;
        speedY = 0;
        onJetpack = false;
        jetpackToggle = true;
        jumping = false;
        jumpCooldown = 0;
        falling = false;
        climbing = false;
        shoot.setDirection(0);
        passedLevel = false;
        constantFigureNumber = 0;
        maxFigureNumber = 1;
        figureNumber = 0;
        deadCounter = -1;
    }

    //FUNZIONE UTILIZZATA AL PASSAGGIO DA LIVELLO
    public void passLevel() {
        jetpackValue = 0;
        directionX = 0;
        directionY = 0;
        speedX = SPEED_SLOW;
        speedY = 0;
        onJetpack = false;
        jetpackToggle = true;
        jumping = false;
        jumpCooldown = 0;
        falling = false;
        climbing = false;
        shoot.setDirection(0);
        hasGun = false;
        hasTrophy = false;
        passedLevel = false;
        constantFigureNumber = 0;
        maxFigureNumber = 1;
        figureNumber = 0;
        deadCounter = -1;
    }

    public void update() {
        if (shoot.isVisible())
            shoot.update();
        if (Math.abs(shoot.getX() - location.x) > 400)
            shoot.setDirection(0);
        if (!alive && deadCounter >= 0)
            deadCounter--;
        if (onJetpack) {
            if (alive)
                jetpackValue -= 0.1;
            if (jetpackValue <= 0)
                triggerJetpackToggle();
        }
        if (jumpCooldown > 0)
            jumpCooldown--;
        setIfIsJumping(speedY < 0 && !onJetpack && !climbing);
        setIfIsFalling(speedY > 0 && !onJetpack && !climbing);
        if (jumping || falling) {
            constantFigureNumber = directionX == LEFT ? 8 : 7;
            maxFigureNumber = 1;
        } else if (onJetpack) {
            constantFigureNumber = directionX == LEFT ? 15 : 12;
            maxFigureNumber = 3;
        } else if (climbing) {
            constantFigureNumber = 9;
            maxFigureNumber = 3;
        } else if (directionX == 0) {
            constantFigureNumber = 0;
            maxFigureNumber = 1;
        } else {
            constantFigureNumber = directionX == LEFT ? 4 : 1;
            maxFigureNumber = 3;
        }
    }

    public void shoot() {
        shoot.setDirection(directionX);
        shoot.setLocation(new PointD(location.x + (WIDTH / 2 * directionX), location.y));
    }

    public void die() {
        deadCounter = DEAD_COUNTER;
        alive = false;
    }

    public void updateFigureNumber() {
        if (onJetpack || speedX != 0 || climbing && (speedX != 0 || speedY != 0))
            figureNumber = (figureNumber + 1) % (maxFigureNumber * Player.FIGURE_SPEED);
    }

    public boolean checkCollisionWithEntity(Entity entity) {
        if (entity == null)
            return false;

        if (!entity.isVisible())
            return false;

        if (!alive)
            return false;

        if (Math.abs(location.x - entity.getX()) < WIDTH / 2 + entity.getWidth() / 2 &&
                Math.abs(location.y - entity.getY()) < HEIGHT / 2 + entity.getHeight() / 2) //COLLIDE CON UN'ENTITA'
        {
            if (entity.isMortal()) {
                score += entity.getScoreValue(); //IL PLAYER MUORE
                die();

                return true;
            } else {
                if (entity.getCode() == 3) {
                    if (hasTrophy) {
                        addScore(entity.getScoreValue());
                        passedLevel = true;
                    }
                } else {
                    if (entity.getCode() == 0)
                        hasGun = true;
                    else if (entity.getCode() == 1)
                        rechargeJetpack();
                    else {
                        if (entity.getCode() == 2)
                            hasTrophy = true;
                        addScore(entity.getScoreValue());
                    }
                    if (entity.getScoreValue() != 0)
                        entity.setVisible(false);
                }

                return true;
            }
        }

        return false;
    }

    public boolean checkCollisionWithShoot(Shoot shoot) {
        PointD shootLocation;

        shootLocation = shoot.getLocation();
        return (Math.abs(location.x - shootLocation.x) < WIDTH / 2 + 20 &&
                Math.abs(location.y - shootLocation.y) < WIDTH / 2 + 3);
    }
}