package scenarios;

import game.Model;
import ui.Keyboard;
import utils.Textures;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.opengl.GL11.*;

public class ScenarioMenu extends Scenario {
    private double figureNumber;
    private boolean drawText;

    public ScenarioMenu(Model model, Textures textures) {
        this.model = model;
        this.textures = textures;
        figureNumber = 0;
        drawText = true;
    }

    @Override
    public void commands(double deltaT) {
        if (Keyboard.isKeyDown(GLFW_KEY_SPACE))
            model.start();
    }

    @Override
    public void collisions(double deltaT) {
    }

    @Override
    public void update(double deltaT) {
        figureNumber += deltaT * 60;
        if (figureNumber >= 16) {
            figureNumber -= 16;
            drawText = !drawText;
        }
    }

    @Override
    public void render(double deltaT) {
        int figureNumber = (int) this.figureNumber;
        //LOGO
        Textures.bindTexture(textures.getTextureGameParts());
        glBegin(GL_QUADS);
        glTexCoord2d((((double) (figureNumber / 4) * 224d) / 896), 0);
        glVertex2d((96d / 640), (20d / 400));
        glTexCoord2d((((double) (figureNumber / 4) * 224d + 224d) / 896), 0);
        glVertex2d((544d / 640), (20d / 400));
        glTexCoord2d((((double) (figureNumber / 4) * 224d + 224d) / 896), (94d / 290));
        glVertex2d((544d / 640), (208d / 400));
        glTexCoord2d((((double) (figureNumber / 4) * 224d) / 896), (94d / 290));
        glVertex2d((96d / 640), (208d / 400));
        glEnd();
        //SCRITTA
        if (drawText) {
            Textures.bindTexture(textures.getTextureGameParts());
            glBegin(GL_QUADS);
            glTexCoord2d((126d / 896), (114d / 290));
            glVertex2d((153d / 640), (280d / 400));
            glTexCoord2d((460d / 896), (114d / 290));
            glVertex2d((487d / 640), (280d / 400));
            glTexCoord2d((460d / 896), (136d / 290));
            glVertex2d((487d / 640), (302d / 400));
            glTexCoord2d((126d / 896), (136d / 290));
            glVertex2d((153d / 640), (302d / 400));
            glEnd();
        }
    }
}