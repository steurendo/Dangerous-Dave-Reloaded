package scenarios;

import entities.*;
import game.*;
import ui.AlphaNumInputKeys;
import ui.Keyboard;
import utils.PointD;
import utils.Textures;

import static game.Level.OFFSET_Y;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class ScenarioLevel extends Scenario {
    private final static int FIGURE_SPEED = 6;
    private final static int MAX_FIGURE_NUMBER = 300;

    private final ModelScore modelScore;
    private final Player player;
    private double figureNumber;
    private double movingEntitiesFigureNumber;
    private double softPauseFigureNumber;
    private boolean paused;
    private boolean softPaused;
    private boolean showPlayer;
    private boolean pauseTrigger;

    public ScenarioLevel(Model model, ModelScore modelScore, Textures textures) {
        this.model = model;
        this.modelScore = modelScore;
        this.textures = textures;
        player = model.getPlayer();
        figureNumber = 0;
        movingEntitiesFigureNumber = 0;
        paused = false;
        pauseTrigger = true;
    }


    @Override
    public void commands(double deltaT) {
        LevelType levelType = model.getCurrentLevel().getLevelType();

        // Se il giocatore ha fatto un nuovo punteggio migliore
        if (model.isNewHighScore()) {
            handleNewScorePrompt(modelScore::type);
            if (Keyboard.isKeyDown(GLFW_KEY_ENTER)) {
                modelScore.insertNew();
                setSoftPaused();
                model.reset();
            }
            return;
        }

        // Se il giocatore ha terminato la partita
        if (model.isGameFinished()) {
            if (Keyboard.isKeyDown(GLFW_KEY_SPACE)) {
                if (modelScore.isValidCandidate(player.getScore())) {
                    modelScore.setScore(player.getScore());
                    modelScore.setLevel(model.getCurrentLevel().getNumber());
                    model.setNewHighScore(true);
                }
            } else return;
        }

        // Se il livello è un livello di transizione, forza la camminata verso destra
        if (levelType == LevelType.TRANSITION_LEVEL || levelType == LevelType.TRANSITION_FROM_WARPZONE) {
            int directionX = Directions.RIGHT;
            player.setDirectionX(directionX);
            player.setSpeedX(Player.SPEED_SLOW * directionX * deltaT * 60);
        } else if (levelType != LevelType.TRANSITION_WARPZONE) {
            // Pausa (P)
            if (Keyboard.isKeyDown(GLFW_KEY_P)) {
                if (pauseTrigger) {
                    paused = !paused;
                    pauseTrigger = false;
                }
            } else
                pauseTrigger = true;
            if (paused) return;

            // Se il player sta morendo, non vengono processati comandi
            if (!player.isAlive()) return;

            // Sparo (Ctrl)
            if ((Keyboard.isKeyDown(GLFW_KEY_LEFT_CONTROL) || Keyboard.isKeyDown(GLFW_KEY_RIGHT_CONTROL)) && player.canShoot())
                player.shoot();

            // Jetpack (Shift)
            if (Keyboard.isKeyDown(GLFW_KEY_LEFT_SHIFT) || Keyboard.isKeyDown(GLFW_KEY_RIGHT_SHIFT)) {
                if (player.isJetpackUnlocked()) player.triggerJetpackToggle();
            } else if (!player.isJetpackUnlocked()) player.unlockJetpack();

            // Arrampicata
            if (Keyboard.isKeyDown(GLFW_KEY_UP) ||
                    Keyboard.isKeyDown(GLFW_KEY_DOWN) ||
                    Keyboard.isKeyDown(GLFW_KEY_LEFT) ||
                    Keyboard.isKeyDown(GLFW_KEY_RIGHT)) {
                Level currentLevel = model.getCurrentLevel();
                boolean wouldClimb = false;
                for (PointD corner : player.getCorners()) {
                    if (currentLevel.checkIfClimbable(corner)) {
                        wouldClimb = true;
                        break;
                    }
                }
                boolean canClimb = !player.isOnJetpack() && !player.isFalling();
                player.setIfIsClimbing(canClimb && wouldClimb);
            }

            // Spostamenti verticali (Freccia su, Freccia giù)
            if (Keyboard.isKeyDown(GLFW_KEY_UP) != Keyboard.isKeyDown(GLFW_KEY_DOWN)) {
                int directionY;

                directionY = Keyboard.isKeyDown(GLFW_KEY_UP) ? Directions.UP : Directions.DOWN;
                if (player.getDirectionX() == Directions.STILL) player.setDirectionX(Directions.RIGHT);
                if (player.getDirectionY() == Directions.UP && Keyboard.isKeyDown(GLFW_KEY_DOWN)) {
                    player.setDirectionY(Directions.DOWN);
                } else if (player.getDirectionY() == Directions.DOWN && Keyboard.isKeyDown(GLFW_KEY_UP))
                    player.setDirectionY(Directions.UP);
                else
                    player.setDirectionY(directionY);
                if (player.isOnJetpack() || player.isClimbing()) {
                    player.setSpeedY(Player.SPEED_FAST * directionY * deltaT * 60);
                } else if (directionY == Directions.UP) {
                    if (!player.isJumping() && !player.isFalling() && player.getJumpCooldown() == 0) {
                        player.setSpeedY(Player.JUMP_POWER);
                        player.setIfIsJumping(true);
                    }
                }
            }

            // Spostamenti orizzontali (Freccia sinistra, Freccia destra)
            if (Keyboard.isKeyDown(GLFW_KEY_RIGHT) != Keyboard.isKeyDown(GLFW_KEY_LEFT)) {
                int directionX;

                directionX = Keyboard.isKeyDown(GLFW_KEY_RIGHT) ? Directions.RIGHT : Directions.LEFT;
                if (player.getDirectionX() == Directions.RIGHT && Keyboard.isKeyDown(GLFW_KEY_LEFT))
                    player.setDirectionX(Directions.LEFT);
                else if (player.getDirectionX() == Directions.LEFT && Keyboard.isKeyDown(GLFW_KEY_RIGHT))
                    player.setDirectionX(Directions.RIGHT);
                else
                    player.setDirectionX(directionX);

                if (player.isClimbing() || player.isOnJetpack() || player.isJumping() || player.isFalling())
                    player.setSpeedX(Player.SPEED_FAST * directionX * deltaT * 60);
                else
                    player.setSpeedX(Player.SPEED_SLOW * directionX * deltaT * 60);
                player.setFreeFalling(player.isFalling());
            }

            if (player.isFreeFalling()) {
                int directionX = player.getDirectionX();
                player.setSpeedX(Player.SPEED_FAST * directionX * deltaT * 60);
            }

            if (softPaused) {
                softPaused = !(Keyboard.isKeyDown(GLFW_KEY_LEFT)
                        || Keyboard.isKeyDown(GLFW_KEY_RIGHT)
                        || Keyboard.isKeyDown(GLFW_KEY_UP)
                        || Keyboard.isKeyDown(GLFW_KEY_DOWN));
                if (softPaused) return;
            }
        } else {
            player.setDirectionX(Directions.STILL);
            player.setSpeedX(0);
        }

        // Gravità
        if (!player.isOnJetpack() && !player.isClimbing()) {
            if (player.getSpeedY() == 0)
                player.setSpeedY(Player.SPEED_FAST);
            else
                player.setSpeedY(Math.min(player.getSpeedY() + Player.GRAVITY * deltaT * 60, Player.SPEED_FAST));
        }
    }

    @Override
    public void collisions(double deltaT) {
        if (!player.isAlive() || paused || softPaused) return;

        LevelType levelType = model.getCurrentLevel().getLevelType();
        // Controllo che il giocatore non stia andando in una warpzone
        if (levelType == LevelType.LEVEL || levelType == LevelType.WARPZONE)
            warpzoneCheck();
        else
            transitionCheck();
        // Verifico collisioni
        collisionsWithWorld();
        collisionsWithEntities();
        collisionsWithMovingEntities();
    }

    private void warpzoneCheck() {
        Level currentLevel = model.getCurrentLevel();
        if (currentLevel.isWarpzoneCompleted()) return;
        double playerX = player.getX() + player.getSpeedX();
        boolean playerTouchingLeftBorder = (playerX - Player.WIDTH / 2) / 32 < 0;
        boolean playerTouchingRightBorder = (playerX + Player.WIDTH / 2) / 32 >= currentLevel.getWidth();
        if (playerTouchingLeftBorder || playerTouchingRightBorder) {
            if (currentLevel.hasWarpzone()) model.nextWarpzone();
        }
    }

    private void transitionCheck() {
        Level level = model.getCurrentLevel();
        PointD speed = player.getSpeed();
        PointD[] corners = player.getCorners();

        for (PointD corner : corners) {
            if (level.isTouchingWorldBorders(corner.x + speed.x, corner.y + speed.y)) {
                model.nextLevel(this::setSoftPaused);
                return;
            }
        }
    }

    private void collisionsWithWorld() {
        Level level = model.getCurrentLevel();
        PointD speed = player.getSpeed();
        PointD[] corners = player.getCorners();

        for (PointD corner : corners) {
            // Collisione lungo y
            if (level.checkPureCollision(corner.x, corner.y + speed.y)) {
                if (player.isJumping()) {
                    speed.y = Player.SPEED_FAST;
                } else {
                    if (player.isOnJetpack() || player.isClimbing())
                        player.setY(Math.round((corner.y + speed.y) / 32) * 32 - player.getDirectionY() * Player.HEIGHT / 2);
                    else {
                        player.setY(Math.round((corner.y + speed.y) / 32) * 32 - Player.HEIGHT / 2);
                    }
                    speed.y = 0;
                    player.setFreeFalling(false);
                }
            }
            // Collisione lungo x
            if (level.checkPureCollision(corner.x + speed.x, corner.y)) {
                speed.x = 0;
                player.setFreeFalling(false);
            }
        }
        player.setSpeed(speed);
    }

    private void collisionsWithEntities() {
        Level currentLevel = model.getCurrentLevel();

        Entity entity;
        // Collisione con entità (x, y + 1)
        entity = currentLevel.getEntity((int) player.getX() / 32, (int) player.getY() / 32 + 1);
        if (player.checkCollisionWithEntity(entity) && !entity.isMortal()) currentLevel.clearEntity(entity);
        // Collisione con entità (x + 1, y)
        entity = currentLevel.getEntity((int) player.getX() / 32 + 1, (int) player.getY() / 32);
        if (player.checkCollisionWithEntity(entity) && !entity.isMortal()) currentLevel.clearEntity(entity);
        // Collisione con entità (x, y - 1)
        entity = currentLevel.getEntity((int) player.getX() / 32, (int) player.getY() / 32 - 1);
        if (player.checkCollisionWithEntity(entity) && !entity.isMortal()) currentLevel.clearEntity(entity);
        // Collisione con entità (x - 1, y)
        entity = currentLevel.getEntity((int) player.getX() / 32 - 1, (int) player.getY() / 32);
        if (player.checkCollisionWithEntity(entity) && !entity.isMortal()) currentLevel.clearEntity(entity);
    }

    private void collisionsWithMovingEntities() {
        for (MovingEntity entity : model.getCurrentLevel().getMovingEntities()) {
            if (entity.isVisible()) {
                if (entity.getShoot().isVisible())
                    if (model.getCurrentLevel().checkPureCollision(entity.getShoot().getX() + entity.getShoot().getDirection() * 20, entity.getShoot().getY()))
                        entity.getShoot().setDirection(0);
                if (entity.getShoot().isVisible())
                    if (player.checkCollisionWithShoot(entity.getShoot()) && player.isAlive()) {
                        player.die();
                        entity.getShoot().setDirection(0);
                    }
                if (player.getShoot().isVisible())
                    if (entity.checkCollisionWithShoot(player.getShoot()) && entity.isAlive()) {
                        entity.die();
                        player.getShoot().setDirection(0);
                    }
                if (entity.isAlive())
                    if (player.checkCollisionWithEntity(entity))
                        entity.die();
            }
        }
    }

    @Override
    public void update(double deltaT) {
        if (paused) return;
        player.updateFigureNumber(deltaT);
        figureNumber += deltaT * 60;
        if (figureNumber >= MAX_FIGURE_NUMBER) figureNumber -= MAX_FIGURE_NUMBER;

        if (!player.isAlive() && player.getDeadCounter() <= 0) manageDeath();

        if (softPaused) {
            softPauseFigureNumber += deltaT * 60;
            if (softPauseFigureNumber >= 16) {
                softPauseFigureNumber -= 16;
                showPlayer = !showPlayer;
            }
            return;
        }
        movingEntitiesFigureNumber += deltaT * 60;
        if (movingEntitiesFigureNumber >= MAX_FIGURE_NUMBER) movingEntitiesFigureNumber -= MAX_FIGURE_NUMBER;

        player.update(deltaT);
        for (MovingEntity entity : model.getCurrentLevel().getMovingEntities())
            entity.update(deltaT, player.getLocation());

        //SPARO
        if (player.getShoot().isVisible())
            if (model.getCurrentLevel().checkPureCollision(player.getShoot().getX() + player.getShoot().getDirection() * 8, player.getShoot().getY()))
                player.getShoot().setDirection(0);
        if (player.isAlive()) {
            //SPOSTAMENTO ORIZZONTALE
            if (player.getSpeedX() != 0) {
                player.moveX();
                player.setSpeedX(0);
            }
            //SPOSTAMENTO VERTICALE
            if (player.getSpeedY() != 0) {
                player.moveY();
                if (player.isOnJetpack() || player.isClimbing())
                    player.setSpeedY(0);
            }
            if (player.getSpeedY() < Player.JUMP_POWER)
                player.setSpeedY(Player.JUMP_POWER);

            // Player has passed level
            if (player.hasPassedLevel()) model.nextLevel();
        }
    }

    private void setSoftPaused() {
        softPaused = true;
        softPauseFigureNumber = 0;
        showPlayer = true;
    }

    private void manageDeath() {
        if (player.getLives() > 0) {
            PointD spawnpoint = new PointD(
                    model.getCurrentLevel().getSpawnpoint().x * 32 + 16,
                    model.getCurrentLevel().getSpawnpoint().y * 32 + (32 - Player.HEIGHT / 2));
            player.setLocation(spawnpoint);
            player.restart();
        } else model.setGameFinished(true);
        setSoftPaused();
    }

    @Override
    public void render(double deltaT) {
        int i;
        double viewport;
        PointD playerLocation;
        int levelWidth;
        int figureNumber = (int) this.figureNumber;
        int movingEntitiesFigureNumber = (int) this.movingEntitiesFigureNumber;
        Level level = model.getCurrentLevel();

        levelWidth = level.getWidth() * 32;
        playerLocation = player.getLocation();
        if (playerLocation.x < 320)
            viewport = 0;
        else if (playerLocation.x > level.getWidth() * 32 - 320)
            viewport = level.getWidth() * 32 - 640;
        else
            viewport = playerLocation.x - 320;
        // Livello attuale
        Textures.bindTexture(level.getTexture());
        glBegin(GL_QUADS);
        glTexCoord2d((viewport / levelWidth), 0);
        glVertex2d(0, (32d / 400));
        glTexCoord2d(((viewport + 640) / levelWidth), 0);
        glVertex2d(1, (32d / 400));
        glTexCoord2d(((viewport + 640) / levelWidth), (300d / 320));
        glVertex2d(1, (332d / 400));
        glTexCoord2d((viewport / levelWidth), (300d / 320));
        glVertex2d(0, (332d / 400));
        glEnd();

        // Finestra di gioco
        int startX, endX;

        if ((int) viewport / 32 < 5)
            startX = 0;
        else
            startX = (int) viewport / 32 - 5;
        endX = Math.min((int) viewport / 32 + 25, levelWidth / 32);

        // Entità ferme
        for (i = startX; i < endX; i++)
            drawEntities(level.getEntities()[i], viewport);

        // Schermata di fine gioco
        if (level.getLevelType() == LevelType.ENDGAME) {
            Textures.bindTexture(textures.getTextureScoreParts());
            glBegin(GL_QUADS);
            glTexCoord2d(0, 0);
            glVertex2d((48d / 640), (64d / 400));
            glTexCoord2d((560d / 640), 0);
            glVertex2d((608d / 640), (64d / 400));
            glTexCoord2d((560d / 640), (256d / 596));
            glVertex2d((608d / 640), (320d / 400));
            glTexCoord2d(0, (256d / 596));
            glVertex2d((48d / 640), (320d / 400));
            glEnd();
        }
        else {
            // Punteggio
            for (i = 0; i < 7; i++) {
                int digit;
                digit = player.getScore() / (int) Math.pow(10, 6 - i) % 10;
                Textures.bindTexture(textures.getTextureGameParts());
                glBegin(GL_QUADS);
                glTexCoord2d((16d * digit) / 896, (94d / 290));
                glVertex2d((114d + 16 * i) / 640, (2d / 400));
                glTexCoord2d((16d * digit + 16) / 896, (94d / 290));
                glVertex2d((130d + 16 * i) / 640, (2d / 400));
                glTexCoord2d((16d * digit + 16) / 896, (114d / 290));
                glVertex2d((130d + 16 * i) / 640, (22d / 400));
                glTexCoord2d((16d * digit) / 896, (114d / 290));
                glVertex2d((114d + 16 * i) / 640, (22d / 400));
                glEnd();
            }

            // Numero livello
            for (i = 0; i < 2; i++) {
                int digit;

                digit = level.getNumber() / (int) Math.pow(10, 1 - i) % 10;
                Textures.bindTexture(textures.getTextureGameParts());
                glBegin(GL_QUADS);
                glTexCoord2d((16d * digit) / 896, (94d / 290));
                glVertex2d((352d + 16 * i) / 640, (2d / 400));
                glTexCoord2d((16d * digit + 16) / 896, (94d / 290));
                glVertex2d((368d + 16 * i) / 640, (2d / 400));
                glTexCoord2d((16d * digit + 16) / 896, (114d / 290));
                glVertex2d((368d + 16 * i) / 640, (22d / 400));
                glTexCoord2d((16d * digit) / 896, (114d / 290));
                glVertex2d((352d + 16 * i) / 640, (22d / 400));
                glEnd();
            }

            // Vite rimanenti
            Textures.bindTexture(textures.getTextureGameParts());
            for (i = 0; i < player.getLives(); i++) {
                glBegin(GL_QUADS);
                glTexCoord2d(0, (216d / 290));
                glVertex2d(((512d + i * 32) / 640), 0);
                glTexCoord2d((32d / 896), (216d / 290));
                glVertex2d(((544d + i * 32) / 640), 0);
                glTexCoord2d((32d / 896), (240d / 290));
                glVertex2d(((544d + i * 32) / 640), (24d / 400));
                glTexCoord2d(0, (240d / 290));
                glVertex2d(((512d + i * 32) / 640), (24d / 400));
                glEnd();
            }

            // Jetpack
            if (player.getJetpackValue() > 0) {
                // Scritta
                Textures.bindTexture(textures.getTextureGameParts());
                glBegin(GL_QUADS);
                glTexCoord2d(0, (114d / 290));
                glVertex2d(0, (340d / 400));
                glTexCoord2d((124d / 896), (114d / 290));
                glVertex2d((124d / 640), (340d / 400));
                glTexCoord2d((124d / 896), (136d / 290));
                glVertex2d((124d / 640), (362d / 400));
                glTexCoord2d(0, (136d / 290));
                glVertex2d(0, (362d / 400));
                glEnd();
                // Frame energia rimanente
                Textures.bindTexture(textures.getTextureGameParts());
                glBegin(GL_QUADS);
                glTexCoord2d(0, (184d / 290));
                glVertex2d((144d / 640), (340d / 400));
                glTexCoord2d((256d / 896), (184d / 290));
                glVertex2d((400d / 640), (340d / 400));
                glTexCoord2d((256d / 896), (208d / 290));
                glVertex2d((400d / 640), (364d / 400));
                glTexCoord2d(0, (208d / 290));
                glVertex2d((144d / 640), (364d / 400));
                glEnd();
                // Energia rimanente
                for (i = 0; i < player.getJetpackValue(); i++) {
                    Textures.bindTexture(textures.getTextureGameParts());
                    glBegin(GL_QUADS);
                    glTexCoord2d(0, (208d / 290));
                    glVertex2d(((152d + i * 4) / 640), (348d / 400));
                    glTexCoord2d((4d / 896), (208d / 290));
                    glVertex2d(((156d + i * 4) / 640), (348d / 400));
                    glTexCoord2d((4d / 896), (216d / 290));
                    glVertex2d(((156d + i * 4) / 640), (356d / 400));
                    glTexCoord2d(0, (216d / 290));
                    glVertex2d(((152d + i * 4) / 640), (356d / 400));
                    glEnd();
                }
            }

            // Pistola
            if (player.getIfHasGun()) {
                // Scritta
                Textures.bindTexture(textures.getTextureGameParts());
                glBegin(GL_QUADS);
                glTexCoord2d(0, (136d / 290));
                glVertex2d((480d / 640), (340d / 400));
                glTexCoord2d((54d / 896), (136d / 290));
                glVertex2d((534d / 640), (340d / 400));
                glTexCoord2d((54d / 896), (158d / 290));
                glVertex2d((534d / 640), (362d / 400));
                glTexCoord2d(0, (158d / 290));
                glVertex2d((480d / 640), (362d / 400));
                glEnd();
                // Icona
                Textures.bindTexture(textures.getTextureGameParts());
                glBegin(GL_QUADS);
                glTexCoord2d(0, (240d / 290));
                glVertex2d((572d / 640), (340d / 400));
                glTexCoord2d((32d / 896), (240d / 290));
                glVertex2d((604d / 640), (340d / 400));
                glTexCoord2d((32d / 896), (262d / 290));
                glVertex2d((604d / 640), (362d / 400));
                glTexCoord2d(0, (262d / 290));
                glVertex2d((572d / 640), (362d / 400));
                glEnd();
            }

            // Coppa
            if (player.getIfHasTrophy()) {
                Textures.bindTexture(textures.getTextureGameParts());
                glBegin(GL_QUADS);
                glTexCoord2d(0, (158d / 290));
                glVertex2d((146d / 640), (370d / 400));
                glTexCoord2d((340d / 896), (158d / 290));
                glVertex2d((486d / 640), (370d / 400));
                glTexCoord2d((340d / 896), (184d / 290));
                glVertex2d((486d / 640), (396d / 400));
                glTexCoord2d(0, (184d / 290));
                glVertex2d((146d / 640), (396d / 400));
                glEnd();
            }

            // Pausa
            if (paused) {
                Textures.bindTexture(textures.getTextureGameParts());
                glBegin(GL_QUADS);
                glTexCoord2d((56d / 896), (136d / 290));
                glVertex2d((500d / 640), (370d / 400));
                glTexCoord2d((160d / 896), (136d / 290));
                glVertex2d((604d / 640), (370d / 400));
                glTexCoord2d((160d / 896), (158d / 290));
                glVertex2d((604d / 640), (392d / 400));
                glTexCoord2d((56d / 896), (158d / 290));
                glVertex2d((500d / 640), (392d / 400));
                glEnd();
            }

            // Entità mobili
            for (MovingEntity entity : level.getMovingEntities()) {
                int textureNumber;
                double limitYTexture, limitY;

                if (entity.isVisible()) {
                    textureNumber = (movingEntitiesFigureNumber / FIGURE_SPEED) % entity.getFiguresNumber();
                    limitY = entity.getY() - OFFSET_Y * 32 + entity.getHeight() / 2 + 32 > 332 ? 332d / 400 : (entity.getY() - OFFSET_Y * 32 + entity.getHeight() / 2 + 32) / 400;
                    limitYTexture = entity.getY() - OFFSET_Y * 32 + entity.getHeight() / 2 + 32 > 332 ? (entity.getTextureY() + entity.getHeight() - (entity.getY() - OFFSET_Y * 32 + entity.getHeight() / 2 + 32 - 332)) / entity.getTextureHeight() : (entity.getTextureY() + entity.getHeight()) / entity.getTextureHeight();
                    Textures.bindTexture(entity.getTexture());
                    if (entity.isAlive()) {
                        glBegin(GL_QUADS);
                        glTexCoord2d((textureNumber * entity.getWidth()) / entity.getTextureWidth(), (entity.getTextureY()) / entity.getTextureHeight());
                        glVertex2d((entity.getX() - entity.getWidth() / 2 - viewport) / 640, (entity.getY() - OFFSET_Y * 32 - entity.getHeight() / 2 + 32) / 400);
                        glTexCoord2d((textureNumber * entity.getWidth() + entity.getWidth()) / entity.getTextureWidth(), (entity.getTextureY()) / entity.getTextureHeight());
                        glVertex2d((entity.getX() + entity.getWidth() / 2 - viewport) / 640, (entity.getY() - OFFSET_Y * 32 - entity.getHeight() / 2 + 32) / 400);
                        glTexCoord2d((textureNumber * entity.getWidth() + entity.getWidth()) / entity.getTextureWidth(), limitYTexture);
                        glVertex2d((entity.getX() + entity.getWidth() / 2 - viewport) / 640, limitY);
                        glTexCoord2d((textureNumber * entity.getWidth()) / entity.getTextureWidth(), limitYTexture);
                        glVertex2d((entity.getX() - entity.getWidth() / 2 - viewport) / 640, limitY);
                        glEnd();
                    } else {
                        glBegin(GL_QUADS);
                        glTexCoord2d(((40d * (figureNumber / 5 % 4)) / 720), (32d / 304));
                        glVertex2d(((entity.getX() - 20 - viewport) / 640), ((entity.getY() - OFFSET_Y * 32 - Player.HEIGHT / 2 - 1 + 32) / 400));
                        glTexCoord2d(((40d * (figureNumber / 5 % 4) + 40) / 720), (32d / 304));
                        glVertex2d(((entity.getX() + 20 - viewport) / 640), ((entity.getY() - OFFSET_Y * 32 - Player.HEIGHT / 2 - 1 + 32) / 400));
                        glTexCoord2d(((40d * (figureNumber / 5 % 4) + 40) / 720), (58d / 304));
                        glVertex2d(((entity.getX() + 20 - viewport) / 640), ((entity.getY() - OFFSET_Y * 32 + Player.HEIGHT / 2 + 32) / 400));
                        glTexCoord2d(((40d * (figureNumber / 5 % 4)) / 720), (58d / 304));
                        glVertex2d(((entity.getX() - 20 - viewport) / 640), ((entity.getY() - OFFSET_Y * 32 + Player.HEIGHT / 2 + 32) / 400));
                        glEnd();
                    }
                    if (entity.getShoot().isVisible()) {
                        Textures.bindTexture(textures.getTextureMovingEntities());
                        glBegin(GL_QUADS);
                        glTexCoord2d((((entity.getShoot().getDirection() == Directions.RIGHT ? 0 : 120d) + (figureNumber / 5 % 3 * 40)) / 720), (298d / 304));
                        glVertex2d(((entity.getShoot().getX() - 20 - viewport) / 640), ((entity.getShoot().getY() - OFFSET_Y * 32 - 3 + 32) / 400));
                        glTexCoord2d((((entity.getShoot().getDirection() == Directions.RIGHT ? 0 : 120d) + (figureNumber / 5 % 3 * 40) + 40) / 720), (298d / 304));
                        glVertex2d(((entity.getShoot().getX() + 20 - viewport) / 640), ((entity.getShoot().getY() - OFFSET_Y * 32 - 3 + 32) / 400));
                        glTexCoord2d((((entity.getShoot().getDirection() == Directions.RIGHT ? 0 : 120d) + (figureNumber / 5 % 3 * 40) + 40) / 720), 1);
                        glVertex2d(((entity.getShoot().getX() + 20 - viewport) / 640), ((entity.getShoot().getY() - OFFSET_Y * 32 + 3 + 32) / 400));
                        glTexCoord2d((((entity.getShoot().getDirection() == Directions.RIGHT ? 0 : 120d) + (figureNumber / 5 % 3 * 40)) / 720), 1);
                        glVertex2d(((entity.getShoot().getX() - 20 - viewport) / 640), ((entity.getShoot().getY() - OFFSET_Y * 32 + 3 + 32) / 400));
                        glEnd();
                    }
                }
            }

            // Giocatore
            Textures.bindTexture(textures.getTextureMovingEntities());
            if (!model.isGameFinished() && (!softPaused || showPlayer)) {
                if (player.isAlive()) {
                    double cutBottom = playerLocation.y - OFFSET_Y * 32 + Player.HEIGHT / 2 + 32 - 332;
                    double cutTop = 30 - (playerLocation.y - OFFSET_Y * 32 - Player.HEIGHT / 2 + 32);
                    if (cutBottom < 0) cutBottom = 0;
                    if (cutTop < 0) cutTop = 0;
                    if (cutBottom < 32 && cutTop < 32) {
                        glBegin(GL_QUADS);
                        glTexCoord2d(((40d * player.getFigureNumber()) / 720), cutTop / 304);
                        glVertex2d(((playerLocation.x - 20 - viewport) / 640), ((playerLocation.y - OFFSET_Y * 32 - Player.HEIGHT / 2 - 1 + 32 + cutTop) / 400));
                        glTexCoord2d(((40d * player.getFigureNumber() + 40) / 720), cutTop / 304);
                        glVertex2d(((playerLocation.x + 20 - viewport) / 640), ((playerLocation.y - OFFSET_Y * 32 - Player.HEIGHT / 2 - 1 + 32 + cutTop) / 400));
                        glTexCoord2d(((40d * player.getFigureNumber() + 40) / 720), ((32 - cutBottom) / 304));
                        glVertex2d(((playerLocation.x + 20 - viewport) / 640), ((playerLocation.y - OFFSET_Y * 32 + Player.HEIGHT / 2 + 32 - cutBottom) / 400));
                        glTexCoord2d(((40d * player.getFigureNumber()) / 720), ((32 - cutBottom) / 304));
                        glVertex2d(((playerLocation.x - 20 - viewport) / 640), ((playerLocation.y - OFFSET_Y * 32 + Player.HEIGHT / 2 + 32 - cutBottom) / 400));
                        glEnd();
                    }
                } else {
                    glBegin(GL_QUADS);
                    glTexCoord2d(((40d * (figureNumber / 5 % 4)) / 720), (32d / 304));
                    glVertex2d(((playerLocation.x - 20 - viewport) / 640), ((playerLocation.y - OFFSET_Y * 32 - Player.HEIGHT / 2 - 1 + 32) / 400));
                    glTexCoord2d(((40d * (figureNumber / 5 % 4) + 40) / 720), (32d / 304));
                    glVertex2d(((playerLocation.x + 20 - viewport) / 640), ((playerLocation.y - OFFSET_Y * 32 - Player.HEIGHT / 2 - 1 + 32) / 400));
                    glTexCoord2d(((40d * (figureNumber / 5 % 4) + 40) / 720), (58d / 304));
                    glVertex2d(((playerLocation.x + 20 - viewport) / 640), ((playerLocation.y - OFFSET_Y * 32 + Player.HEIGHT / 2 + 32) / 400));
                    glTexCoord2d(((40d * (figureNumber / 5 % 4)) / 720), (58d / 304));
                    glVertex2d(((playerLocation.x - 20 - viewport) / 640), ((playerLocation.y - OFFSET_Y * 32 + Player.HEIGHT / 2 + 32) / 400));
                    glEnd();
                }
            }

            // Sparo del giocatore
            if (player.getShoot().isVisible()) {
                Textures.bindTexture(textures.getTextureMovingEntities());
                glBegin(GL_QUADS);
                glTexCoord2d(((player.getShoot().getDirection() == Directions.RIGHT ? 0 : 16d) / 720), (292d / 304));
                glVertex2d(((player.getShoot().getX() - 8 - viewport) / 640), ((player.getShoot().getY() - OFFSET_Y * 32 - 3 + 32) / 400));
                glTexCoord2d((((player.getShoot().getDirection() == Directions.RIGHT ? 0 : 16d) + 16) / 720), (292d / 304));
                glVertex2d(((player.getShoot().getX() + 8 - viewport) / 640), ((player.getShoot().getY() - OFFSET_Y * 32 - 3 + 32) / 400));
                glTexCoord2d((((player.getShoot().getDirection() == Directions.RIGHT ? 0 : 16d) + 16) / 720), (298d / 304));
                glVertex2d(((player.getShoot().getX() + 8 - viewport) / 640), ((player.getShoot().getY() - OFFSET_Y * 32 + 3 + 32) / 400));
                glTexCoord2d(((player.getShoot().getDirection() == Directions.RIGHT ? 0 : 16d) / 720), (298d / 304));
                glVertex2d(((player.getShoot().getX() - 8 - viewport) / 640), ((player.getShoot().getY() - OFFSET_Y * 32 + 3 + 32) / 400));
                glEnd();
            }

            // Scritte dei livelli di transizione nuovo livello
            if (level.getLevelType() == LevelType.TRANSITION_LEVEL) {
                Textures.bindTexture(textures.getTextureLevelsLeft());
                if (model.getRemainingLevels() == 0) { // Finito il gioco
                    glBegin(GL_QUADS);
                    glTexCoord2d(0, (20d / 40));
                    glVertex2d((112d / 640), (114d / 400));
                    glTexCoord2d((432d / 480), (20d / 40));
                    glVertex2d((544d / 640), (114d / 400));
                    glTexCoord2d((432d / 480), (30d / 40));
                    glVertex2d((544d / 640), (124d / 400));
                    glTexCoord2d(0, (30d / 40));
                    glVertex2d((112d / 640), (124d / 400));
                    glEnd();
                } else if (model.getRemainingLevels() == 1) { // Verso ultimo livello
                    glBegin(GL_QUADS);
                    glTexCoord2d(0, (10d / 40));
                    glVertex2d((128d / 640), (114d / 400));
                    glTexCoord2d((400d / 480), (10d / 40));
                    glVertex2d((528d / 640), (114d / 400));
                    glTexCoord2d((400d / 480), (20d / 40));
                    glVertex2d((528d / 640), (124d / 400));
                    glTexCoord2d(0, (20d / 40));
                    glVertex2d((128d / 640), (124d / 400));
                    glEnd();
                } else { // Livello normale
                    glBegin(GL_QUADS);
                    glTexCoord2d(0, 0);
                    glVertex2d((80d / 640), (114d / 400));
                    glTexCoord2d(1, 0);
                    glVertex2d((560d / 640), (114d / 400));
                    glTexCoord2d(1, (10d / 40));
                    glVertex2d((560d / 640), (124d / 400));
                    glTexCoord2d(0, (10d / 40));
                    glVertex2d((80d / 640), (124d / 400));
                    glEnd();

                    // Numero del livello (unità)
                    int levelUnit = model.getRemainingLevels() % 10 - 1;
                    glBegin(GL_QUADS);
                    glTexCoord2d((16d * levelUnit / 480), (30d / 40));
                    glVertex2d((352d / 640), (114d / 400));
                    glTexCoord2d((16d * (levelUnit + 1) / 480), (30d / 40));
                    glVertex2d((368d / 640), (114d / 400));
                    glTexCoord2d((16d * (levelUnit + 1) / 480), 1);
                    glVertex2d((368d / 640), (124d / 400));
                    glTexCoord2d((16d * levelUnit / 480), 1);
                    glVertex2d((352d / 640), (124d / 400));
                    glEnd();

                    // Decina (numero a due cifre)
                    if (model.getRemainingLevels() > 9) {
                        int levelDigit = model.getRemainingLevels() / 10 - 1;
                        glBegin(GL_QUADS);
                        glTexCoord2d((16d * levelDigit / 480), (30d / 40));
                        glVertex2d((336d / 640), (114d / 400));
                        glTexCoord2d((16d * (levelDigit + 1) / 480), (30d / 40));
                        glVertex2d((352d / 640), (114d / 400));
                        glTexCoord2d((16d * (levelDigit + 1) / 480), 1);
                        glVertex2d((352d / 640), (124d / 400));
                        glTexCoord2d((16d * levelDigit / 480), 1);
                        glVertex2d((336d / 640), (124d / 400));
                        glEnd();
                    }
                }
            }

            // Scritte dei livelli di transizione verso warpzone
            if (level.getLevelType() == LevelType.TRANSITION_WARPZONE) {
                Textures.bindTexture(textures.getTextureGameParts());
                glBegin(GL_QUADS);
                glTexCoord2d(0, (262d / 290));
                glVertex2d((70d / 640), (170d / 400));
                glTexCoord2d((142d / 896), (262d / 290));
                glVertex2d((212d / 640), (170d / 400));
                glTexCoord2d((142d / 896), 1);
                glVertex2d((212d / 640), (198d / 400));
                glTexCoord2d(0, 1);
                glVertex2d((70d / 640), (198d / 400));
                glEnd();
                glBegin(GL_QUADS);
                glTexCoord2d((142d / 896), (262d / 290));
                glVertex2d((394d / 640), (170d / 400));
                glTexCoord2d((272d / 896), (262d / 290));
                glVertex2d((524d / 640), (170d / 400));
                glTexCoord2d((272d / 896), 1);
                glVertex2d((524d / 640), (198d / 400));
                glTexCoord2d((142d / 896), 1);
                glVertex2d((394d / 640), (198d / 400));
                glEnd();
            }

            // Sfondo
            Textures.bindTexture(textures.getTextureBackground());
            glBegin(GL_QUADS);
            glTexCoord2d(0, 0);
            glVertex2d(0, 0);
            glTexCoord2d(1, 0);
            glVertex2d(1, 0);
            glTexCoord2d(1, 1);
            glVertex2d(1, 1);
            glTexCoord2d(0, 1);
            glVertex2d(0, 1);
            glEnd();
        }

        if (model.isGameFinished()) {
            Textures.bindTexture(textures.getTextureScoreParts());
            if (model.isNewHighScore()) {
                // Disegna tabella punteggi
                glBegin(GL_QUADS);
                glTexCoord2d(0, (300d / 596));
                glVertex2d((114d / 640), (82d / 400));
                glTexCoord2d((428d / 640), (300d / 596));
                glVertex2d((542d / 640), (82d / 400));
                glTexCoord2d((428d / 640), (536d / 596));
                glVertex2d((542d / 640), (318d / 400));
                glTexCoord2d(0, (536d / 596));
                glVertex2d((114d / 640), (318d / 400));
                glEnd();
                // Disegna punteggi
                // x -> 114 + 62 (cifra più grossa)
                // delta x -> 32 (da score a nome)
                // delta x -> 80 (da nome a numero più grosso)
                // y -> 82 + 78
                // delta y -> 16
                // Disegna "NEW HIGH SCORE"
                glBegin(GL_QUADS);
                glTexCoord2d(0, (536d / 596));
                glVertex2d(0, (354d / 400));
                glTexCoord2d(1, (536d / 596));
                glVertex2d(1, (354d / 400));
                glTexCoord2d(1, (580d / 596));
                glVertex2d(1, (398d / 400));
                glTexCoord2d(0, (580d / 596));
                glVertex2d(0, (398d / 400));
                glEnd();
            } else if (model.getCurrentLevel().getLevelType() != LevelType.ENDGAME) {
                // Disegna "GAME OVER"
                glBegin(GL_QUADS);
                glTexCoord2d(0, (256d / 596));
                glVertex2d(0, (354d / 400));
                glTexCoord2d(1, (256d / 596));
                glVertex2d(1, (354d / 400));
                glTexCoord2d(1, (300d / 596));
                glVertex2d(1, (398d / 400));
                glTexCoord2d(0, (300d / 596));
                glVertex2d(0, (398d / 400));
                glEnd();
            }
        }
    }

    private void drawEntities(EntityChain entities, double viewport) {
        if (entities != null) {
            Entity currentEntity;
            int textureNumber;
            double limitYTexture, limitY;
            int figureNumber = (int) this.figureNumber;

            currentEntity = entities.getEntity();
            if (currentEntity.isVisible()) {
                textureNumber = (figureNumber / FIGURE_SPEED + (int) currentEntity.getX() / 32 + (int) currentEntity.getY() / 32) % currentEntity.getFiguresNumber();
                limitY = currentEntity.getY() - OFFSET_Y * 32 + currentEntity.getHeight() / 2 + 32 > 332 ? 332d / 400 : (currentEntity.getY() - OFFSET_Y * 32 + currentEntity.getHeight() / 2 + 32) / 400;
                limitYTexture = currentEntity.getY() - OFFSET_Y * 32 + currentEntity.getHeight() / 2 + 32 > 332 ? (currentEntity.getTextureY() + currentEntity.getHeight() - (currentEntity.getY() - OFFSET_Y * 32 + currentEntity.getHeight() / 2 + 32 - 332)) / currentEntity.getTextureHeight() : (currentEntity.getTextureY() + currentEntity.getHeight()) / currentEntity.getTextureHeight();
                Textures.bindTexture(currentEntity.getTexture());
                glBegin(GL_QUADS);
                glTexCoord2d((textureNumber * currentEntity.getWidth()) / currentEntity.getTextureWidth(), (currentEntity.getTextureY()) / currentEntity.getTextureHeight());
                glVertex2d((currentEntity.getX() - currentEntity.getWidth() / 2 - viewport) / 640, (currentEntity.getY() - OFFSET_Y * 32 - currentEntity.getHeight() / 2 + 32) / 400);
                glTexCoord2d((textureNumber * currentEntity.getWidth() + currentEntity.getWidth()) / currentEntity.getTextureWidth(), (currentEntity.getTextureY()) / currentEntity.getTextureHeight());
                glVertex2d((currentEntity.getX() + currentEntity.getWidth() / 2 - viewport) / 640, (currentEntity.getY() - OFFSET_Y * 32 - currentEntity.getHeight() / 2 + 32) / 400);
                glTexCoord2d((textureNumber * currentEntity.getWidth() + currentEntity.getWidth()) / currentEntity.getTextureWidth(), limitYTexture);
                glVertex2d((currentEntity.getX() + currentEntity.getWidth() / 2 - viewport) / 640, limitY);
                glTexCoord2d((textureNumber * currentEntity.getWidth()) / currentEntity.getTextureWidth(), limitYTexture);
                glVertex2d((currentEntity.getX() - currentEntity.getWidth() / 2 - viewport) / 640, limitY);
                glEnd();
            }
            drawEntities(entities.getNext(), viewport);
        }
    }

    interface NewScorePrompt {
        void prompt(String entry, boolean cancel);
    }

    private void handleNewScorePrompt(NewScorePrompt callback) {
        if (Keyboard.isKeyDown(GLFW_KEY_DELETE)) callback.prompt(null, true);
        else {
            if (modelScore.getPlayerName().length() == 3) return;
            for (int i = 0; i < AlphaNumInputKeys.keys.length; i++)
                if (Keyboard.isKeyDown(AlphaNumInputKeys.keys[i])){
                    callback.prompt(AlphaNumInputKeys.values.substring(i, i + 1), false);
                    return;
                }
        }
    }
}