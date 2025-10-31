package entities;

import utils.PointD;

import static game.Level.TILES_ALONG_Y;

public class Player {
    public final static double WIDTH = 22;
    public final static double HEIGHT = 30;
    public final static double BOX_HEIGHT = 26;
    public final static double SPEED_FAST = 2.2;
    public final static double JUMP_POWER = -3.1;
    public final static int JUMP_COOLDOWN = 5;
    public final static double SPEED_SLOW = 1.6;
    public final static double GRAVITY = 0.072;
    public final static int SCORE_LIFE = 20000;
    public final static int DEAD_COUNTER = 150;
    public final static int FIGURE_SPEED = 6;

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
    private boolean jetpackUnlocked;
    private boolean jumping;
    private int jumpCooldown;
    private boolean falling;
    private boolean climbing;
    private final Shoot shoot;
    private boolean hasGun;
    private boolean hasTrophy;
    private boolean passedLevel;
    private int constantFigureNumber;
    private int maxFigureNumber;
    private double figureNumber;
    private double deadCounter;

    public Player() {
        figureNumber = 0;
        shoot = new Shoot();
        reset();
    }

    public PointD getLocation() {
        return location;
    }

    public PointD[] getCorners() {
        return new PointD[]{
                new PointD(location.x - WIDTH / 2, location.y - BOX_HEIGHT / 2), // TOP-LEFT
                new PointD(location.x + WIDTH / 2, location.y - BOX_HEIGHT / 2), // TOP-RIGHT
                new PointD(location.x - WIDTH / 2, location.y + HEIGHT / 2), // BOTTOM-LEFT
                new PointD(location.x + WIDTH / 2, location.y + HEIGHT / 2), // BOTTOM-RIGHT
        };
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

    public void setSpeed(PointD speed) {
        speedX = speed.x;
        speedY = speed.y;
    }

    public PointD getSpeed() {
        return new PointD(speedX, speedY);
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

    public boolean isJetpackUnlocked() {
        return jetpackUnlocked;
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

    public int getFigureNumber() {
        return (int) (figureNumber / FIGURE_SPEED) + constantFigureNumber;
    }

    public double getDeadCounter() {
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
            if (lives < 3)
                lives++;
        this.score += score;
    }

    public void setIfIsClimbing(boolean climbing) {
        this.climbing = climbing;
    }

    public void setIfIsJumping(boolean jumping) {
        if (jumping != this.jumping) {
            this.jumping = jumping;
            jumpCooldown = JUMP_COOLDOWN;
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
        if (location.y < 0) location.y += TILES_ALONG_Y * 32;
        if (location.y >= TILES_ALONG_Y * 32) location.y -= TILES_ALONG_Y * 32;
    }

    // Ricarica il jetpack
    public void rechargeJetpack() {
        jetpackValue = 60;
    }

    // Sblocca la possibilità di usare il jetpack
    public void unlockJetpack() {
        jetpackUnlocked = true;
    }

    // Attiva o disattiva il jetpack
    public void triggerJetpackToggle() {
        onJetpack = !onJetpack && jetpackValue > 0;
        if (onJetpack) {
            jumping = false;
            falling = false;
        } else {
            falling = true;
        }
        figureNumber = 0;
        jetpackUnlocked = false;
    }

    // Modifica la direzione del giocatore
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
        speedX = 0;
        speedY = 0;
        jetpackValue = 0;
        alive = true;
        directionX = 0;
        directionY = 0;
        onJetpack = false;
        jetpackUnlocked = true;
        jumping = false;
        jumpCooldown = 0;
        falling = true;
        climbing = false;
        shoot.setDirection(0);
        hasGun = false;
        hasTrophy = false;
        passedLevel = false;
        constantFigureNumber = 0;
        maxFigureNumber = 1;
        figureNumber = 0;
        deadCounter = DEAD_COUNTER;
    }

    //FUNZIONE UTILIZZATA PER RIPARTIRE IN UN LIVELLO
    public void restart() {
        alive = true;
        lives--;
        directionX = 0;
        directionY = 0;
        speedX = 0;
        speedY = 0;
        onJetpack = false;
        jetpackUnlocked = true;
        jumping = false;
        jumpCooldown = 0;
        falling = true;
        climbing = false;
        shoot.setDirection(0);
        passedLevel = false;
        constantFigureNumber = 0;
        maxFigureNumber = 1;
        figureNumber = 0;
        deadCounter = DEAD_COUNTER;
    }

    //FUNZIONE UTILIZZATA AL PASSAGGIO DA LIVELLO
    public void passLevel() {
        jetpackValue = 0;
        directionX = 0;
        directionY = 0;
        speedX = 0;
        speedY = 0;
        onJetpack = false;
        jetpackUnlocked = true;
        jumping = false;
        jumpCooldown = 0;
        falling = true;
        climbing = false;
        shoot.setDirection(0);
        hasGun = false;
        hasTrophy = false;
        passedLevel = false;
        constantFigureNumber = 0;
        maxFigureNumber = 1;
        figureNumber = 0;
        deadCounter = DEAD_COUNTER;
    }

    public void update(double deltaT) {
        if (shoot.isVisible())
            shoot.update(deltaT);
        if (Math.abs(shoot.getX() - location.x) > 400)
            shoot.setDirection(0);
        if (!alive && deadCounter >= 0)
            deadCounter -= deltaT * 60;
        if (onJetpack) {
            if (alive)
                jetpackValue -= 0.1 * deltaT * 60;
            if (jetpackValue <= 0)
                triggerJetpackToggle();
        }
        if (jumpCooldown > 0)
            jumpCooldown--;
        setIfIsJumping(speedY < 0 && !onJetpack && !climbing);
        setIfIsFalling(speedY > 0 && !onJetpack && !climbing);
        if (jumping || falling) {
            constantFigureNumber = directionX == Directions.LEFT ? 8 : 7;
            maxFigureNumber = 1;
        } else if (onJetpack) {
            constantFigureNumber = directionX == Directions.LEFT ? 15 : 12;
            maxFigureNumber = 3;
        } else if (climbing) {
            constantFigureNumber = 9;
            maxFigureNumber = 3;
        } else if (directionX == 0) {
            constantFigureNumber = 0;
            maxFigureNumber = 1;
        } else {
            constantFigureNumber = directionX == Directions.LEFT ? 4 : 1;
            maxFigureNumber = 3;
        }
    }

    public void shoot() {
        shoot.setDirection(directionX);
        shoot.setLocation(new PointD(location.x + (WIDTH / 2 * directionX), location.y));
    }

    public void die() {
        alive = false;
    }

    public void updateFigureNumber(double deltaT) {
        boolean isWalking = speedX != 0;
        boolean isClimbing = climbing && (speedX != 0 || speedY != 0);
        if (onJetpack || isWalking || isClimbing) {
            figureNumber += deltaT * 60;
            if (figureNumber >= maxFigureNumber * Player.FIGURE_SPEED)
                figureNumber -= maxFigureNumber * Player.FIGURE_SPEED;
        }
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
            }
            return true;
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