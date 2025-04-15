import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;

public class ScenarioLevel extends Scenario {
    private final static int FIGURE_SPEED = 5;
    private final static int MAX_FIGURE_NUMBER = 300;
    private final static int RIGHT = 1;
    private final static int LEFT = -1;
    private final static int UP = -1;
    private final static int DOWN = 1;

    private Player player;
    private int figureNumber;
    private boolean paused;
    private boolean pauseTrigger;

    public ScenarioLevel(Model model, Textures textures) {
        this.model = model;
        this.textures = textures;
        player = model.getPlayer();
        figureNumber = 0;
        paused = false;
        pauseTrigger = true;
    }

    public void render() {
        int i;
        double viewport;
        PointD playerLocation;
        int levelWidth;

        levelWidth = model.getCurrentLevel().getWidth() * 32;
        playerLocation = player.getLocation();
        if (playerLocation.x < 320)
            viewport = 0;
        else if (playerLocation.x > model.getCurrentLevel().getWidth() * 32 - 320)
            viewport = model.getCurrentLevel().getWidth() * 32 - 640;
        else
            viewport = playerLocation.x - 320;
        //PUNTEGGIO
        for (i = 0; i < 7; i++) {
            Integer digit;
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
        //NUMERO LIVELLO
        for (i = 0; i < 2; i++) {
            Integer digit;

            digit = model.getCurrentLevel().getNumber() / (int) Math.pow(10, 1 - i) % 10;
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
        //VITE
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
        //JETPACK
        if (player.getJetpackValue() > 0) {
            //SCRITTA
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
            //BARRA
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
            //VALORE
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
        //PISTOLA
        if (player.getIfHasGun()) {
            //SCRITTA
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
            //ICONA
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
        //COPPA
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
        //PAUSA
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
        //LIVELLO
        Textures.bindTexture(model.getCurrentLevel().getTexture());
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
        //FINESTRA DI GIOCO
        int startX, endX;

        if ((int) viewport / 32 < 5)
            startX = 0;
        else
            startX = (int) viewport / 32 - 5;
        if ((int) viewport / 32 + 25 > levelWidth / 32)
            endX = levelWidth / 32;
        else
            endX = (int) viewport / 32 + 25;
        //ENTITA'
        for (i = startX; i < endX; i++)
            drawEntities(model.getCurrentLevel().getEntities()[i], viewport);
        //ENTITA' MOBILI
        for (MovingEntity entity : model.getCurrentLevel().getMovingEntities()) {
            int textureNumber;
            double limitYTexture, limitY;

            if (entity.isVisible()) {
                textureNumber = (figureNumber / FIGURE_SPEED) % entity.getFiguresNumber();
                limitY = entity.getY() + entity.getHeight() / 2 + 32 > 332 ? 332d / 400 : (entity.getY() + entity.getHeight() / 2 + 32) / 400;
                limitYTexture = entity.getY() + entity.getHeight() / 2 + 32 > 332 ? (entity.getTextureY() + entity.getHeight() - (entity.getY() + entity.getHeight() / 2 + 32 - 332)) / entity.getTextureHeight() : (entity.getTextureY() + entity.getHeight()) / entity.getTextureHeight();
                Textures.bindTexture(entity.getTexture());
                if (entity.isAlive()) {
                    glBegin(GL_QUADS);
                    glTexCoord2d((textureNumber * entity.getWidth()) / entity.getTextureWidth(), (entity.getTextureY()) / entity.getTextureHeight());
                    glVertex2d((entity.getX() - entity.getWidth() / 2 - viewport) / 640, (entity.getY() - entity.getHeight() / 2 + 32) / 400);
                    glTexCoord2d((textureNumber * entity.getWidth() + entity.getWidth()) / entity.getTextureWidth(), (entity.getTextureY()) / entity.getTextureHeight());
                    glVertex2d((entity.getX() + entity.getWidth() / 2 - viewport) / 640, (entity.getY() - entity.getHeight() / 2 + 32) / 400);
                    glTexCoord2d((textureNumber * entity.getWidth() + entity.getWidth()) / entity.getTextureWidth(), limitYTexture);
                    glVertex2d((entity.getX() + entity.getWidth() / 2 - viewport) / 640, limitY);
                    glTexCoord2d((textureNumber * entity.getWidth()) / entity.getTextureWidth(), limitYTexture);
                    glVertex2d((entity.getX() - entity.getWidth() / 2 - viewport) / 640, limitY);
                    glEnd();
                } else {
                    glBegin(GL_QUADS);
                    glTexCoord2d(((40d * (figureNumber / 5 % 4)) / 720), (32d / 304));
                    glVertex2d(((entity.getX() - 20 - viewport) / 640), ((entity.getY() - Player.HEIGHT / 2 - 1 + 32) / 400));
                    glTexCoord2d(((40d * (figureNumber / 5 % 4) + 40) / 720), (32d / 304));
                    glVertex2d(((entity.getX() + 20 - viewport) / 640), ((entity.getY() - Player.HEIGHT / 2 - 1 + 32) / 400));
                    glTexCoord2d(((40d * (figureNumber / 5 % 4) + 40) / 720), (58d / 304));
                    glVertex2d(((entity.getX() + 20 - viewport) / 640), ((entity.getY() + Player.HEIGHT / 2 + 32) / 400));
                    glTexCoord2d(((40d * (figureNumber / 5 % 4)) / 720), (58d / 304));
                    glVertex2d(((entity.getX() - 20 - viewport) / 640), ((entity.getY() + Player.HEIGHT / 2 + 32) / 400));
                    glEnd();
                }
                if (entity.getShoot().isVisible()) {
                    Textures.bindTexture(textures.getTextureMovingEntities());
                    glBegin(GL_QUADS);
                    glTexCoord2d((((entity.getShoot().getDirection() == 1 ? 0 : 120d) + (figureNumber / 5 % 3 * 40)) / 720), (298d / 304));
                    glVertex2d(((entity.getShoot().getX() - 20 - viewport) / 640), ((entity.getShoot().getY() - 3 + 32) / 400));
                    glTexCoord2d((((entity.getShoot().getDirection() == 1 ? 0 : 120d) + (figureNumber / 5 % 3 * 40) + 40) / 720), (298d / 304));
                    glVertex2d(((entity.getShoot().getX() + 20 - viewport) / 640), ((entity.getShoot().getY() - 3 + 32) / 400));
                    glTexCoord2d((((entity.getShoot().getDirection() == 1 ? 0 : 120d) + (figureNumber / 5 % 3 * 40) + 40) / 720), 1);
                    glVertex2d(((entity.getShoot().getX() + 20 - viewport) / 640), ((entity.getShoot().getY() + 3 + 32) / 400));
                    glTexCoord2d((((entity.getShoot().getDirection() == 1 ? 0 : 120d) + (figureNumber / 5 % 3 * 40)) / 720), 1);
                    glVertex2d(((entity.getShoot().getX() - 20 - viewport) / 640), ((entity.getShoot().getY() + 3 + 32) / 400));
                    glEnd();
                }
            }
        }
        //GIOCATORE
        Textures.bindTexture(textures.getTextureMovingEntities());
        if (player.isAlive()) {
            glBegin(GL_QUADS);
            glTexCoord2d(((40d * player.getFigureNumber()) / 720), 0);
            glVertex2d(((playerLocation.x - 20 - viewport) / 640), ((playerLocation.y - Player.HEIGHT / 2 - 1 + 32) / 400));
            glTexCoord2d(((40d * player.getFigureNumber() + 40) / 720), 0);
            glVertex2d(((playerLocation.x + 20 - viewport) / 640), ((playerLocation.y - Player.HEIGHT / 2 - 1 + 32) / 400));
            glTexCoord2d(((40d * player.getFigureNumber() + 40) / 720), (32d / 304));
            glVertex2d(((playerLocation.x + 20 - viewport) / 640), ((playerLocation.y + Player.HEIGHT / 2 + 32) / 400));
            glTexCoord2d(((40d * player.getFigureNumber()) / 720), (32d / 304));
            glVertex2d(((playerLocation.x - 20 - viewport) / 640), ((playerLocation.y + Player.HEIGHT / 2 + 32) / 400));
            glEnd();
        } else {
            glBegin(GL_QUADS);
            glTexCoord2d(((40d * (figureNumber / 5 % 4)) / 720), (32d / 304));
            glVertex2d(((playerLocation.x - 20 - viewport) / 640), ((playerLocation.y - Player.HEIGHT / 2 - 1 + 32) / 400));
            glTexCoord2d(((40d * (figureNumber / 5 % 4) + 40) / 720), (32d / 304));
            glVertex2d(((playerLocation.x + 20 - viewport) / 640), ((playerLocation.y - Player.HEIGHT / 2 - 1 + 32) / 400));
            glTexCoord2d(((40d * (figureNumber / 5 % 4) + 40) / 720), (58d / 304));
            glVertex2d(((playerLocation.x + 20 - viewport) / 640), ((playerLocation.y + Player.HEIGHT / 2 + 32) / 400));
            glTexCoord2d(((40d * (figureNumber / 5 % 4)) / 720), (58d / 304));
            glVertex2d(((playerLocation.x - 20 - viewport) / 640), ((playerLocation.y + Player.HEIGHT / 2 + 32) / 400));
            glEnd();
        }
        //SPARI - GIOCATORE
        if (player.getShoot().isVisible()) {
            Textures.bindTexture(textures.getTextureMovingEntities());
            glBegin(GL_QUADS);
            glTexCoord2d(((player.getShoot().getDirection() == 1 ? 0 : 16d) / 720), (292d / 304));
            glVertex2d(((player.getShoot().getX() - 8 - viewport) / 640), ((player.getShoot().getY() - 3 + 32) / 400));
            glTexCoord2d((((player.getShoot().getDirection() == 1 ? 0 : 16d) + 16) / 720), (292d / 304));
            glVertex2d(((player.getShoot().getX() + 8 - viewport) / 640), ((player.getShoot().getY() - 3 + 32) / 400));
            glTexCoord2d((((player.getShoot().getDirection() == 1 ? 0 : 16d) + 16) / 720), (298d / 304));
            glVertex2d(((player.getShoot().getX() + 8 - viewport) / 640), ((player.getShoot().getY() + 3 + 32) / 400));
            glTexCoord2d(((player.getShoot().getDirection() == 1 ? 0 : 16d) / 720), (298d / 304));
            glVertex2d(((player.getShoot().getX() - 8 - viewport) / 640), ((player.getShoot().getY() + 3 + 32) / 400));
            glEnd();
        }
        //BACKGROUND
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

    private void drawEntities(EntityChain entities, double viewport) {
        if (entities != null) {
            Entity currentEntity;
            int textureNumber;
            double limitYTexture, limitY;

            currentEntity = entities.getEntity();
            if (currentEntity.isVisible()) {
                textureNumber = (figureNumber / FIGURE_SPEED + (int) currentEntity.getX() / 32 + (int) currentEntity.getY() / 32) % currentEntity.getFiguresNumber();
                limitY = currentEntity.getY() + currentEntity.getHeight() / 2 + 32 > 332 ? 332d / 400 : (currentEntity.getY() + currentEntity.getHeight() / 2 + 32) / 400;
                limitYTexture = currentEntity.getY() + currentEntity.getHeight() / 2 + 32 > 332 ? (currentEntity.getTextureY() + currentEntity.getHeight() - (currentEntity.getY() + currentEntity.getHeight() / 2 + 32 - 332)) / currentEntity.getTextureHeight() : (currentEntity.getTextureY() + currentEntity.getHeight()) / currentEntity.getTextureHeight();
                Textures.bindTexture(currentEntity.getTexture());
                glBegin(GL_QUADS);
                glTexCoord2d((textureNumber * currentEntity.getWidth()) / currentEntity.getTextureWidth(), (currentEntity.getTextureY()) / currentEntity.getTextureHeight());
                glVertex2d((currentEntity.getX() - currentEntity.getWidth() / 2 - viewport) / 640, (currentEntity.getY() - currentEntity.getHeight() / 2 + 32) / 400);
                glTexCoord2d((textureNumber * currentEntity.getWidth() + currentEntity.getWidth()) / currentEntity.getTextureWidth(), (currentEntity.getTextureY()) / currentEntity.getTextureHeight());
                glVertex2d((currentEntity.getX() + currentEntity.getWidth() / 2 - viewport) / 640, (currentEntity.getY() - currentEntity.getHeight() / 2 + 32) / 400);
                glTexCoord2d((textureNumber * currentEntity.getWidth() + currentEntity.getWidth()) / currentEntity.getTextureWidth(), limitYTexture);
                glVertex2d((currentEntity.getX() + currentEntity.getWidth() / 2 - viewport) / 640, limitY);
                glTexCoord2d((textureNumber * currentEntity.getWidth()) / currentEntity.getTextureWidth(), limitYTexture);
                glVertex2d((currentEntity.getX() - currentEntity.getWidth() / 2 - viewport) / 640, limitY);
                glEnd();
            }
            drawEntities(entities.getNext(), viewport);
        }
    }

    public void commands() {
        if (Keyboard.isKeyDown(GLFW_KEY_P)) {
            if (pauseTrigger) {
                paused = !paused;
                pauseTrigger = false;
            }
        } else
            pauseTrigger = true;
        if (!paused) {
            player.update();
            if (player.isAlive()) {
                //SPARO
                if ((Keyboard.isKeyDown(GLFW_KEY_LEFT_CONTROL) || Keyboard.isKeyDown(GLFW_KEY_RIGHT_CONTROL)) && player.canShoot())
                    player.shoot();
                //JETPACK
                if (Keyboard.isKeyDown(GLFW_KEY_LEFT_SHIFT) || Keyboard.isKeyDown(GLFW_KEY_RIGHT_SHIFT)) {
                    if (player.getJetpackValue() > 0 && player.getJetpackToggle())
                        player.triggerJetpackToggle();
                } else
                    player.jetpackToggleUnlock();
                //UP DOWN
                if (Keyboard.isKeyDown(GLFW_KEY_UP) != Keyboard.isKeyDown(GLFW_KEY_DOWN)) {
                    int directionY;

                    directionY = Keyboard.isKeyDown(GLFW_KEY_UP) ? UP : DOWN;
                    if (player.getDirectionY() == UP && Keyboard.isKeyDown(GLFW_KEY_DOWN)) {
                        player.setDirectionY(DOWN);
                    } else if (player.getDirectionY() == DOWN && Keyboard.isKeyDown(GLFW_KEY_UP))
                        player.setDirectionY(UP);
                    else
                        player.setDirectionY(directionY);
                    if (player.isOnJetpack() || player.isClimbing()) {
                        if (model.getCurrentLevel().checkCollisionY(player.getX(), player.getY() + Player.SPEED_FAST * directionY) == 0)
                            player.setSpeedY(Player.SPEED_FAST * directionY);
                    } else if (directionY == UP) {
                        if (!player.isJumping() && !player.isFalling() && player.getJumpCooldown() == 0) {
                            player.setSpeedY(Player.JUMP_POWER);
                            player.setJumpCooldown(Player.JUMP_COOLDOWN);
                        }
                    }
                }
                //LEFT RIGHT
                if (Keyboard.isKeyDown(GLFW_KEY_RIGHT) != Keyboard.isKeyDown(GLFW_KEY_LEFT)) {
                    int directionX;

                    directionX = Keyboard.isKeyDown(GLFW_KEY_RIGHT) ? RIGHT : LEFT;
                    if (player.getDirectionX() == RIGHT && Keyboard.isKeyDown(GLFW_KEY_LEFT))
                        player.setDirectionX(LEFT);
                    else if (player.getDirectionX() == LEFT && Keyboard.isKeyDown(GLFW_KEY_RIGHT))
                        player.setDirectionX(RIGHT);
                    else
                        player.setDirectionX(directionX);
                    if (model.getCurrentLevel().checkCollisionX(player.getX() + (player.isClimbing() || player.isOnJetpack() ? Player.SPEED_FAST : Player.SPEED_SLOW) * directionX, player.getY()) == 0) {
                        if (player.isClimbing() || player.isOnJetpack())
                            player.setSpeedX(Player.SPEED_FAST * directionX);
                        else if (player.isJumping() || player.isFalling())
                            player.setSpeedX(Player.JUMP_SPEED_X * directionX);
                        else
                            player.setSpeedX(Player.SPEED_SLOW * directionX);
                        //player.setSpeedX((player.isClimbing() || player.isOnJetpack() || player.isJumping() || player.isFalling() ? Player.SPEED_FAST : Player.SPEED_SLOW) * directionX);
                    }
                }
                update();
            } else {
                if (player.getDeadCounter() == 0) {
                    if (player.getLives() > 0) {
                        player.setLocation(new PointD(model.getCurrentLevel().getSpawnpoint().x * 32 + 16, model.getCurrentLevel().getSpawnpoint().y * 32 + (32 - Player.HEIGHT / 2)));
                        player.restart();
                        update();
                    } else
                        model.reset();
                } else
                    update();
            }
        }
    }

    public void update() {
        Level currentLevel;

        currentLevel = model.getCurrentLevel();
        player.updateFigureNumber();
        figureNumber = (figureNumber + 1) % MAX_FIGURE_NUMBER;
        //SPARO
        if (player.getShoot().isVisible())
            if (model.getCurrentLevel().checkPureCollision(player.getShoot().getX() + player.getShoot().getDirection() * 8, player.getShoot().getY()))
                player.getShoot().setDirection(0);
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
                entity.update();
            }
        }
        if (player.isAlive()) {
            //GRAVITA'
            if (!player.isOnJetpack() && !player.isClimbing()) {
                double speedToSet;

                speedToSet = player.getSpeedY() + Player.GRAVITY < Player.GRAVITY_MAX ? player.getSpeedY() + Player.GRAVITY : Player.GRAVITY_MAX;
                if (player.isFalling() && model.getCurrentLevel().checkCollisionY(player.getX(), player.getY() + speedToSet) == DOWN) {
                    player.setY(player.getY() + (32 - (player.getY() % 32 + Player.HEIGHT / 2)) % 32);
                    player.setSpeedY(0);
                    player.setDirectionX(0);
                }
                if (player.isJumping() && model.getCurrentLevel().checkCollisionY(player.getX(), player.getY() + speedToSet) == UP) {
                    player.setSpeedY(Player.GRAVITY_MAX);
                }
                if (model.getCurrentLevel().checkCollisionY(player.getX(), player.getY() + speedToSet) == 0)
                    player.setSpeedY(speedToSet);
            }
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
            if (player.checkCollisionWithEntity(currentLevel.getEntity((int) player.getX() / 32, (int) player.getY() / 32 + 1)) &&
                    !currentLevel.getEntity((int) player.getX() / 32, (int) player.getY() / 32 + 1).isMortal())
                currentLevel.clearEntity((int) player.getX() / 32, (int) player.getY() / 32 + 1);
            if (player.checkCollisionWithEntity(currentLevel.getEntity((int) player.getX() / 32 + 1, (int) player.getY() / 32)) &&
                    !currentLevel.getEntity((int) player.getX() / 32 + 1, (int) player.getY() / 32).isMortal())
                currentLevel.clearEntity((int) player.getX() / 32, (int) player.getY() / 32 + 1);
            if (player.checkCollisionWithEntity(currentLevel.getEntity((int) player.getX() / 32, (int) player.getY() / 32 - 1)) &&
                    !currentLevel.getEntity((int) player.getX() / 32, (int) player.getY() / 32 - 1).isMortal())
                currentLevel.clearEntity((int) player.getX() / 32, (int) player.getY() / 32 + 1);
            if (player.checkCollisionWithEntity(currentLevel.getEntity((int) player.getX() / 32 - 1, (int) player.getY() / 32)) &&
                    !currentLevel.getEntity((int) player.getX() / 32 - 1, (int) player.getY() / 32).isMortal())
                currentLevel.clearEntity((int) player.getX() / 32, (int) player.getY() / 32 + 1);
            if (player.hasPassedLevel()) {
                if (model.getCurrentLevel().getNext() != null)
                    model.nextLevel();
                else
                    model.reset();
            }
        }
    }
}